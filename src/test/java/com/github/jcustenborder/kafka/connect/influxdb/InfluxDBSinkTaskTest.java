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

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InfluxDBSinkTaskTest {
  InfluxDBSinkTask task;
  InfluxDB influxDB;


  @BeforeEach
  public void beforeEach() {
    this.task = new InfluxDBSinkTask();
    this.task.factory = mock(InfluxDBFactory.class);
    this.influxDB = mock(InfluxDB.class);
    when(this.task.factory.create(any())).thenReturn(this.influxDB);
    this.task.start(InfluxDBSinkConnectorConfigTest.settings());
  }

  @Test
  public void poll() {
    final Schema schema = SchemaBuilder.struct()
        .field("measurement", Schema.STRING_SCHEMA)
        .field("tags", InfluxDBSinkTask.TAG_OPTIONAL_SCHEMA)
        .field("cpu0", Schema.OPTIONAL_INT32_SCHEMA)
        .field("cpu1", Schema.OPTIONAL_INT32_SCHEMA)
        .field("cpu2", Schema.OPTIONAL_INT32_SCHEMA)
        .field("cpu3", Schema.OPTIONAL_INT32_SCHEMA)
        .field("cpu4", Schema.OPTIONAL_INT32_SCHEMA)
        .field("cpu5", Schema.OPTIONAL_INT32_SCHEMA)
        .field("cpu6", Schema.OPTIONAL_INT32_SCHEMA)
        .field("cpu7", Schema.OPTIONAL_INT32_SCHEMA)
        .build();

    Random random = new Random();

    List<SinkRecord> records = new ArrayList<>();

    long timestamp = 1499180530123L;
    for (long i = 0; i < 8; i++) {
      int percentage = random.nextInt(100);
      Struct struct = new Struct(schema)
          .put("measurement", "cpu")
          .put("tags", ImmutableMap.of("host", "test"))
          .put("cpu" + i, percentage);

      SinkRecord record = new SinkRecord(
          "measurements",
          1,
          null,
          null,
          struct.schema(),
          struct,
          i,
          timestamp,
          TimestampType.CREATE_TIME
      );
      records.add(record);
    }

    doAnswer(invocationOnMock -> {
      BatchPoints batchPoints = invocationOnMock.getArgument(0);
      assertNotNull(batchPoints, "batchPoints should not be null.");
      return null;
    }).when(this.influxDB).write(any(BatchPoints.class));

    this.task.put(records);

//    this.influxDB.

    verify(this.influxDB, times(1)).write(any(BatchPoints.class));
  }

  @AfterEach
  public void afterEach() {
    this.task.stop();
    verify(this.influxDB, times(1)).close();
  }

}
