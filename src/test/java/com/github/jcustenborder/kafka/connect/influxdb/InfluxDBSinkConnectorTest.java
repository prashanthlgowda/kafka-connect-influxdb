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
