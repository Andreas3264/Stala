#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

set -e # stop on error

./scripts/build_bootstrap.sh
./scripts/test_self_compile.sh
./scripts/compile_x86_64.sh
./scripts/compile_x86_32.sh