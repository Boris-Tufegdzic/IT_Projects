#! /bin/bash

if [ -z ${PROGRAM_PATH+x} ]; then
    echo "ERROR : end-script.sh must be called by another script!"
    exit 1
fi

#Clear result of preceding test, if any
if [ -f "$PROGRAM_PATH/Main.class" ]; then
    rm $PROGRAM_PATH/*.class &> /dev/null
fi

if [ "$TEST_NAME" == "" ]; then
    TEST_NAME=$PROGRAM_NAME
fi

if [ "$EXPECTED_JAVA_OUTPUT" == "" ]; then
    EXPECTED_JAVA_OUTPUT=$EXPECTED_OUTPUT
fi

if [[ $PROGRAM_PATH =~ "/invalid/" ]]; then   #The program is invalid and should fail
    SHOULD_FAIL=1
else                                        #The program is valid and should not fail
    SHOULD_FAIL=0
fi

{
  decac_result=$($decac $OPTIONS "$PROGRAM_PATH/$PROGRAM_NAME.deca")
} # &> /dev/null

if [ "$?" -ne 0 ]; then
    if [ $SHOULD_FAIL -eq 1 ]; then    #It's invalid
        echo "$TEST_NAME: compilation has failed, as expected"
        exit 0
    fi
    echo "$TEST_NAME: ERROR: decac has returned with non-zero status!"
    exit 1
else
    if [ $SHOULD_FAIL -eq 1 ]; then    #It should have failed but hasn't
        echo "$TEST_NAME: ERROR: compilation has succeeded unexpectedly"
        exit 1
    fi
fi

if [ "${decac_result}" != "$EXPECTED_DECA_OUTPUT" ]; then
    echo "$TEST_NAME: ERROR: decac has produced an unexpected output!"
    echo $decac_result
    exit 1
fi

if [ -z ${EXPECTED_OUTPUT+x} ]; then
    # echo "$TEST_NAME: (No expected output, not running ima)"
    exit 0;
fi

ima_result=$($ima "$PROGRAM_PATH/$PROGRAM_NAME.ass")

if [ "$ima_result" != "$EXPECTED_OUTPUT" ]; then
    echo "$TEST_NAME: Expected output: $EXPECTED_OUTPUT
    Actual output: $ima_result
    $TEST_NAME: ERROR: The compiled program $PROGRAM_NAME.ass has returned an unexpected output"
    exit 1
fi

{
  decac_bytecode_result=$($decac $OPTIONS -byte "$PROGRAM_PATH/$PROGRAM_NAME.deca")
} # &> /dev/null

if [ "$?" -ne 0 ]; then
    if [ $SHOULD_FAIL -eq 1 ]; then    #It's invalid
        echo "$TEST_NAME (bytecode): compilation has failed, as expected"
        exit 0
    fi
    echo "$TEST_NAME (bytecode): ERROR: decac has returned with non-zero status!"
    exit 1
else
    if [ $SHOULD_FAIL -eq 1 ]; then    #It should have failed but hasn't
        echo "$TEST_NAME (bytecode): ERROR: compilation has succeeded unexpectedly"
        exit 1
    fi
fi

if [ "${decac_byte_code_result}" != "$EXPECTED_DECA_OUTPUT" ]; then
    echo "$TEST_NAME (bytecode): ERROR: decac has produced an unexpected output!"
    echo $decac_result
    exit 1
fi

if [ $TEST_BYTECODE -eq 1 ]; then
    cd ./$PROGRAM_PATH
    java_result=$(java "Main")
    if [ "$java_result" != "$EXPECTED_JAVA_OUTPUT" ]; then
        echo "$TEST_NAME (bytecode): Expected output: $EXPECTED_JAVA_OUTPUT
        Actual output: $java_result
        $TEST_NAME (bytecode): ERROR: The compiled program Main.class has returned an unexpected output"
        exit 1
    fi
    cd ./$PROJECT_PATH
fi

echo "$TEST_NAME: SUCCESS: decac has successfully compiled $PROGRAM_NAME.deca into $PROGRAM_NAME.ass and $PROGRAM_NAME.class, and both have returned the expected output"
