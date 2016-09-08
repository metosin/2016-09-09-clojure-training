#!/bin/bash

set -e

if [ ! -f build.boot ]; then
    echo "$0 must be inside the directory where build.boot is!"
fi

PACKAGEDATE=`date +%Y-%m-%d-%H%M%S`

boot package

zip -r "app-$PACKAGEDATE.zip" \
    Dockerfile \
    target/app.jar
