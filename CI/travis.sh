#!/bin/bash

set -e

cd buildscripts
./full-build.sh

cd ..

./gradlew assembleDebug
