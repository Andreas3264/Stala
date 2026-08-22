#!/bin/bash
# set the working directory to the directory containing this script
cd "$(dirname "$0")"
source ../../scripts/build.conf

cat compiler.stala stdlib/x86_64_asm.stala stdlib/system.stala stdlib/string.stala stdlib/stdio.stala stdlib/memory.stala stdlib/file.stala stdlib/collections.stala > ../../"$TEMP_DIR"/compiler_R1.stala