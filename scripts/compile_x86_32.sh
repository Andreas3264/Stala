#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

set -e # stop on error

echo "compiling 32 bit compiler"
self_compile_using_compiler_compile_to "stala" "compiler_64_to_32" "stala_x86_32"