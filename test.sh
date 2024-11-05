#!/bin/bash

TARGET=$1

if [ $# -eq 0 ];then
    TARGET='all'
fi

echo "arguments is ${TARGET}"

if [ 'all' = $TARGET ];then
    mvn test surefire-report:report-only
else
    mvn -pl $TARGET test surefire-report:report-only
fi
