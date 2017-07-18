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

import org.influxdb.InfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InfluxDBFactoryImpl implements InfluxDBFactory {
  private static final Logger log = LoggerFactory.getLogger(InfluxDBFactoryImpl.class);

  @Override
  public InfluxDB create(InfluxDBSinkConnectorConfig config) {
    final InfluxDB result;
    if (config.authentication) {
      log.info("Authenticating to {} as {}", config.url, config.username);
      result = org.influxdb.InfluxDBFactory.connect(config.url, config.username, config.password);
    } else {
      log.info("Connecting to {}", config.url);
      result = org.influxdb.InfluxDBFactory.connect(config.url);
    }
    if (config.gzipEnable) {
      result.enableGzip();
    }
    return result;
  }
}
