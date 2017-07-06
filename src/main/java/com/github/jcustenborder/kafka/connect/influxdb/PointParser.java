/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.influxdb;

import com.github.jcustenborder.kafka.connect.utils.data.Parser;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PointParser {
  static final Schema MEASUREMENT_SCHEMA = SchemaBuilder.string()
      .doc("The name of the measurement.")
      .build();

  static final Schema TAGS_SCHEMA = SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA)
      .doc("Tags associated with the measurement.")
      .build();

  static final Schema KEY_SCHEMA = SchemaBuilder.struct()
      .name("com.github.jcustenborder.kafka.connect.influxdb.PointKey")
      .doc("")
      .field("measurement", MEASUREMENT_SCHEMA)
      .field("tags", TAGS_SCHEMA)
      .build();

  static final Schema POINT_VALUE_SCHEMA = SchemaBuilder.struct()
      .name("com.github.jcustenborder.kafka.connect.influxdb.PointValue")
      .field("name", Schema.STRING_SCHEMA)
      .field("type", Schema.STRING_SCHEMA)
      .field("float64", Schema.OPTIONAL_FLOAT64_SCHEMA)
      .field("int64", Schema.OPTIONAL_INT64_SCHEMA)
      .field("boolean", Schema.OPTIONAL_BOOLEAN_SCHEMA)
      .field("timestamp", Timestamp.builder().optional().build())
      .field("string", Schema.OPTIONAL_STRING_SCHEMA)
      .build();

  static final Schema POINT_VALUE_ARRAY_SCHEMA = SchemaBuilder.array(POINT_VALUE_SCHEMA)
      .doc("")
      .build();

  static final Schema VALUE_SCHEMA = SchemaBuilder.struct()
      .name("com.github.jcustenborder.kafka.connect.influxdb.Point")
      .doc("")
      .field("measurement", MEASUREMENT_SCHEMA)
      .field("tags", TAGS_SCHEMA)
      .field("timestampNano", SchemaBuilder.int64().doc("Timestamp in nanosecond precision.").build())
      .field("timestamp", Timestamp.builder().doc("Timestamp of the message.").build())
      .field("values", POINT_VALUE_ARRAY_SCHEMA)
      .build();


  final InfluxDBSourceConnectorConfig config;
  Time time = new SystemTime();
  private static final Logger log = LoggerFactory.getLogger(PointParser.class);

  static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile(",");
  static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^([^=]+)=(.+)$");

  PointParser(InfluxDBSourceConnectorConfig config) {
    this.config = config;
  }

  static Map<String, String> tags(String tagsRaw) {
    log.trace("tags() - tagsRaw='{}'", tagsRaw);
    Map<String, String> result = new LinkedHashMap<>(50);

    if (Strings.isNullOrEmpty(tagsRaw)) {
      return result;
    }

    final String[] parts = COMMA_SPLIT_PATTERN.split(tagsRaw);
    for (final String part : parts) {
      if (Strings.isNullOrEmpty(part)) {
        continue;
      }
      Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(part);
      Preconditions.checkState(keyValueMatcher.matches(), "Could not match '%s' with '%s'", part, KEY_VALUE_PATTERN.pattern());
      final String key = keyValueMatcher.group(1);
      final String value = keyValueMatcher.group(2);
      result.put(key, value);
    }

    return ImmutableMap.copyOf(result);
  }

  static final Map<String, Schema> VALUE_SCHEMAS = ImmutableMap.of(
      "float64", Schema.FLOAT64_SCHEMA,
      "int64", Schema.INT64_SCHEMA,
      "boolean", Schema.BOOLEAN_SCHEMA,
      "timestamp", Timestamp.SCHEMA,
      "string", Schema.STRING_SCHEMA
  );

  static Parser parser = new Parser();

  static List<Struct> values(String valuesRaw) {
    log.trace("values() - valuesRaw='{}'", valuesRaw);

    List<Struct> result = new ArrayList<>(50);

    if (null == valuesRaw || valuesRaw.isEmpty()) {
      return result;
    }

    final String[] parts = COMMA_SPLIT_PATTERN.split(valuesRaw);
    for (final String part : parts) {
      Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(part);
      Preconditions.checkState(keyValueMatcher.matches(), "Could not match '%s' with '%s'", part, KEY_VALUE_PATTERN.pattern());
      final String key = keyValueMatcher.group(1);
      final String valueRaw = keyValueMatcher.group(2);

      Struct value = null;
      for (Map.Entry<String, Schema> kvp : VALUE_SCHEMAS.entrySet()) {
        try {
          final Object parsedValue = parser.parseString(kvp.getValue(), valueRaw);
          log.trace("values() - Parsed '{}' to '{}'", valueRaw, parsedValue);
          value = new Struct(POINT_VALUE_SCHEMA)
              .put("name", key)
              .put("type", kvp.getKey())
              .put(kvp.getKey(), parsedValue);
          break;
        } catch (DataException ex) {
          log.trace("values() - Exception thrown while parsing '{}'", valueRaw, ex);
        }
      }
      Preconditions.checkState(null != value, "Could not parse '%s'", valueRaw);
      result.add(value);
    }

    return ImmutableList.copyOf(result);

  }

  static final Pattern MEASUREMENT_PATTERN = Pattern.compile("^([^,]+)(?:(,\\S+))?\\s+(\\S+)(?:\\s+(\\d+))?$");

  static final Map<String, ?> EMPTY_PARTITION = ImmutableMap.of();
  static final Map<String, ?> EMPTY_OFFSET = ImmutableMap.of();

  final Map<String, String> topics = new HashMap<>();

  public List<SourceRecord> parse(String database, BufferedReader reader) throws IOException {
    List<SourceRecord> results = new ArrayList<>(512);

    String line;

    while ((line = reader.readLine()) != null) {
      log.trace("parse() - processing '{}'", line);
      Matcher matcher = MEASUREMENT_PATTERN.matcher(line);
      Preconditions.checkState(matcher.matches(), "Could not match '%s' with '%s'", line, matcher.pattern());

      final String measurement = matcher.group(1);
      final String tagsRaw = matcher.group(2);
      final String valuesRaw = matcher.group(3);
      final String timestampRaw = matcher.group(4);
      final long timestamp;
      final long nanoTimestamp;

      final Map<String, String> tags = tags(tagsRaw);
      final List<Struct> values = values(valuesRaw);

      if (null == timestampRaw) {
        nanoTimestamp = this.time.nanoseconds();
      } else {
        log.trace("parse() - Parsing {} to long.", timestampRaw);
        nanoTimestamp = Long.parseLong(timestampRaw);
      }

      timestamp = TimeUnit.NANOSECONDS.convert(nanoTimestamp, TimeUnit.MILLISECONDS);

      final Struct key = new Struct(KEY_SCHEMA)
          .put("measurement", measurement)
          .put("tags", tags);

      final Struct value = new Struct(VALUE_SCHEMA)
          .put("measurement", measurement)
          .put("tags", tags)
          .put("timestampNano", nanoTimestamp)
          .put("timestamp", new Date(timestamp))
          .put("values", values);

      final String topic = this.topics.computeIfAbsent(database, s -> String.format("%s%s", this.config.topicPrefix, database));

      final SourceRecord record = new SourceRecord(
          EMPTY_PARTITION,
          EMPTY_OFFSET,
          topic,
          null,
          key.schema(),
          key,
          value.schema(),
          value,
          timestamp
      );
      results.add(record);
    }

    return results;
  }

}
