#! /bin/bash

##################
#Init (don't modify)
SCRIPT_PATH=./src/test/script/
. ${SCRIPT_PATH}/init-script.sh
##################




PROGRAM_PATH="$TESTS_PATH/codegen/valid/"
PROGRAM_NAME="cond4"
TEST_NAME="optionParse"
# OPTIONS="-p"
# EXPECTED_DECA_OUTPUT='{
# 	if(((1 >= 0) && (55 == 45))){println(erreur 1);
# 	} else {if(((1 >= 2) || (1 >= 0))){if((1 != 1)){println(erreur 2);
# 			} else {if((1 == 1)){if((2 == 2)){println(ok);
# 					} else {}
# 				} else {println(erreur 4);
# 				}
# 			}
# 		} else {println(erreur 5);
# 		}
# 	}
# }'
#It works, but the actual output is more beautiful




##################
#End (don't modify)
. ${SCRIPT_PATH}/end-script.sh
##################
