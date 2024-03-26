#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="object_instanceof"
EXPECTED_OUTPUT="ok1
ok2
ok3
ok4
ok5
ok6"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################