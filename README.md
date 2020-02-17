# Serenity Parallel Execution Plugin
=============
A gradle plugin that enables parallel execution of automated Serenity BDD tests with a smaller configuration.

## General Description
=============
To run a Aerenity BDD suite, a property file with all capabilities related to the test environment and tests configuration,
is needed. This plugin is an alternative that allows you to run a test suite in parallel (usin two or more property files) with
a small configuration.

This plugin is made under Gradle's [Worker API](https://guides.gradle.org/using-the-worker-api/) guidelines and follows
the specifications to develop [custom gradle plugins](https://docs.gradle.org/4.10/userguide/custom_plugins.html).

This plugin is specially useful when you are trying to execute mobile or desktop parallel tests.

## Usage
=============
To use the plugin you need Gradle version 4.1 or later and gradle wrapper, to start add the following section into your
build.gradle file.

```shell
plugins {
 id "co.com.bancolombia.certification:serenity-parallel-execution-plugin" version "1.0.0"
}

task parallel(type: ParallelTests, dependsOn: 'clean') {
    srcDirName = 'parallel'
}
```

Where ```srcDirName``` is the name of the properties file(s) folder (one per mobile device or desktop machine where tests will
be executed).

To run the test suite; ```./gradlew parallel``` must be executed.

## Task parallel
=============
With the previous configuration, this plugin guarantees that all scenarios in the test suite will be executed in every
device with an associated properties file.
Also, just a metgod, class or package can be executed to facilitate parallelism not only in execution environment but also in the
scenarios quantity. To do so, there is a new property (```testsToRun```) that must be added into the property file having in
account the next notation:

| Test to execute  | Execution notation |
| ------------- | ------------- |
| A method  |  The method's conainer class and name. Ex: *MakePayment.init  |
| A class  | The class name. Ex: MakePayment  |
| A package  | The package's path. Ex: co.com.devco.certificacion.eribank.auth.*   |

The property will look similar to this:
```shell
testsToRun=MakePayment
```
All test per device will be run if no new property is added.

## Plugin output
=============
Parallel task will not finish after every test in every device is executed. Assuming that the source folder name was 'parallel',
the output files will be as shown below:

```shell
+ build/parallel: Will contain gradle logs by device.
+ target/parallel: Will contain Serenity BDD tests results by device.
+ target/binary: Will contain Serenity BDD execution binaries by device.
```

## Some recomendations
=============
1. The properties file name must be mnemonic (for example the device name) with no spaces or special characters. It is
    recomended in this way because this standard is taken into account to name the plugin outputs.

2. Avoid adding other files than properties into the source folder.

3. It is mandatory to have gradle wrapper installed.

# How I can help?
=============
Review the issues, we hear new ideas.