package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * @author gl51
 * @date 01/01/2024
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type type = expression.verifyExpr(compiler, localEnv, currentClass);
        expression.setType(type);
        expression.verifyRValue(compiler, localEnv, currentClass, t);
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        expression.decompile(s);
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print(" = ");
        expression.decompileToJava(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenInitialization(DecacCompiler compiler, DAddr address) {
        DVal dVal = expression.getDVal(compiler);
        if (dVal instanceof GPRegister) {
            compiler.addInstruction(new STORE((GPRegister) dVal, address));
        }
        else {
            compiler.addInstruction(new LOAD(dVal, Register.R1));
            compiler.addInstruction(new STORE(Register.R1, address));
        }
    }

    @Override
    protected boolean register0Used() {
        return expression.register0Used();
    }
}
