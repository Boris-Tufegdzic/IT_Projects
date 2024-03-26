#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/interactive/"
PROGRAM_NAME="read0"
EXPECTED_OUTPUT="Please enter an integer
You have entered: [First input]
Please enter a float
You have entered: [Second input]"



##################
#End (don't modify)
. ${SCRIPT_PATH}/end-interactive-script.sh
##################
