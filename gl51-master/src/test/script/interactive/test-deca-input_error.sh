#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/interactive/"
PROGRAM_NAME="input_error"
EXPECTED_OUTPUT="Please enter a letter
ERROR: Input error"



##################
#End (don't modify)
. ${SCRIPT_PATH}/end-interactive-script.sh
##################
