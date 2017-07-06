package com.github.jcustenborder.kafka.connect.influxdb;

import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class PointParserTest {

  InfluxDBSourceConnectorConfig config;

  @BeforeEach
  public void beforeEach() {
    this.config = new InfluxDBSourceConnectorConfig(InfluxDBSourceConnectorConfigTest.settings());
  }

  @Test
  public void foo() throws IOException {
    String input = "cpu_load_short,host=server02 value=0.67\n" +
        "cpu_load_short,host=server02,region=us-west value=0.55 1422568543702900257\n" +
        "cpu_load_short,direction=in,host=server01,region=us-west value=2.0 1422568543702900257\n" +
        "liters value=10";

    PointParser parser = new PointParser(config);

    final List<SourceRecord> result;
    try (StringReader reader = new StringReader(input)) {
      try (BufferedReader bufferedReader = new BufferedReader(reader)) {
        result = parser.parse("test", bufferedReader);
      }
    }


  }


}
