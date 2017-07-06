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

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InfluxDBSinkTask extends SinkTask {
  private static final Logger log = LoggerFactory.getLogger(InfluxDBSinkTask.class);
  InfluxDBSinkConnectorConfig config;
  InfluxDBFactory factory = new InfluxDBFactoryImpl();
  InfluxDB influxDB;

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  @Override
  public void start(Map<String, String> settings) {
    this.config = new InfluxDBSinkConnectorConfig(settings);
    this.influxDB = this.factory.create(this.config);


  }

  static final Schema TAG_SCHEMA = SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA).build();
  static final Schema TAG_OPTIONAL_SCHEMA = SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA).optional().build();
  static final Set<String> SKIP_FIELDS = ImmutableSet.of("measurement", "tags");
  static final Set<String> INCLUDE_LOGICAL = ImmutableSet.of(
      Decimal.LOGICAL_NAME
  );

  static final Set<Schema.Type> INCLUDE_TYPES = ImmutableSet.of(
      Schema.Type.FLOAT32,
      Schema.Type.FLOAT64,
      Schema.Type.INT8,
      Schema.Type.INT16,
      Schema.Type.INT32,
      Schema.Type.INT64,
      Schema.Type.STRING,
      Schema.Type.BOOLEAN
  );

  @Override
  public void put(Collection<SinkRecord> records) {
    if (null == records || records.isEmpty()) {
      return;
    }

    log.trace(
        "put() - Creating new batch. expectedKeys = {} expectedValues = {}",
        this.config.expectedKeys,
        this.config.expectedValues
    );

    Map<PointKey, Map<String, Object>> builders = new HashMap<>(records.size());


    for (SinkRecord record : records) {
      final Struct value = (Struct) record.value();

      String measurement = value.getString("measurement");

      if (Strings.isNullOrEmpty("measurement")) {
        throw new DataException("measurement is a required field.");
      }

      final Map<String, String> tags;
      Field tagField = value.schema().field("tags");
      log.trace("put() - Looking for tags");
      if (null == tagField) {
        log.trace("put() - tags field not found.");
        tags = ImmutableMap.of();
      } else if (TAG_SCHEMA.equals(tagField.schema()) || TAG_OPTIONAL_SCHEMA.equals(tagField.schema())) {
        log.trace("put() - tags field found.");
        final Map<String, String> t = value.getMap(tagField.name());
        if (null != t) {
          tags = ImmutableMap.copyOf(t);
        } else {
          tags = ImmutableMap.of();
        }
      } else {
        log.trace("put() - tags field found but doesn't match {} or {}.", TAG_SCHEMA, TAG_OPTIONAL_SCHEMA);
        tags = ImmutableMap.of();
      }

      if (log.isTraceEnabled()) {
        log.trace("put() - tags = {}", Joiner.on(", ").withKeyValueSeparator("=").join(tags));
      }

      final long time = record.timestamp();

      PointKey key = PointKey.of(measurement, time, tags);
      Map<String, Object> fields = builders.computeIfAbsent(key, pointKey -> new HashMap<>(100));


      for (Field field : value.schema().fields()) {
        if (SKIP_FIELDS.contains(field.name())) {
          log.trace("put() - Skipping field '{}'", field.name());
          continue;
        }
        log.trace("put() - Processing field '{}':{}:'{}'", field.name(), field.schema().type(), field.schema().name());

        if (INCLUDE_LOGICAL.contains(field.schema().name()) || INCLUDE_TYPES.contains(field.schema().type())) {
          final Object fieldValue = value.get(field);
          if (null != fieldValue) {
            fields.put(field.name(), fieldValue);
          }
        } else {
          log.trace("put() - Ignoring field '{}':{}:'{}'", field.name(), field.schema().type(), field.schema().name());
        }
      }
    }

    BatchPoints.Builder batchBuilder = BatchPoints.database(this.config.database)
        .consistency(this.config.consistencyLevel);

    for (Map.Entry<PointKey, Map<String, Object>> values : builders.entrySet()) {
      final Point.Builder builder = Point.measurement(values.getKey().measurement);
      builder.time(values.getKey().time, this.config.precision);
      if (null != values.getKey().tags || values.getKey().tags.isEmpty()) {
        builder.tag(values.getKey().tags);
      }
      builder.fields(values.getValue());
      Point point = builder.build();
      if (log.isTraceEnabled()) {
        log.trace("put() - Adding point {}", point.toString());
      }
      batchBuilder.point(point);
    }

    BatchPoints batch = batchBuilder.build();
    this.influxDB.write(batch);
  }


  @Override
  public void stop() {
    if (null != this.influxDB) {
      log.info("stop() - Closing InfluxDB client.");
      this.influxDB.close();
    }
  }
}
