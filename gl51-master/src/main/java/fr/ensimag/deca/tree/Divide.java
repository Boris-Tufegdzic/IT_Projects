package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl51
 * @date 01/01/2024
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        try {
            DVal immediate = super.getDVal(compiler);
            if (hasIdentifier()) {
                if (getType().isInt()) {
                    compiler.getErrors().codeGenCheckDividingByZero(Register.R0, false);
                    compiler.addInstruction(new QUO(Register.R0, Register.R1));
                } else {
                    compiler.getErrors().codeGenCheckDividingByZero(Register.R0, true);
                    compiler.addInstruction(new DIV(Register.R0, Register.R1));
                    compiler.getErrors().codeGenFloatOverflow();
                }
                return Register.R1;
            }
            return immediate;
        } catch (ArithmeticException arithmeticException) { //There is a division by zero, so we can't actually compute the result, but we still have to compile the program, which will end with an error during execution
            compiler.getErrors().jumpToDividingByZero();
            if (getType().isInt()) return new ImmediateInteger(0);
            else return new ImmediateFloat(0);
        }
    }

    @Override
    protected Object getLiteralValue() {
        if(getType().isInt()) {
            return (Integer) getLeftOperand().getLiteralValue() / (Integer) getRightOperand().getLiteralValue();
        }
        else {
            return (Float) getLeftOperand().getLiteralValue() / (Float) getRightOperand().getLiteralValue();
        }
    }
}
