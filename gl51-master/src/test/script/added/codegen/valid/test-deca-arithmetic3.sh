#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="arithmetic3"
EXPECTED_OUTPUT="-3
-3.50000e+00
5
2
-2"
EXPECTED_JAVA_OUTPUT="-3
-3.5
5
2
-2"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
