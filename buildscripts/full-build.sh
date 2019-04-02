#!/bin/bash

set -e

./build.sh --arch arm --ccache

./build.sh --arch arm64 --ccache &
PID1=$!

./build.sh --arch x86_64 --ccache &
PID2=$!

./build.sh --arch x86 --ccache &
PID3=$!

wait $PID1 $PID2 $PID3
