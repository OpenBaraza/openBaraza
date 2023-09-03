#!/bin/bash
cd $(dirname $0)
export CLASSPATH=$CLASSPATH:./../../../build/lib/json-20171018.jar
export CLASSPATH=$CLASSPATH:./../../../build/lib/okhttp-3.14.0.jar
export CLASSPATH=$CLASSPATH:./../../../build/lib/okio-1.17.2.jar
export CLASSPATH=$CLASSPATH:./../../../build/lib/postgresql-42.2.2.jar

javac Access_Token.java
java Access_Token

