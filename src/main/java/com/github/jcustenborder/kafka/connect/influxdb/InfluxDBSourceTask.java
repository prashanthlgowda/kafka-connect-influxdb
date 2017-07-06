package com.github.jcustenborder.kafka.connect.influxdb;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.webhook.BaseWebHookTask;
import com.google.inject.servlet.ServletModule;

import java.util.Map;

public class InfluxDBSourceTask extends BaseWebHookTask<InfluxDBSourceConnectorConfig> {

  @Override
  protected InfluxDBSourceConnectorConfig config(Map<String, String> settings) {
    return new InfluxDBSourceConnectorConfig(settings);
  }

  @Override
  protected ServletModule servletModule() {
    return new ServletModule() {
      @Override
      protected void configureServlets() {
        bind(InfluxDBSourceConnectorConfig.class).toInstance(config);
        serve("/write").with(InfluxDBServlet.class);
      }
    };
  }

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }
}
