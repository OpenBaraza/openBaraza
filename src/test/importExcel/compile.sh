 
 #!/bin/bash
 

export CLASSPATH=.:../../../build/lib/poi-3.10.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/poi-ooxml-3.10.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/poi-ooxml-schemas-3.10.jar
export CLASSPATH=$CLASSPATH:../../../build/lib/postgresql-42.2.2.jar


#echo "loading"
 
javac huduma_import.java
 
java huduma_import
 
 
 
