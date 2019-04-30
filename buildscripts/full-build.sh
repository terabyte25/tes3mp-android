#!/bin/bash

set -e

# ARCH=arm below is a hack for our Great Build System
ARCH=arm ./include/download-ndk.sh
ARCH=arm ./include/setup-ndk.sh

./build.sh --arch arm --ccache

./build.sh --arch arm64 --ccache --no-resources &
PID1=$!

./build.sh --arch x86_64 --ccache --no-resources &
PID2=$!

./build.sh --arch x86 --ccache --no-resources &
PID3=$!

wait $PID1 $PID2 $PID3
