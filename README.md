# Monitoring Filter [![Circle CI](https://circleci.com/gh/Rise-Vision/monitoring-filter.svg?style=svg)](https://circleci.com/gh/Rise-Vision/monitoring-filter)

## Introduction

The Monitoring Filter is a servlet filter implemented to be use within your Google App Engine application. When it is enabled in you GAE app, it will intercept all the requests, get specific information from them then record a log entry with the extracted information.

The currently version has been implemented to get the GAE client id OAuth token and the username for logged in users 

Monitoring Filter works in conjunction with [Rise Vision](http://www.risevision.com), the [digital signage management application](http://rva.risevision.com/) that runs on [Google Cloud](https://cloud.google.com).

At this time Chrome is the only browser that this project and Rise Vision supports.

## Built With
- Java (1.7)
- GAE (Google App Engine) 
- Maven
- [Wagon-Git](https://github.com/synergian/wagon-git)
- [Mockito](https://github.com/mockito/mockito)

## Development

### Local Development Environment Setup and Installation

* Maven 3 is required so you need to do some things to make sure your apt-get doesn't install an older version of maven.

* clone the repo using Git to your local:
```bash
git clone https://github.com/Rise-Vision/monitoring-filter.git
```

* cd into the repo directory
```bash
cd monitoring-filter
```

* Run this command to build locally
``` bash
mvn clean install
```

### Dependencies
* Junit for testing 
* Mockito for mocking and testing
* Google App Engine SDK
* Wagon-Git for releasing the artifacts to [Rise Vision Maven Repository](https://github.com/Rise-Vision/mvn-repo)

### Testing
* Run this command to test locally
``` bash
mvn test
```

### Usage
* Add Monitoring filter as dependency to your project 

```xml

<!-- Inside pom.xml of your project -->

<repositories>
  <repository>
    <id>mvn-repo-releases</id>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
    <name>Maven Repository - Releases</name>
    <url>https://raw.github.com/Rise-Vision/mvn-repo/releases</url>
  </repository>
  <repository>
    <id>mvn-repo-snapshots</id>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
    <name>Maven Repository - Snapshots</name>
    <url>https://raw.github.com/Rise-Vision/mvn-repo/snapshots</url>
  </repository>
</repositories>

<!-- ... -->

<!-- In the <dependencies> section of your project's pom.xml -->
<dependency>
  <!-- From our private repo -->
  <groupId>com.risevision.monitoring</groupId>
  <artifactId>monitoring-filter</artifactId>
  <version>1.0</version>
</dependency>

```

* Configure the filter on the web.xml of your project. It accepts a list of api names which are the API class names. 

```xml
    
<filter>
    <filter-name>MonitoringFilter</filter-name>
    <filter-class>com.risevision.monitoring.filter.MonitoringFilter</filter-class>
    <init-param>
        <param-name>apis</param-name>
        <param-value>API_1_CLASS_NAME, API_2_CLASS_NAME</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>MonitoringFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

```


## Submitting Issues
If you encounter problems or find defects we really want to hear about them. If you could take the time to add them as issues to this Repository it would be most appreciated. When reporting issues please use the following format where applicable:

**Reproduction Steps**

1. did this
2. then that
3. followed by this (screenshots / video captures always help)

**Expected Results**

What you expected to happen.

**Actual Results**

What actually happened. (screenshots / video captures always help)

## Contributing
All contributions are greatly appreciated and welcome! If you would first like to sound out your contribution ideas please post your thoughts to our [community](http://community.risevision.com), otherwise submit a pull request and we will do our best to incorporate it

## Resources
If you have any questions or problems please don't hesitate to join our lively and responsive community at http://community.risevision.com.

If you are looking for user documentation on Rise Vision please see http://www.risevision.com/help/users/

If you would like more information on developing applications for Rise Vision please visit http://www.risevision.com/help/developers/.

**Facilitator**

[Rodrigo Serviuc Pavezi](https://github.com/rodrigopavezi "Rodrigo Serviuc Pavezi")
