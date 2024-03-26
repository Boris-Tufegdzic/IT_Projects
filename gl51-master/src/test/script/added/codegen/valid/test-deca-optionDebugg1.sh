#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="cond4"
TEST_NAME="optionDebugg1"
#OPTIONS="-d" it works 
#EXPECTED_DECA_OUTPUT="INFO  fr.ensimag.deca.CompilerOptions.parseArgs(CompilerOptions.java:113) - Application-wide trace level set to INFO
#INFO  fr.ensimag.deca.CompilerOptions.parseArgs(CompilerOptions.java:118) - Java assertions enabled
#INFO  fr.ensimag.deca.DecacCompiler.doCompile(DecacCompiler.java:220) - Output file assembly file is: /user/7/.base/granierj/home/Projet_GL/gl51/src/test/deca/codegen/valid/cond4.ass
#INFO  fr.ensimag.deca.DecacCompiler.doCompile(DecacCompiler.java:229) - Writing assembler file ...
#INFO  fr.ensimag.deca.DecacCompiler.doCompile(DecacCompiler.java:233) - Compilation of /user/7/.base/granierj/home/Projet_GL/gl51/src/test/deca/codegen/valid/cond4.deca successful."




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
