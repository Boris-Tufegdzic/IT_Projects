#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/invalid"
PROGRAM_NAME="float_overflow"
EXPECTED_OUTPUT=$FLOAT_OVERFLOW_ERROR
TEST_BYTECODE=0




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
