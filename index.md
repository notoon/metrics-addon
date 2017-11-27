---
title: "Metrics"
addon: "Metrics"
repo: "https://github.com/seedstack/metrics-addon"
author: Adrien LAUER
description: "Provides metrics and health monitoring using Codahale Metrics library."
tags:
    - monitoring
zones:
    - Addons
noMenu: true    
---

SeedStack metrics add-on provides integration of [CodaHale Metrics](http://metrics.dropwizard.io/) to monitor applicative 
metrics and health.<!--more-->

## Dependency

To add the Metrics add-on to your project, add the following dependency: 

{{< dependency g="org.seedstack.addons.metrics" a="metrics" >}}

## Metrics

Five types of metrics can be measured:

* **Gauge**, which simply collects a value.
* **Counter**, which is an incrementing or decrementing value.
* **Histogram**, which measures the distribution of values in a stream of data. 
* **Meter**, which measure the rate at which a set of events occur.
* **Timer**, which combines an histogram of an event duration and a meter of the rate of its occurrence.

### @Gauge

To register a Gauge, use the <{{< java "com.codahale.metrics.annotation.Gauge" "@" >}} annotation on any method:

```java
public class SomeClass {
    @Gauge(name = "queueSize")
    public int getQueueSize() {
        return queue.size;
    }
}
```

### @CachedGauge

You can also use its {{< java "com.codahale.metrics.annotation.CachedGauge" "@" >}} counterpart which allows for a more efficient
reporting of value which are expensive to calculate:

```java
public class SomeClass {
    @CachedGauge(name = "queueSize", timeout = 30, timeoutUnit = TimeUnit.SECONDS)
    public int getQueueSize() {
        return queue.getSize();
    }
}
```

### @Counted

The {{< java "com.codahale.metrics.annotation.Counted" "@" >}} annotation will create a counter of the invocations of the
method it is applied to:

```java
public class SomeClass {
    @Counted(name = "fancyName")
    public String fancyName(String name) {
        return "Sir Captain " + name;
    }
}
```
    
Note that if the `monotonic` parameter is set to false, the counter is increment upon method entry and decremented upon
method exit. If set to true, the counter only increments, effectively counting the number of method invocations.

### @Metered

The {{< java "com.codahale.metrics.annotation.Metered" "@" >}} annotation will create a meter which will measure the
rate of invocation of the method it is applied to:

```java
public class SomeClass {
    @Metered(name = "fancyName")
    public String fancyName(String name) {
        return "Sir Captain " + name;
    }
}
```

### @ExceptionMetered
    
Its counter-part, the {{< java "com.codahale.metrics.annotation.ExceptionMetered" "@" >}} annotation will create a meter
which will measure the rate of exception throwing of the method it is applied to:

```java
public class SomeClass {
    @ExceptionMetered
    public String fancyName(String name) {
        return "Sir Captain " + name;
    }
}
```

### @Metric

    
The more generic {{< java "com.codahale.metrics.annotation.Metric" "@" >}} annotation permits two different uses. When 
applied on an empty Metric field, the corresponding metric will be created and injected:

```java
public class SomeClass {
    @Metric
    private Meter meter;
}
```

When applied on a non-empty Metric field, the metric will be registered:

```java
public class SomeClass {
    @Metric
    private Histogram uniformHistogram = new Histogram(new UniformReservoir());
}
```
    
In both cases, it is up to the client code to interact with the metric.       

### Registry

If you need more control over the metrics registration process, you can inject the {{< java "com.codahale.metrics.MetricRegistry" >}}:

```java
public class SomeClass {
    @Inject
    private MetricRegistry metricRegistry;
}
```
    
This also allows to interact programmatically with any registered metrics.

## Health-checks

An health check is a class that will check a specific state of the application and report it. To create an health check, 
you must extend the {{< java "com.codahale.metrics.health.HealthCheck" >}} class and annotate it with the 
{{< java "org.seedstack.seed.metrics.HealthChecked" "@" >}} annotation:

```java
@HealthChecked
public class GoodHealthCheck extends HealthCheck {
    @Inject
    private MyService myService;

    @Override
    protected Result check() throws Exception {
        if (myService.isOk()) {
            return Result.healthy("I'm fine !");
        } else {
            return Result.unhealthy("Boo");
        }
    }
}
```
