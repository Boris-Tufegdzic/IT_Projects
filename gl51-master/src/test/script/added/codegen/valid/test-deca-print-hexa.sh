#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="print_hexa"
EXPECTED_OUTPUT="-0x1.4p+1
0x1.4p+1
0x1.34b852p+6
2.50000e+00

7.71800e+01
0
10
-0x1.4p+10x1.cb851ep+2010"
EXPECTED_JAVA_OUTPUT="-0x1.4p1
0x1.4p1
0x1.34b852p6
2.5

77.18
0
10
-0x1.4p10x1.cb851ep2010"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################