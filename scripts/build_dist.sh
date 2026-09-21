#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

set -e # stop on error

./scripts/build_all.sh

mkdir "$DIST_DIR"

cp "$TEMP_DIR"/bootstrap.asm "$DIST_DIR"/bootstrap.asm
cp "$TEMP_DIR"/stala_x86_64 "$DIST_DIR"/stala_x86_64
cp "$TEMP_DIR"/stala_x86_32 "$DIST_DIR"/stala_x86_32