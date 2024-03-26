#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="entier2"
EXPECTED_OUTPUT="0
-5
-2
3
13
16"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
