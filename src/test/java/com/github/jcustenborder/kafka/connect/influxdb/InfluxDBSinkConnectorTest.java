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

import com.google.common.collect.ImmutableList;
import org.influxdb.InfluxDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InfluxDBSinkConnectorTest {
  InfluxDBSinkConnector connector;
  InfluxDB influxDB;

  @BeforeEach
  public void beforeEach() {
    this.connector = new InfluxDBSinkConnector();
    this.connector.factory = mock(InfluxDBFactory.class);
    this.influxDB = mock(InfluxDB.class);
    when(this.connector.factory.create(any())).thenReturn(this.influxDB);
  }

  @Test
  public void startCreatesDatabase() {
    when(this.influxDB.describeDatabases()).thenReturn(ImmutableList.of());
    this.connector.start(InfluxDBSinkConnectorConfigTest.settings());
    verify(this.influxDB, times(1)).describeDatabases();
    verify(this.influxDB, times(1)).createDatabase(InfluxDBSinkConnectorConfigTest.DATABASE);
  }

  @Test
  public void startDatabaseExists() {
    when(this.influxDB.describeDatabases()).thenReturn(ImmutableList.of(InfluxDBSinkConnectorConfigTest.DATABASE));
    this.connector.start(InfluxDBSinkConnectorConfigTest.settings());
    verify(this.influxDB, times(1)).describeDatabases();
    verify(this.influxDB, never()).createDatabase(InfluxDBSinkConnectorConfigTest.DATABASE);
  }
}
