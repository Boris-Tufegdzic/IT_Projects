#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="arithmetic2"
EXPECTED_OUTPUT="8.00000e+00
-1.10000e+00
-6.00000e+00
3.90000e+00
-1.02000e+01"
EXPECTED_JAVA_OUTPUT="8.0
-1.1
-6.0
3.9
-10.2"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
