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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InfluxDBServletTest {

  InfluxDBServlet servlet;

  @BeforeEach
  public void beforeEach() {
    this.servlet = new InfluxDBServlet();
  }

  @TestFactory
  public Stream<DynamicTest> queryStringParameters() {
    Map<String, Map<String, String>> tests = new LinkedHashMap<>();
    tests.put("", ImmutableMap.of());
    tests.put(null, ImmutableMap.of());
    tests.put("db=testing", ImmutableMap.of("db", "testing"));
    tests.put("db=testing&foo=bar", ImmutableMap.of("db", "testing", "foo", "bar"));
    tests.put("encoded=this%20is%20text%20that%20should%20be%20encoded&foo=bar", ImmutableMap.of("encoded", "this is text that should be encoded", "foo", "bar"));

    return tests.entrySet().stream().map(entry -> dynamicTest(String.format("input: '%s'", entry.getKey()), () -> {
      final Map<String, String> expected = entry.getValue();
      final HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getQueryString()).thenReturn(entry.getKey());
      final Map<String, String> actual = InfluxDBServlet.queryStringParameters(request);
      assertNotNull(actual, "actual should never be null");
      MapDifference<String, String> difference = Maps.difference(expected, actual);
      assertTrue(difference.areEqual(), "maps should not be different.");
    }));

  }

}
