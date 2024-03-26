package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        getRightOperand().verifyRValue(compiler, localEnv, currentClass, leftType);
        return leftType;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (getRightOperand().getType().isString()) { //String variables are eliminated during compilation
            Definition def = getLeftOperand().getDefinition();
            if (getRightOperand() instanceof StringLiteral) def.setStringValue(((StringLiteral) getRightOperand()).getValue());
            else def.setStringValue(getRightOperand().getDefinition().getStringValue());
            return;
        }
        if (getLeftOperand().register0Used()) {
            compiler.incrementMaxStackSize(); //We'll use the stack
            DVal rightValue = getRightOperand().getDVal(compiler);
            if (!rightValue.equals(Register.R0)) compiler.addInstruction(new LOAD(rightValue, Register.R0));
            compiler.addInstruction(new PUSH(Register.R0)); //Register R0 would be overwritten during loading of left operand
            DVal leftValue = getLeftOperand().getDVal(compiler);
            compiler.addInstruction(new POP(Register.R0));
            compiler.addInstruction(new STORE(Register.R0, (DAddr) leftValue));
        }
        else {
            DVal rightValue = getRightOperand().getDVal(compiler);
            if (!rightValue.equals(Register.R0)) compiler.addInstruction(new LOAD(rightValue, Register.R0));
            DVal leftValue = getLeftOperand().getDVal(compiler);
            compiler.addInstruction(new STORE(Register.R0, (DAddr) leftValue));
        }
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        getLeftOperand().decompileToJava(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompileToJava(s);
        s.print(";");
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }
}
