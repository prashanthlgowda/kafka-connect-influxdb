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
