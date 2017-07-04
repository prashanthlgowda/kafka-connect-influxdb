/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.influxdb.InfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfluxDBSinkConnector extends SinkConnector {
  private static final Logger log = LoggerFactory.getLogger(InfluxDBSinkConnector.class);

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  Map<String, String> settings;
  InfluxDBFactory factory = new InfluxDBFactoryImpl();

  @Override
  public void start(Map<String, String> settings) {
    InfluxDBSinkConnectorConfig config = new InfluxDBSinkConnectorConfig(settings);
    this.settings = settings;

    InfluxDB influxDB = this.factory.create(config);

    List<String> databases = influxDB.describeDatabases();
    if (log.isTraceEnabled()) {
      log.trace("start() - existing databases = '{}'", Joiner.on(", ").join(databases));
    }

    if (!databases.contains(config.database)) {
      log.info("start() - Database '{}' was not found. Creating...", config.database);
      influxDB.createDatabase(config.database);
    }
    this.settings = settings;
  }

  @Override
  public Class<? extends Task> taskClass() {
    return InfluxDBSinkTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int taskConfigs) {
    Preconditions.checkState(taskConfigs > 0, "taskConfigs must be greater than 0.");
    List<Map<String, String>> result = new ArrayList<>(taskConfigs);

    for (int i = 0; i < taskConfigs; i++) {
      result.add(this.settings);
    }

    return result;
  }

  @Override
  public void stop() {

  }

  @Override
  public ConfigDef config() {
    return InfluxDBSinkConnectorConfig.config();
  }
}
