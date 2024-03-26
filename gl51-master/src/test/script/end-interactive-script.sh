#! /bin/bash

if [ -z ${PROGRAM_PATH+x} ]; then
    echo "ERROR : end-script.sh must be called by another script!"
    exit 1
fi

decac_result=$($decac $OPTIONS "$PROGRAM_PATH/$PROGRAM_NAME.deca")

if [ "$?" -ne 0 ]; then
    echo "$PROGRAM_NAME: ERROR: decac has returned with non-zero status!"
    exit 1
fi

if [ "${decac_result}" != "" ]; then
    echo "$PROGRAM_NAME: ERROR: decac has produced an output!"
    echo $decac_result
    exit 1
fi

echo "SUCCESS: decac has successfully compiled $PROGRAM_NAME.deca"
echo "Note : This is an interactive test that may require inputs from you ; the result will NOT be checked automatically.\nFor your information, the expected output is: $EXPECTED_OUTPUT"

echo "[Beginning of $PROGRAM_NAME.ass]"
ima_result=$($ima "$PROGRAM_PATH/$PROGRAM_NAME.ass")
echo "[End of $PROGRAM_NAME.ass]"
