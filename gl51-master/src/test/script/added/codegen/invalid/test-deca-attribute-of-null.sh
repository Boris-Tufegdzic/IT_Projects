#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/invalid"
PROGRAM_NAME="attribute_of_null"
EXPECTED_OUTPUT=$NULL_DEREFERENCE_ERROR
TEST_BYTECODE=0




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
