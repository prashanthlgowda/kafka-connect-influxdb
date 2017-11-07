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

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.servlet.BaseWebHookTask;
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
