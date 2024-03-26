#! /bin/bash

# Initialize default values
OPTIONS=""
TEST_NAME=""
EXPECTED_DECA_OUTPUT=""
EXPECTED_JAVA_OUTPUT=""
TEST_BYTECODE=1

# Initialize paths
PROJECT_PATH=.
TESTS_PATH=${PROJECT_PATH}/src/test/deca
decac=${PROJECT_PATH}/src/main/bin/decac
ima=${PROJECT_PATH}/ima/bin/ima

# Expected outputs for errors
INPUT_ERROR="ERROR: Input error"
STACK_OVERFLOW_ERROR="ERROR: Stack overflow"
NULL_DEREFERENCE_ERROR="ERROR: Dereferencing null"
DIVISION_BY_ZERO_ERROR="ERROR: Dividing (or modulo) by zero"
FLOAT_OVERFLOW_ERROR="ERROR: Float overflow"
NO_RETURN_IN_METHOD_ERROR="ERROR: No return during execution of method"
CAST_ERROR="ERROR: Cast error"
HEAP_OVERFLOW_ERROR="ERROR: Heap overflow"

# Check if compiler has been compiled
if [ ! -f "$PROJECT_PATH/target/classes/fr/ensimag/deca/DecacMain.class" ]; then
  echo "Please run 'mvn compile' before testing"
  exit 1
fi

# Check if ima is installed; else, install it
if [ ! -f "$ima" ]; then
  echo "ima couldn't be detected in $ima: initializing"
  mkdir ${PROJECT_PATH}/ima
  tar -C ${PROJECT_PATH}/ima -xzf ${PROJECT_PATH}/docker/ima_sources.tgz
  if [ -f "$ima" ]; then
    echo "ima initialized successfully!"
  else
    echo "FATAL ERROR: unable to initialize ima. Please make sure that docker/ima_sources.tgz is present"
  fi
fi
