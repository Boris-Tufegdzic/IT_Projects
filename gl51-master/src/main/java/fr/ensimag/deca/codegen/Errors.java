package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * External class used to manage errors
 */
public class Errors {
    private final DecacCompiler compiler;
    private final Label stackOverflowLabel;
    private final Label nullDereferenceLabel;
    private final Label dividingByZeroLabel; // either a division by 0, or a division by 0.0, or a modulo with 0
    private final Label floatOverflowLabel;
    private final Label noReturnInMethodLabel;
    private final Label castErrorLabel;
    private final Label inputErrorLabel;
    private final Label heapOverflow;
    private final boolean noCheck;

    /**
     * Generates the label of each error
     * @param compiler  the compiler that should call this
     */
    public Errors(DecacCompiler compiler, boolean noCheck) {
        this.compiler = compiler;
        stackOverflowLabel = compiler.newLabel("stackOverflow");
        nullDereferenceLabel = compiler.newLabel("nullDereference");
        dividingByZeroLabel = compiler.newLabel("dividingByZero");
        floatOverflowLabel = compiler.newLabel("floatOverflow");
        noReturnInMethodLabel = compiler.newLabel("noReturnInMethod");
        castErrorLabel = compiler.newLabel("castError");
        inputErrorLabel = compiler.newLabel("inputError");
        heapOverflow = compiler.newLabel("heapOverFlow");
        this.noCheck = noCheck;
    }

    /**
     * Generates the code related to errors
     */
    public void codeGenErrors() {
        compiler.addComment("Errors");

        compiler.addLabel(inputErrorLabel);
        compiler.addInstruction(new WSTR("ERROR: Input error"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        if (!noCheck) {
            compiler.addLabel(stackOverflowLabel);
            compiler.addInstruction(new WSTR("ERROR: Stack overflow"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());

            compiler.addLabel(nullDereferenceLabel);
            compiler.addInstruction(new WSTR("ERROR: Dereferencing null"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());

            compiler.addLabel(dividingByZeroLabel);
            compiler.addInstruction(new WSTR("ERROR: Dividing (or modulo) by zero"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());

            compiler.addLabel(floatOverflowLabel);
            compiler.addInstruction(new WSTR("ERROR: Float overflow"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());

            compiler.addLabel(noReturnInMethodLabel);
            compiler.addInstruction(new WSTR("ERROR: No return during execution of method"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());

            compiler.addLabel(castErrorLabel);
            compiler.addInstruction(new WSTR("ERROR: Cast error"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());

            compiler.addLabel(heapOverflow);
            compiler.addInstruction(new WSTR("ERROR: Heap overflow"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
        }
    }

    /**
     * Generates the code jumping to stack overflow error, without check
     */
    public void jumpToStackOverflow() {
        if (noCheck) return;
        compiler.addInstruction(new BRA(stackOverflowLabel));
    }

    /**
     * Generates the code jumping to null dereference error, without check
     */
    public void jumpToNullDereference() {
        if (noCheck) return;
        compiler.addInstruction(new BRA(nullDereferenceLabel));
    }

    /**
     * Generates the code jumping to dividing by zero error, without check
     */
    public void jumpToDividingByZero() {
        if (noCheck) return;
        compiler.addInstruction(new BRA(dividingByZeroLabel));
    }

    /**
     * Generates the code jumping to float overflow error, without check
     */
    public void jumpToFloatOverflow() {
        if (noCheck) return;
        compiler.addInstruction(new BRA(floatOverflowLabel));
    }

    /**
     * Generates the code jumping to no return in method error, without check
     */
    public void jumpToNoReturnInMetheod() {
        if (noCheck) return;
        compiler.addInstruction(new BRA(floatOverflowLabel));
    }

    /**
     * Generates the code jumping to cast error, without check
     */
    public void jumpToCastError() {
        if (noCheck) return;
        compiler.addInstruction(new BRA(castErrorLabel));
    }

    /**
     * Generates the code jumping to input error, without check
     */
    public void jumpToInputError() {
        compiler.addInstruction(new BRA(inputErrorLabel));
    }

    /**
     * Generates the code checking for a stack overflow
     * @param numberPush    the number of pushes that will be made
     */
    public void codeGenCheckStackOverflow(int numberPush) {
        if (noCheck) return;
        compiler.addComment("Checking for an overflow");
        if (compiler.getGBOffset() == 0) {
            compiler.addComment("GBOffset = 0, test skipped");
            return;
        }
        compiler.addInstruction(new TSTO(compiler.getGBOffset()));
        compiler.addInstruction(new BOV(stackOverflowLabel));
    }

    /**
     * Generates the code checking if null has been dereferenced
     * @param dVal  the dVal to be compared to null
     */
    public void codeGenCheckNullDereference(DVal dVal) {
        if (noCheck) return;
        compiler.addComment("Checking if null is dereferenced");
        compiler.addInstruction(new LOAD(dVal, Register.R1));
        compiler.addInstruction(new CMP(new NullOperand(), Register.R1));
        compiler.addInstruction(new BEQ(nullDereferenceLabel));
    }

    /**
     * Generates the code checking for a division or a modulo by zero
     * @param register  the register containing the value to be compared to 0
     * @param isFloat   true if checking for division by 0.0, false if checking for division or modulo by 0
     */
    public void codeGenCheckDividingByZero(GPRegister register, boolean isFloat) {
        if (noCheck) return;
        compiler.addComment("Checking if division or modulo is by zero");
        if (isFloat) compiler.addInstruction(new CMP(new ImmediateFloat(0.0f), register));
        else compiler.addInstruction(new CMP(0, register));
        compiler.addInstruction(new BEQ(dividingByZeroLabel));
    }

    /**
     * Generates the code checking for a float overflow
     */
    public void codeGenFloatOverflow() {
        if (noCheck) return;
        compiler.addComment("Checking for a float overflow");
        compiler.addInstruction(new BOV(floatOverflowLabel));
    }

    /**
     * Generates the code checking if no return has been reached during the execution of a method
     */
    public void codeGenCheckNoReturnInMethod() {
        if (noCheck) return;
        compiler.addInstruction(new BRA(noReturnInMethodLabel));
    }

    /**
     * Generates the code checking if a cast is impossible
     * @param toLookFor     the GB offset of the type to which to cast
     * @param toLoad    the DVal of the object to be cast
     */
    public void codeGenCheckCastError(RegisterOffset toLookFor, DVal toLoad) {
        if (noCheck) return;
        Label beginLabel = compiler.newLabel("castErrorTest");
        Label endLabel = compiler.newLabel("endOfCastErrorTest");
        compiler.addInstruction(new LOAD(toLoad, Register.R1));
        compiler.addInstruction(new CMP(toLookFor, Register.R1));
        compiler.addInstruction(new BEQ(endLabel));
        compiler.addLabel(beginLabel);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R1), Register.R1));
        compiler.addInstruction(new CMP(toLookFor, Register.R1));
        compiler.addInstruction(new BEQ(endLabel));
        compiler.addInstruction(new CMP(new NullOperand(), Register.R1));
        compiler.addInstruction(new BNE(beginLabel));
        compiler.addInstruction(new BRA(castErrorLabel));
        compiler.addLabel(endLabel);
    }

    /**
     * Generates the code checking for an input error
     */
    public void codeGenCheckInputError() {
        compiler.addComment("Checking for an input error");
        compiler.addInstruction(new BOV(inputErrorLabel));
    }

    /**
     * Generates the code checking for a heap overflow
     */
    public void codeGenCheckHeapOverflow() {
        compiler.addComment("Checking for a heap overflow");
        compiler.addInstruction(new BOV(heapOverflow));
    }
}
