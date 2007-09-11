#!/bin/sh

# directory 
# JAVA_HOME=/usr/jdk1.5.0_07
WORKSPACE=`pwd`
NAME=`date +%Y%m%d%H%M`

build() {
    b=$1
    shift
    p=$@
    echo Building $b with $p
    $JAVA_HOME/bin/java -jar org.eclipse.releng.basebuilder/eclipse/startup.jar -application org.eclipse.ant.core.antRunner -buildfile $WORKSPACE/org.eclipse.releng.basebuilder/eclipse/plugins/org.eclipse.pde.build_3.2.0.v20060505a/scripts/build.xml -Dbuilder=$WORKSPACE/$b.builder -DforceContextQualifier=v${NAME} $p
    if [ $? -ne 0 ]
    then
        exit 1
    fi

}

echo Command line: $@
build beans-feature $@
build aop-feature $@
build ajdt-feature $@
build javaconfig-feature $@
build webflow-feature $@
build mylyn-feature $@



