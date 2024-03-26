#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="optionBanner"
OPTIONS="-b"
EXPECTED_DECA_OUTPUT="---------------------------------------
---------------- DECAC ----------------
------------ Deca compiler ------------
--------- Projet GL 2023-2024 ---------
------------ gl51, Ensimag ------------
---------------------------------------
--------------- MADE BY ---------------
--------- Jean-Charles Granier --------
------------- Vishal Kumar ------------
------------ Thomas Serafin -----------
------------- Virgile Solt ------------
--------- AND Boris Tufegdzic ---------
---------------------------------------"

##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
