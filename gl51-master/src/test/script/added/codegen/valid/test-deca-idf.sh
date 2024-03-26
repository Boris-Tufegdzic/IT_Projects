#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="idf"
TEST_NAME="idf"
EXPECTED_OUTPUT="0
-2
2
2.50000e+00
0x1.4p+1
7.18000e+00
0x1.cb851ep+2
1234
1234
56789
56789"
EXPECTED_JAVA_OUTPUT="0
-2
2
2.5
0x1.4p1
7.18
0x1.cb851ep2
1234
1234
56789
56789"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################