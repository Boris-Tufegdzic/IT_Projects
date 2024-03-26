#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="modulo"
EXPECTED_OUTPUT="0
1
0
5
-3
0
-1
0
6
-6
0
0
1
-1"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################