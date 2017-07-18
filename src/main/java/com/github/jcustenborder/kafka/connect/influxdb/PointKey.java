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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

class PointKey implements Comparable<PointKey> {
  public final String measurement;
  public final long time;
  public final Map<String, String> tags;


  private PointKey(String measurement, long time, Map<String, String> tags) {
    this.measurement = measurement;
    this.tags = ImmutableMap.copyOf(tags);
    this.time = time;
  }

  public static PointKey of(String measurement, long time, Map<String, String> tags) {
    return new PointKey(measurement, time, tags);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("measurement", this.measurement)
        .add("time", this.time)
        .add("tags", this.tags)
        .toString();
  }

  @Override
  public int compareTo(PointKey that) {
    return ComparisonChain.start()
        .compare(this.measurement, that.measurement)
        .compare(this.time, that.time)
        .compare(Objects.hashCode(this.tags), Objects.hashCode(that.tags))
        .result();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.measurement, this.time, this.tags);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PointKey) {
      return 0 == compareTo((PointKey) obj);
    } else {
      return false;
    }
  }
}