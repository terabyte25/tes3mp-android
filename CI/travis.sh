#!/bin/bash

set -e

./buildscripts/full-build.sh

./gradlew assembleDebug
