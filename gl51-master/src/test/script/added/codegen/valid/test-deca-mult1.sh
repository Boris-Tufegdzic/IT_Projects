#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="mult1"
EXPECTED_OUTPUT="0
56
-16
-40

0
0
6
-2
0

1
0
1

-1
-9"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################