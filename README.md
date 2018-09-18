[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cloud.cirrusup/aws-latency-request-log-handler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cloud.cirrusup/aws-latency-request-log-handler)

# AWS Latency request log handler #

### Description ###
This library exposes a simple but very efficient AWS request handler that log in a text files various details about your AWS calls.

### How to use it ###
i. Declare the dependency in your _pom.xml_ file.
<dependency>
  <groupId>cloud.cirrusup</groupId>
  <artifactId>aws-latency-request-log-handler</artifactId>
  <version>1.0.0</version>
</dependency>

ii. Create a _request handler_ object.

```java
AwsLatencyRequestLogHandler handler = new AwsLatencyRequestLogHandler();
```

iii. Enhance the _AWS client_ with the request handler created above.

```java
amazonS3Client.addRequestHandler(handler);    
```

iv. Create a log appender named *aws-latency-log*.
```java
  <appender name="AWS-APPENDER-LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>/var/log/awsLatency.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <!-- hourly rollover -->
      <fileNamePattern>/var/log/awsLatency/awsLatency.%d{yyyy-MM-dd-HH}.log</fileNamePattern>
      <!-- keep 2 days' worth of history capped at 500MB total size -->
      <maxHistory>2</maxHistory>
      <totalSizeCap>500MB</totalSizeCap>
    </rollingPolicy>
    <encoder>
      <pattern>[%thread] %-5level - %d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>[%thread] %-5level - %d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
```
