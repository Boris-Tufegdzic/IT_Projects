#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/invalid"
PROGRAM_NAME="division_by_zero"
EXPECTED_OUTPUT=$DIVISION_BY_ZERO_ERROR
TEST_BYTECODE=0




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
