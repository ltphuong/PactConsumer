# Overview

In this source, I create 2 pact consumer:
1. Search weather by city name
2. Search weatther with invalid city id

# Technical in project:

- Programming language: Java

# Install

- Install java sdk version 8: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
- Install IntelliJ: https://www.jetbrains.com/idea/download/
- Install Gradle 6.8: https://gradle.org/install/

# How to work?

- Clone source from this repo to your PC
- Go to folder that store this souce code, open terminal and run "grade test"
- Run "gradle pactPublish" to publish pact consumer to pact-broker server

After finished, you will see as below image:
![image](https://user-images.githubusercontent.com/17809726/116716226-b0a19200-aa01-11eb-8db5-42974168c939.png)
