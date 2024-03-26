#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="ecrit2"
EXPECTED_OUTPUT="-2.50000e+00
7.18000e+00
9.80000e+01"
EXPECTED_JAVA_OUTPUT="-2.5
7.18
98.0"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
