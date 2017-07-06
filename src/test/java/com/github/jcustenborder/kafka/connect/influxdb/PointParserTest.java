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
