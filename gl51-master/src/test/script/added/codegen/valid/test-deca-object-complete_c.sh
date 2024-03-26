#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/context/valid/"
PROGRAM_NAME="testObject-complete"
EXPECTED_OUTPUT="init classes done !
                 setCoordinate ...
                 setCoordinate done !
                 Configure Player...
                 Configure Player done !
                 Call player to String ...
                 -----to_String Player --------
                 123456789
                 220.0
                 ------------------------------
                 test instanceof de monster ...
                 ----- toString Monster --------
                 123456789
                 -----------------------------
                 monster instance of Monster
                 test while: increase player coin till 400.0f...
                 test while done !
                 Test complete finished ! Ciao"


##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################


