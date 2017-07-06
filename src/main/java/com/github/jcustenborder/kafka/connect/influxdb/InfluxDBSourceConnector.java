package com.github.jcustenborder.kafka.connect.influxdb;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@DocumentationImportant("This connector listens on a network port. Running more than one task or running in distributed " +
    "mode can cause some undesired effects if another task already has the port open. It is recommended that you run this " +
    "connector in :term:`Standalone Mode`.")
@Description("The InfluxDBSourceConnector is used to emulate an InfluxDB host and receive messages written by an InfluxDB client.")
public class InfluxDBSourceConnector extends SourceConnector {
  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  Map<String, String> settings;

  @Override
  public void start(Map<String, String> settings) {
    InfluxDBSourceConnectorConfig config = new InfluxDBSourceConnectorConfig(settings);
    this.settings = settings;
  }

  @Override
  public Class<? extends Task> taskClass() {
    return InfluxDBSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int i) {
    return Arrays.asList(this.settings);
  }

  @Override
  public void stop() {

  }

  @Override
  public ConfigDef config() {
    return InfluxDBSourceConnectorConfig.config();
  }
}
