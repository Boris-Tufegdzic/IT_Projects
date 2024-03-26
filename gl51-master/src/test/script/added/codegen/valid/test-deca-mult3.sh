#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="mult3"
EXPECTED_OUTPUT="0.00000e+00
5.00000e+00
8.50000e+00
-8.50000e+00
-8.50000e+00
-1.04000e+01
-4.52553e+02
0.00000e+00
4.80000e+00
-2.20000e+00
1.43750e+00
1.25000e+00
-1.03125e+00
-8.34783e+00"
EXPECTED_JAVA_OUTPUT="0.0
5.0
8.5
-8.5
-8.5
-10.4
-452.55255
0.0
4.8
-2.2
1.4375
1.25
-1.03125
-8.347827"




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################