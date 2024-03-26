#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/context/invalid/"
PROGRAM_NAME="includeCircular"
EXPECTED_OUTPUT="CircularIncludeError"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
