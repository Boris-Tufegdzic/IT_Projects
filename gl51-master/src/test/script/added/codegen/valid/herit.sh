#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="herit"
EXPECTED_OUTPUT="1.00000e-01
2.00000e-01
3.00000e-01
4.00000e-01
5.00000e-01
6.00000e-01
7.00000e-01
8.00000e-01
9.00000e-01
1.00000e+00"
EXPECTED_JAVA_OUTPUT="0.1
0.2
0.3
0.4
0.5
0.6
0.7
0.8
0.9
1.0"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################