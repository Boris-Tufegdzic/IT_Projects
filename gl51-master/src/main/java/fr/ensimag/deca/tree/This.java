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
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl51
 * @date 01/01/2024
 */
public class This extends AbstractExpr {
    private ClassDefinition currentClass;

    public This() {
        super();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        if(currentClass == null){
            throw new ContextualError("Using This method on a null Class", this.getLocation());
        }
        this.currentClass = currentClass;
        this.setType(currentClass.getType());
        return currentClass.getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getDVal(compiler);
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        s.print("this");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //Nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        return Register.R0;
    }

    @Override
    protected boolean register0Used() {
        return true;
    }
}