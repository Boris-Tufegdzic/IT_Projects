#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="cast_to_float"
EXPECTED_OUTPUT="1.00000e+01
0.00000e+00
-1.00000e+01
0.00000e+00"
EXPECTED_JAVA_OUTPUT="10.0
0.0
-10.0
0.0"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
