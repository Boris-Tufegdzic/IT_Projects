package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl51
 * @date 01/01/2024
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!type.isInt() && !type.isFloat()){
            throw new ContextualError("Type error in a unary minus operation", this.getLocation());
        }
        setType(type);
        return type;
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(getDVal(compiler), Register.R1));
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(getDVal(compiler), Register.R1));
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOATX());
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        DVal immediate = super.getDVal(compiler);
        if (hasIdentifier()) {
            compiler.addInstruction(new OPP(Register.R1, Register.R1));
            return Register.R1;
        }
        return immediate;
    }

    @Override
    protected Object getLiteralValue() {
        if (getType().isInt()) return - (Integer) getOperand().getLiteralValue();
        else return - (Float) getOperand().getLiteralValue();
    }
}
