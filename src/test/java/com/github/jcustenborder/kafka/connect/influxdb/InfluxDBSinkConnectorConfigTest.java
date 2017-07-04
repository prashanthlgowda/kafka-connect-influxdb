package com.github.jcustenborder.kafka.connect.influxdb;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class InfluxDBSinkConnectorConfigTest {
  public static final String DATABASE = "test";
  public static final String URL = "http://localhost:8086/";

  public static Map<String, String> settings() {
    return ImmutableMap.of(
        InfluxDBSinkConnectorConfig.DATABASE_CONF, DATABASE,
        InfluxDBSinkConnectorConfig.URL_CONF, URL,
        InfluxDBSinkConnectorConfig.GZIP_ENABLE_CONF, "true"
    );
  }


}
