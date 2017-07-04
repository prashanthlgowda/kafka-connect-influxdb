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

import com.github.jcustenborder.kafka.connect.utils.config.ConfigUtils;
import com.github.jcustenborder.kafka.connect.utils.config.ValidEnum;
import com.google.common.base.Strings;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.influxdb.InfluxDB;

import java.util.Map;
import java.util.concurrent.TimeUnit;

class InfluxDBSinkConnectorConfig extends AbstractConfig {

  public static final String DATABASE_CONF = "influxdb.database";
  static final String DATABASE_DOC = "The influxdb database to write to.";
  public static final String CONSISTENCY_LEVEL_CONF = "influxdb.consistency.level";
  static final String CONSISTENCY_LEVEL_DOC = "influxdb.consistency.level";
  public static final String TIMEUNIT_CONF = "influxdb.timeunit";
  static final String TIMEUNIT_DOC = "influxdb.timeunit";
  public static final String LOG_LEVEL_CONF = "influxdb.log.level";
  static final String LOG_LEVEL_DOC = "influxdb.log.level";
  public static final String EXPECTED_KEYS_CONF = "influxdb.expected.keys";
  static final String EXPECTED_KEYS_DOC = "influxdb.expected.keys";
  public static final String EXPECTED_VALUES_CONF = "influxdb.expected.values";
  static final String EXPECTED_VALUES_DOC = "influxdb.expected.values";

  public static final String URL_CONF = "influxdb.url";
  static final String URL_DOC = "influxdb.url";

  public static final String USERNAME_CONF = "influxdb.username";
  static final String USERNAME_DOC = "influxdb.username";

  public static final String PASSWORD_CONF = "influxdb.password";
  static final String PASSWORD_DOC = "influxdb.password";

  public static final String GZIP_ENABLE_CONF = "influxdb.gzip.enable";
  static final String GZIP_ENABLE_DOC = "influxdb.gzip.enable";

  public final String database;
  public final InfluxDB.ConsistencyLevel consistencyLevel;
  public final TimeUnit precision;
  public final int expectedKeys;
  public final int expectedValues;
  public final InfluxDB.LogLevel logLevel;
  public final String url;
  public final String username;
  public final String password;
  public final boolean authentication;
  public final boolean gzipEnable;

  public InfluxDBSinkConnectorConfig(Map<String, String> settings) {
    super(config(), settings);

    this.database = getString(DATABASE_CONF);
    this.consistencyLevel = ConfigUtils.getEnum(InfluxDB.ConsistencyLevel.class, this, CONSISTENCY_LEVEL_CONF);
    this.precision = ConfigUtils.getEnum(TimeUnit.class, this, TIMEUNIT_CONF);
    this.expectedKeys = getInt(EXPECTED_KEYS_CONF);
    this.expectedValues = getInt(EXPECTED_VALUES_CONF);
    this.logLevel = ConfigUtils.getEnum(InfluxDB.LogLevel.class, this, LOG_LEVEL_CONF);

    this.url = getString(URL_CONF);
    this.username = getString(USERNAME_CONF);
    this.authentication = !Strings.isNullOrEmpty(this.username);
    this.password = getPassword(PASSWORD_CONF).value();
    this.gzipEnable = getBoolean(GZIP_ENABLE_CONF);
  }


  public static ConfigDef config() {
    return new ConfigDef()
        .define(URL_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, URL_DOC)
        .define(DATABASE_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, DATABASE_DOC)
        .define(USERNAME_CONF, ConfigDef.Type.STRING, "", ConfigDef.Importance.HIGH, USERNAME_DOC)
        .define(PASSWORD_CONF, ConfigDef.Type.PASSWORD, "", ConfigDef.Importance.HIGH, PASSWORD_DOC)
        .define(CONSISTENCY_LEVEL_CONF, ConfigDef.Type.STRING, InfluxDB.ConsistencyLevel.ONE.toString(), ValidEnum.of(InfluxDB.ConsistencyLevel.class), ConfigDef.Importance.MEDIUM, CONSISTENCY_LEVEL_DOC)
        .define(TIMEUNIT_CONF, ConfigDef.Type.STRING, TimeUnit.MILLISECONDS.toString(), ValidEnum.of(TimeUnit.class), ConfigDef.Importance.MEDIUM, TIMEUNIT_DOC)
        .define(EXPECTED_KEYS_CONF, ConfigDef.Type.INT, 100, ConfigDef.Range.atLeast(1), ConfigDef.Importance.LOW, EXPECTED_KEYS_DOC)
        .define(EXPECTED_VALUES_CONF, ConfigDef.Type.INT, 1000, ConfigDef.Range.atLeast(1), ConfigDef.Importance.LOW, EXPECTED_VALUES_DOC)
        .define(LOG_LEVEL_CONF, ConfigDef.Type.STRING, InfluxDB.LogLevel.NONE.toString(), ValidEnum.of(InfluxDB.LogLevel.class), ConfigDef.Importance.LOW, LOG_LEVEL_DOC)
        .define(GZIP_ENABLE_CONF, ConfigDef.Type.BOOLEAN, true, ConfigDef.Importance.LOW, GZIP_ENABLE_DOC);
  }
}
