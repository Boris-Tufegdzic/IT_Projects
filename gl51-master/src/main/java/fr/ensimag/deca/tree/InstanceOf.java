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
public class InstanceOf extends AbstractExpr {

    private final AbstractIdentifier type;

    private final AbstractExpr expr;

    public InstanceOf(AbstractIdentifier type, AbstractExpr expr) {
        Validate.notNull(type);
        Validate.notNull(expr);
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type type1 = expr.verifyExpr(compiler, localEnv, currentClass);
        Type type2 = type.verifyType(compiler);
        if(!((type1.isClass() && type2.isClass()) || (type1.isNull() && type2.isClass()))){
            throw new ContextualError("type error in a instanceof expression", this.getLocation());
        }
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getDVal(compiler);
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        expr.decompileToJava(s);
        s.print(" instanceof ");
        type.decompileToJava(s);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(" instanceof ");
        type.decompile(s);
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
    protected DVal getDVal(DecacCompiler compiler) {
        Label beginLabel = compiler.newLabel("instanceofTest");
        Label endLabel = compiler.newLabel("endInstanceofTest");
        Label endEndLabel = compiler.newLabel("endEndInstanceofTest");
        RegisterOffset toLookFor = new RegisterOffset(type.getClassDefinition().getGBOffset(), Register.GB);
        DVal toLoad = expr.getDVal(compiler);
        compiler.addComment("beginning of instanceof test");
        compiler.addInstruction(new LOAD(toLoad, Register.R1));
        compiler.addInstruction(new LEA(toLookFor, Register.getR(2)));
        compiler.addInstruction(new CMP(Register.getR(2), Register.R1));
        compiler.addInstruction(new BEQ(endLabel));
        compiler.addLabel(beginLabel);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R1), Register.R1));
        compiler.addInstruction(new LEA(toLookFor, Register.getR(2)));
        compiler.addInstruction(new CMP(Register.getR(2), Register.R1));
        compiler.addInstruction(new BEQ(endLabel));
        compiler.addInstruction(new CMP(new NullOperand(), Register.R1));
        compiler.addInstruction(new BNE(beginLabel));
        compiler.addInstruction(new LOAD(0, Register.R1), "Result of instanceof is false");
        compiler.addInstruction(new BRA(endEndLabel));
        compiler.addLabel(endLabel);
        compiler.addInstruction(new LOAD(1, Register.R1), "Result of instanceof is true");
        compiler.addLabel(endEndLabel);
        return Register.R1;
    }

    @Override
    protected boolean hasIdentifier() {
        return true;
    }
}