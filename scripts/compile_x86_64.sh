#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

set -e # stop on error

echo "compiling 64 bit compiler"
rm -f "$TEMP_DIR/stala_x86_64.asm"
self_compile_using_compiler_compile_to "stala" "configs/compiler_x86_64" "stala_x86_64"