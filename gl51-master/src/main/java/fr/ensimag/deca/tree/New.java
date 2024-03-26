package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
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
public class New extends AbstractExpr {

    private final AbstractIdentifier type;

    public New(AbstractIdentifier type) {
        Validate.notNull(type);
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type type1 = type.verifyType(compiler);
        if(!type1.isClass()){
            throw new ContextualError("Trying to use New method on a non class object", this.getLocation());
        }
        this.setType(type1);
        return type1;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getDVal(compiler);
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        s.print("new ");
        type.decompileToJava(s);
        s.print("()");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        type.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
    }

    @Override
    protected boolean register0Used() {
        return true;
    }

    @Override
    protected boolean hasIdentifier() {
        return true;
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        compiler.addInstruction(new NEW(type.getClassDefinition().getNumberOfFields() + 1, Register.R0));
        compiler.getErrors().codeGenCheckHeapOverflow();
        compiler.addInstruction(new LEA(new RegisterOffset(type.getClassDefinition().getGBOffset(), Register.GB), Register.R1));
        compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(0, Register.R0)));
        compiler.addInstruction(new PUSH(Register.R0));
        compiler.addInstruction(new BSR(type.getClassDefinition().getInitLabel()));
        compiler.addInstruction(new POP(Register.R1));
        /*
        for (int i = 0; i < type.getClassDefinition().getNumberOfFields(); i++) {
            for (SymbolTable.Symbol c : type.getClassDefinition().getMembers().getEnvExp().keySet()) {
                if (type.getClassDefinition().getMembers().get(c).isField()) {
                    FieldDefinition fieldDefinition = ((FieldDefinition) type.getClassDefinition().getMembers().get(c));
                    if (fieldDefinition.getInit() instanceof Initialization) {
                        compiler.addInstruction(new LOAD(((Initialization) fieldDefinition.getInit()).getExpression().getDVal(compiler), Register.R0));
                        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(fieldDefinition.getIndex(), Register.R1)));
                    }
                }
            }
        }
         */
        return Register.R1;
    }
}