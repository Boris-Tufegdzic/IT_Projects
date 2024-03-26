package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl51
 * @date 01/01/2024
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
 

    @Override
    protected String getOperatorName() {
        return "+";
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        DVal immediate = super.getDVal(compiler);
        if (hasIdentifier()) {
            compiler.addInstruction(new ADD(Register.R0, Register.R1));
            if (getType().isFloat()) compiler.getErrors().codeGenFloatOverflow();
            return Register.R1;
        }
        return immediate;
    }

    @Override
    protected Object getLiteralValue(){
        if(getType().isInt()) {
            return (Integer) getLeftOperand().getLiteralValue() + (Integer) getRightOperand().getLiteralValue();
        }
        else {
            return (Float) getLeftOperand().getLiteralValue() + (Float) getRightOperand().getLiteralValue();
        }
    }
}
