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
