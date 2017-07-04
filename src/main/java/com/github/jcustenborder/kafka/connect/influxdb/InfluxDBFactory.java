package com.github.jcustenborder.kafka.connect.influxdb;

import org.influxdb.InfluxDB;

interface InfluxDBFactory {
  InfluxDB create(InfluxDBSinkConnectorConfig config);
}
