package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Unary expression.
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    public AbstractExpr getOperand() {
        return operand;
    }
    private AbstractExpr operand;
    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }


    protected abstract String getOperatorName();
  
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(getOperatorName());
        operand.decompile(s);
        s.print(")");
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print("(");
        s.print(getOperatorName());
        operand.decompileToJava(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        if (hasIdentifier()) {
            DVal value = getOperand().getDVal(compiler);
            if (!value.equals(Register.R1)) compiler.addInstruction(new LOAD(value, Register.R1));
        }
        else {
            if (getType().isFloat()) return new ImmediateFloat((Float) getLiteralValue());
            else return new ImmediateInteger((Integer) getLiteralValue());
        }
        return null;
    }

    @Override
    protected boolean hasIdentifier() {
        return getOperand().hasIdentifier();
    }

    @Override
    protected boolean register0Used() {
        return getOperand().register0Used();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal dVal = getDVal(compiler);
        if (!hasIdentifier()) compiler.addInstruction(new LOAD(dVal, Register.R1));
    }

    @Override
    protected int getNumberPush() {
        return getOperand().getNumberPush();
    }
}
