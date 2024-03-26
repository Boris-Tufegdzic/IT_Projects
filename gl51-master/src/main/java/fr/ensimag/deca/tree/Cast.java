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
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Cast extends AbstractExpr {

    private final AbstractIdentifier type;

    private final AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        Validate.notNull(type);
        Validate.notNull(expr);
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type type1 = type.verifyType(compiler);
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);
        if(!type1.castCompatible(type2, compiler)){
            throw new ContextualError("Incompatible cast", this.getLocation());
        }
        this.setType(type1);
        return type1;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal dVal = getDVal(compiler);
        if (!hasIdentifier()) compiler.addInstruction(new LOAD(dVal, Register.R1));
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        s.print("(");
        type.decompileToJava(s);
        s.print(") ");
        expr.decompileToJava(s);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(") (");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, false);
    }

    @Override
    protected boolean hasIdentifier() {
        return expr.hasIdentifier();
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        if (type.getType().isInt()) compiler.addInstruction(new INT(expr.getDVal(compiler), Register.R1));
        else if (type.getType().isFloat()) compiler.addInstruction(new FLOAT(expr.getDVal(compiler), Register.R1));
        else {
            RegisterOffset toLookFor = new RegisterOffset(type.getClassDefinition().getGBOffset(), Register.GB);
            DVal toLoad = expr.getDVal(compiler);
            compiler.getErrors().codeGenCheckCastError(toLookFor, toLoad);
            compiler.addInstruction(new LOAD(toLoad, Register.R1));
        }
        return Register.R1;
    }

    @Override
    protected Object getLiteralValue() {
        if (type.getType().isInt()) return (int) expr.getLiteralValue();
        else if (type.getType().isFloat()) return (float) expr.getLiteralValue();
        return expr;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        super.codeGenPrintX(compiler);
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOATX());
    }
}