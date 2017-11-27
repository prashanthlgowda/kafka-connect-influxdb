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

import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordConcurrentLinkedDeque;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
class InfluxDBServlet extends HttpServlet {
  private static final Logger log = LoggerFactory.getLogger(InfluxDBServlet.class);

  @Inject
  InfluxDBSourceConnectorConfig config;

  @Inject
  SourceRecordConcurrentLinkedDeque records;

  static Map<String, String> queryStringParameters(HttpServletRequest request) {
    log.trace("queryStringParameters('{}')", request.getQueryString());
    Map<String, String> result = new LinkedHashMap<>();

    if (Strings.isNullOrEmpty(request.getQueryString())) {
      log.trace("queryStringParameters('{}') - null or blank querystring, returning empty result.", request.getQueryString());
      return result;
    }
    final String[] parts = request.getQueryString().split("&");
    for (final String part : parts) {
      log.trace("queryStringParameters('{}') - processing '{}'.", request.getQueryString(), part);
      Pattern pattern = Pattern.compile("^(.+)=(.+)");
      Matcher matcher = pattern.matcher(part);
      Preconditions.checkState(matcher.matches(), "Could not match '%s' with pattern '%s'", part, pattern.pattern());

      final String key = matcher.group(1);
      final String value = matcher.group(2);
      log.trace("queryStringParameters('{}') - processing '{}' key = '{}' value='{}'.", request.getQueryString(), part, key, value);

      final String valueDecoded;
      try {
        valueDecoded = URLDecoder.decode(value, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException("Could not decode '" + value + "'", e);
      }

      result.put(key, valueDecoded);
    }

    return result;
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    final String database = request.getParameter("database");
    log.trace("doPost() - database = '{}'", database);
    if (!this.config.allowedDatabases.isEmpty() && !this.config.allowedDatabases.contains(database)) {
      log.warn("doPost() - database '{}' is not allowed.", database);
      response.setStatus(404);
      try (PrintWriter writer = response.getWriter()) {
        writer.printf("{\"error\":\"database not found: \\\"%s\\\"\"}", database);
      }
      return;
    }

    PointParser parser = new PointParser(this.config);

    try (InputStream inputStream = request.getInputStream()) {
      try (Reader reader = new InputStreamReader(inputStream)) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
          final List<SourceRecord> points = parser.parse(database, bufferedReader);
          this.records.addAll(points);
        }
      }
    }
    response.setStatus(204);
  }
}
