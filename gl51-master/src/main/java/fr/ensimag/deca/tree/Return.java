package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Return statement
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Return extends AbstractInst {

    private AbstractExpr rValue;

    public Return(AbstractExpr rValue) {
        Validate.notNull(rValue);
        this.rValue = rValue;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType)
            throws ContextualError{

        if(currentClass.getType() != null){
            if(rValue instanceof AbstractIdentifier){
                ExpDefinition fieldDef = currentClass.getMembers().get(((AbstractIdentifier) rValue).getName());
                if(fieldDef != null) {
                    if (fieldDef.isField()) {
                        rValue = new Selection(new This(), (AbstractIdentifier) rValue);
                    }
                }
            }
        }

        rValue.verifyReturnValue(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal dVal = rValue.getDVal(compiler);
        if (!dVal.equals(Register.R1)) compiler.addInstruction(new LOAD(dVal, Register.R1));
        compiler.addInstruction(new RTS());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        rValue.decompile(s);
        s.print(";");
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        s.print("return ");
        rValue.decompileToJava(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        rValue.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.rValue.prettyPrint(s, prefix, false);
    }

}