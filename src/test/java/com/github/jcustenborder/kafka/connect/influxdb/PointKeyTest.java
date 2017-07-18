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
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PointKeyTest {

  @Test
  public void equal() {
    final String messurement = "test";
    final long time = 123412341234L;
    final Map<String, String> tags = ImmutableMap.of("first", "one", "second", "two");

    final PointKey expected = PointKey.of(messurement, time, tags);
    final PointKey actual = PointKey.of(messurement, time, tags);
    assertEquals(expected, actual);
  }

  @Test
  public void different() {
    final String messurement = "test";
    final long time = 123412341234L;

    final PointKey expected = PointKey.of(messurement, time, ImmutableMap.of("first", "one", "second", "two"));
    final PointKey actual = PointKey.of(messurement, time, ImmutableMap.of("second", "two"));
    assertNotEquals(expected, actual);
  }

}
