#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

rm -rf "$TEMP_DIR" "$DIST_DIR"