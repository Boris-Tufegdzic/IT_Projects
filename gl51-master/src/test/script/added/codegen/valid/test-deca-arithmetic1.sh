#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="arithmetic1"
EXPECTED_OUTPUT="8.00000e+00
-1.60000e+00
-6.00000e+00
4.10000e+00
-1.09000e+01"
EXPECTED_JAVA_OUTPUT="8.0
-1.6
-6.0
4.1
-10.900001"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
