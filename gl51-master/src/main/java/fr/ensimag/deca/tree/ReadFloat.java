package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class ReadFloat extends AbstractReadExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readFloat()");
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        s.print("new Scanner(System.in).nextFloat()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void codeGenRead(DecacCompiler compiler) {
        compiler.addInstruction(new RFLOAT());
        compiler.getErrors().codeGenCheckInputError();
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenRead(compiler);
        compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        codeGenRead(compiler);
        compiler.addInstruction(new WFLOATX());
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        compiler.addInstruction(new RFLOAT());
        return Register.R1;
    }

    @Override
    protected boolean hasIdentifier() {
        return true;
    }
}
