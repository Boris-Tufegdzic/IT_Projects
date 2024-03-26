package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;

import org.apache.commons.lang.Validate;

/**
 * Single precision, floating-point literal
 *
 * @author gl51
 * @date 01/01/2024
 */
public class FloatLiteral extends AbstractExpr {

    public float getValue() {
        return value;
    }

    private float value;

    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = compiler.environmentType.FLOAT;
        setType(type);
        return type;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(java.lang.Float.toHexString(value));
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print(java.lang.Float.toHexString(value) + "f");
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
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
    protected void codeGenPrint(DecacCompiler compiler ) {
        compiler.addInstruction(new LOAD(getDVal(compiler), Register.R1));
        compiler.addInstruction(new WFLOAT());
    }



    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(getDVal(compiler), Register.R1));
        compiler.addInstruction(new WFLOATX());
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        return new ImmediateFloat(value);
    }

    @Override
    protected Object getLiteralValue() {
        return value;
    }

    @Override
    protected boolean hasIdentifier() {
        return false;
    }
}
