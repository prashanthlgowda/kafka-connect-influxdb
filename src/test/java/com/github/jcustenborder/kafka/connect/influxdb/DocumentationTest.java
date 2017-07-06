package com.github.jcustenborder.kafka.connect.influxdb;

import com.github.jcustenborder.kafka.connect.utils.BaseDocumentationTest;
import org.apache.kafka.connect.data.Schema;

import java.util.Arrays;
import java.util.List;

public class DocumentationTest extends BaseDocumentationTest {
  @Override
  protected String[] packages() {
    return new String[]{this.getClass().getPackage().getName()};
  }

  @Override
  protected List<Schema> schemas() {
    return Arrays.asList(PointParser.KEY_SCHEMA, PointParser.VALUE_SCHEMA, PointParser.POINT_VALUE_SCHEMA);
  }
}
