#!/bin/bash

cd "$(dirname "$0")"
source build.conf
cd ..

set -e # stop on error

./scripts/clean.sh

mkdir "$TEMP_DIR"


bootstrap_using_compiler_compile_to() {
	echo "compiling $2 with $1"
	cd bootstrap/"$2"
	using_compiler_compile_to ../../"$TEMP_DIR"/compiler_"$1" compiler ../../"$TEMP_DIR"/compiler_"$2"
	cd ../..
}


cd bootstrap
echo "compiling StalaJava"
find StalaJava/src -type f -name "*.java" -print0 | xargs -0 javac -d ../"$TEMP_DIR"/StalaJava/bin -Xlint:deprecation
cd ..


FIRST="R1"
echo "compiling $FIRST with original Java compiler"
cd bootstrap/"$FIRST"
java -cp ../../"$TEMP_DIR"/StalaJava/bin/ main/Main stala compiler.stala -out ../../"$TEMP_DIR"/compiler_"$FIRST"
nasm -f macho64 ../../"$TEMP_DIR"/compiler_"$FIRST".asm
ld -macosx_version_min 11.0 -L /Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/usr/lib -lSystem -o ../../"$TEMP_DIR"/compiler_"$FIRST" ../../"$TEMP_DIR"/compiler_"$FIRST".o
chmod +x ../../"$TEMP_DIR"/compiler_"$FIRST"
cd ../..

bootstrap_using_compiler_compile_to "R1" "V2"
#bootstrap_using_compiler_compile_to "V1" "V2" # R1 can compile V2
bootstrap_using_compiler_compile_to "V2" "V3"

cp "$TEMP_DIR"/compiler_V3.asm "$TEMP_DIR"/bootstrap.asm

./scripts/compile_self.sh



