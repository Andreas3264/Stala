#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

set -e # stop on error

create_executable_from_asm "$TEMP_DIR"/bootstrap

# compile compiler.stala using bootstrap
echo "compiling compiler with bootstrap"
self_compile_using_compiler_compile_to "bootstrap" "compiler_x86_64" "stala_from_bootstrap"

# compile compiler.stala using compiler.stala
echo "compiling compiler with itself"
self_compile_using_compiler_compile_to "stala_from_bootstrap" "compiler_x86_64" "stala_from_stala"

# compile compiler.stala using compiler.stala
echo "verifying it compiles itself to itself"
self_compile_using_compiler_compile_to "stala_from_stala" "compiler_x86_64" "stala"

# verify that stala_from_stala compiled itself to itself
diff "$TEMP_DIR"/stala_from_stala.asm "$TEMP_DIR"/stala.asm -q -s