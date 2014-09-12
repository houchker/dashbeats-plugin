#/usr/bin/bash

HOME=./JenkinsHome85
PORT=8085
AJP13_PORT=-1
DEBUG_PORT=8005

DEBUG=" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=$DEBUG_PORT,suspend=n"

export JENKINS_HOME=$HOME
export HUDSON_HOME=$HOME
export MAVEN_OPTS="$MVN_OPTS $DEBUG"

JAVA_ARGS="$JAVA_ARGS --httpPort=$PORT" 
#JAVA_ARGS="$JAVA_ARGS --ajp13Port=$AJP13_PORT" 

mvn clean hpi:run -Djetty.port=$PORT -DhudsonHome=$HOME 

