#!/bin/bash
# set the working directory to the directory containing this script
cd "$(dirname "$0")"
source ../../scripts/build.conf

find . -type f -name '*.stala' -exec cat {} + > ../../"$TEMP_DIR"/compiler_R1.stala