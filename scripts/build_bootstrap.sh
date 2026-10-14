#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

set -e # stop on error

./scripts/clean.sh

mkdir "$TEMP_DIR"

echo "compiling StalaJava"
find bootstrap/StalaJava/src -type f -name "*.java" -print0 | xargs -0 javac -d "$TEMP_DIR"/StalaJava/bin -Xlint:deprecation

FIRST="R1"

./bootstrap/"$FIRST"/merge.sh

echo "compiling $FIRST with original Java compiler"
java -cp "$TEMP_DIR"/StalaJava/bin/ main/Main stala "$TEMP_DIR"/compiler_"$FIRST".stala -out "$TEMP_DIR"/compiler_"$FIRST"
create_executable_from_asm "$TEMP_DIR"/compiler_"$FIRST"

#bootstrap_using_compiler_compile_to "R1" "V3" # R1 can compile V3

cp "$TEMP_DIR"/compiler_R1.asm "$TEMP_DIR"/bootstrap.asm