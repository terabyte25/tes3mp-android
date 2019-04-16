#!/bin/bash

set -e

git checkout master

cd buildscripts

# ./full-build.sh

cd ..

./gradlew assembleDebug

owner="terabyte25"
repo="tes3mp-android"
github_api_token="d409637f7414741538f5454b0101a9e25e8696e6"
VERSION=$(sed -n '2p' < app/src/main/assets/libopenmw/resources/version)
tag=$(date +%D)

hub release create -a ./app/build/outputs/apk/debug/app-debug.apk -m "$tag nightly" $tag

