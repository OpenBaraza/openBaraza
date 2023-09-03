#!/bin/bash

export CLASSPATH=.:./lib/jfreechart-1.5.2.jar:./lib/jcommon-1.0.24.jar:../../../build/lib/postgresql-42.4.0.jar:

javac LineChartTest.java

java LineChartTest

