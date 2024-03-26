package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import org.apache.commons.lang.Validate;

/**
 * Binary expressions.
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
        s.print(")");
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompileToJava(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompileToJava(s);
        s.print(")");
    }

    abstract protected String getOperatorName();

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        if (hasIdentifier()) {
            if (leftOperand.register0Used()) {
                compiler.incrementMaxStackSize(); //We'll use the stack
                DVal rightValue = getRightOperand().getDVal(compiler);
                if (!rightValue.equals(Register.R0)) compiler.addInstruction(new LOAD(rightValue, Register.R0));
                compiler.addInstruction(new PUSH(Register.R0)); //Register R0 would be overwritten during loading of left operand
                DVal leftValue = getLeftOperand().getDVal(compiler);
                if (!leftValue.equals(Register.R1)) compiler.addInstruction(new LOAD(leftValue, Register.R1));
                compiler.addInstruction(new POP(Register.R0));
            }
            else {
                DVal rightValue = getRightOperand().getDVal(compiler);
                if (!rightValue.equals(Register.R0)) compiler.addInstruction(new LOAD(rightValue, Register.R0));
                DVal leftValue = getLeftOperand().getDVal(compiler);
                if (!leftValue.equals(Register.R1)) compiler.addInstruction(new LOAD(leftValue, Register.R1));
            }
            return null;
        }
        else { //We can compute it once during compilation, and never do it again during execution
            if (getType().isFloat()) {
                Float value = (Float) getLiteralValue();
                if (Float.isInfinite(value)) {
                    compiler.getErrors().jumpToFloatOverflow();
                    value = 0f;
                }
                return new ImmediateFloat(value);
            }
            else return new ImmediateInteger((Integer) getLiteralValue());
        }
    }

    @Override
    protected boolean hasIdentifier() {
        return getLeftOperand().hasIdentifier() || getRightOperand().hasIdentifier();
    }

    @Override
    protected boolean register0Used() {
        return true;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal dVal = getDVal(compiler);
        if (!hasIdentifier()) compiler.addInstruction(new LOAD(dVal, Register.R1));
    }

    @Override
    protected int getNumberPush() {
        if (register0Used()) return 1 + getLeftOperand().getNumberPush() + getRightOperand().getNumberPush();
        else return getLeftOperand().getNumberPush() + getRightOperand().getNumberPush();
    }
}
