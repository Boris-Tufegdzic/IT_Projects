#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/provided/"
PROGRAM_NAME="exdoc"
EXPECTED_OUTPUT="a.getX() = 1"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
