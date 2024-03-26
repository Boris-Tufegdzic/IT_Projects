#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="cast_float"
EXPECTED_OUTPUT="3
-3
5
-4
6
-6
0
0
1
-1
1
2"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
