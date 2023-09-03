 
 #!/bin/bash
 

export CLASSPATH=.:../../../build/lib/poi-3.10.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/poi-ooxml-3.10.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/poi-ooxml-schemas-3.10.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/xmlbeans-2.3.0.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/dom4j-1.6.1.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/postgresql-42.2.2.jar


#echo "loading"
 
javac importExcel.java
 
java -Xmx2048m importExcel
 
 
 
