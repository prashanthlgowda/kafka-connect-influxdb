# Introduction

# Source Connectors

## InfluxDBSourceConnector

The InfluxDBSourceConnector is used to emulate an InfluxDB host and receive messages written by an InfluxDB client.

### Configuration

| Name                       | Type     | Importance | Default Value | Validator                        | Documentation                                                                    |
| -------------------------- | -------- | ---------- | ------------- | -------------------------------- | ---------------------------------------------------------------------------------|
| http.enable                | Boolean  | High       | true          |                                  | Flag to determine if http should be enabled.                                     |
| http.port                  | Int      | High       | 8080          | ValidPort{start=1000, end=65535} | Port the http listener should be started on.                                     |
| https.enable               | Boolean  | High       | false         |                                  | Flag to determine if https should be enabled.                                    |
| https.port                 | Int      | High       | 8443          | ValidPort{start=1000, end=65535} | Port the https listener should be started on.                                    |
| topic.prefix               | String   | High       | influxdb.     |                                  |                                                                                  |
| allowed.databases          | List     | Medium     | []            |                                  |                                                                                  |
| health.check.enable        | Boolean  | Medium     | true          |                                  | Flag to determine if a health check url for a load balancer should be configured.|
| health.check.path          | String   | Medium     | /healthcheck  |                                  | Path that will respond with a health check.                                      |
| https.key.manager.password | Password | Medium     | [hidden]      |                                  | The key manager password.                                                        |
| https.key.store.password   | Password | Medium     | [hidden]      |                                  | The password for the ssl keystore.                                               |
| https.key.store.path       | String   | Medium     |               |                                  | Path on the local filesystem that contains the ssl keystore.                     |
| https.trust.store.password | Password | Medium     | [hidden]      |                                  | The password for the ssl trust store.                                            |
| https.trust.store.path     | String   | Medium     |               |                                  | The key manager password.                                                        |
| thread.pool.max.size       | Int      | Medium     | 100           | [10,...,1000]                    | The maximum number of threads for the thread pool to allocate.                   |
| thread.pool.min.size       | Int      | Medium     | 10            | [10,...,1000]                    | The minimum number of threads for the thread pool to allocate.                   |
| http.idle.timeout.ms       | Int      | Low        | 30000         | [5000,...,300000]                | The number of milliseconds idle before a connection has timed out.               |
| https.idle.timeout.ms      | Int      | Low        | 30000         | [5000,...,300000]                | The number of milliseconds idle before a connection has timed out.               |


#### Standalone Example

```properties
name=connector1
tasks.max=1
connector.class=com.github.jcustenborder.kafka.connect.influxdb.InfluxDBSourceConnector
# The following values must be configured.
```

#### Distributed Example

```json
{
    "name": "connector1",
    "config": {
      "connector.class": "com.github.jcustenborder.kafka.connect.influxdb.InfluxDBSourceConnector",
    }
}
```


# Source Connectors

## InfluxDBSinkConnector

The InfluxDBSinkConnector is used to write data from a Kafka topic to an InfluxDB host. When there are more than one record in a batch that have the same measurement, time, and tags, they will be combined to a single point an written to InfluxDB in a batch.

### Configuration

| Name                       | Type     | Importance | Default Value | Validator                                                                                                  | Documentation                                              |
| -------------------------- | -------- | ---------- | ------------- | ---------------------------------------------------------------------------------------------------------- | -----------------------------------------------------------|
| influxdb.database          | String   | High       |               |                                                                                                            | The influxdb database to write to.                         |
| influxdb.url               | String   | High       |               |                                                                                                            | The url of the InfluxDB instance to write to.              |
| influxdb.password          | Password | High       | [hidden]      |                                                                                                            | The password to connect to InfluxDB with.                  |
| influxdb.username          | String   | High       |               |                                                                                                            | The username to connect to InfluxDB with.                  |
| influxdb.consistency.level | String   | Medium     | ONE           | ValidEnum{enum=ConsistencyLevel, allowed=[ALL, ANY, ONE, QUORUM]}                                          | The default consistency level for writing data to InfluxDB.|
| influxdb.timeunit          | String   | Medium     | MILLISECONDS  | ValidEnum{enum=TimeUnit, allowed=[NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS]} | The default timeunit for writing data to InfluxDB.         |
| influxdb.gzip.enable       | Boolean  | Low        | true          |                                                                                                            | Flag to determine if gzip should be enabled.               |
| influxdb.log.level         | String   | Low        | NONE          | ValidEnum{enum=LogLevel, allowed=[NONE, BASIC, HEADERS, FULL]}                                             | influxdb.log.level                                         |


#### Standalone Example

```properties
name=connector1
tasks.max=1
connector.class=com.github.jcustenborder.kafka.connect.influxdb.InfluxDBSinkConnector
# The following values must be configured.
influxdb.database=
influxdb.url=
```

#### Distributed Example

```json
{
    "name": "connector1",
    "config": {
        "connector.class": "com.github.jcustenborder.kafka.connect.influxdb.InfluxDBSinkConnector",
        "influxdb.database":"",
        "influxdb.url":"",
    }
}
```


