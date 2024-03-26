package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!(type1.isInt() && type2.isInt())){
            throw new ContextualError("Wrong types on a modulo operation", this.getLocation());
        }
        setType(compiler.environmentType.INT);
        return compiler.environmentType.INT;
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        compiler.addInstruction(new WINT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        codeGenPrint(compiler);
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        try {
            DVal immediate = super.getDVal(compiler);
            if (hasIdentifier()) {
                compiler.getErrors().codeGenCheckDividingByZero(Register.R0, false);
                compiler.addInstruction(new REM(Register.R0, Register.R1));
                return Register.R1;
            }
            return immediate;
        } catch (ArithmeticException arithmeticException) {
            compiler.getErrors().jumpToDividingByZero();
            if (getType().isInt()) return new ImmediateInteger(0);
            else return new ImmediateFloat(0);
        }
    }

    @Override
    protected Object getLiteralValue() {
        return (Integer) getLeftOperand().getLiteralValue() % (Integer) getRightOperand().getLiteralValue();
    }
}
