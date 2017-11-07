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

import com.github.jcustenborder.kafka.connect.servlet.BaseWebHookConnectorConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;
import java.util.Set;

class InfluxDBSourceConnectorConfig extends BaseWebHookConnectorConfig {

  public static final String TOPIC_PREFIX_CONF = "topic.prefix";
  static final String TOPIC_PREFIX_DOC = "";

  public static final String ALLOWED_DATABASES_CONF = "allowed.databases";
  static final String ALLOWED_DATABASES_DOC = "";

  public final String topicPrefix;
  public final Set<String> allowedDatabases;

  public InfluxDBSourceConnectorConfig(Map<String, String> settings) {
    super(config(), settings);

    this.topicPrefix = this.getString(TOPIC_PREFIX_CONF);
    this.allowedDatabases = ImmutableSet.copyOf(getList(ALLOWED_DATABASES_CONF));
  }

  public static ConfigDef config() {
    return BaseWebHookConnectorConfig.config()
        .define(TOPIC_PREFIX_CONF, ConfigDef.Type.STRING, "influxdb.", ConfigDef.Importance.HIGH, TOPIC_PREFIX_DOC)
        .define(ALLOWED_DATABASES_CONF, ConfigDef.Type.LIST, ImmutableList.of(), ConfigDef.Importance.MEDIUM, ALLOWED_DATABASES_DOC);
  }
}
