package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * String literal
 *
 * @author gl51
 * @date 01/01/2024
 */
public class NullLiteral extends AbstractNullLiteral {

    @Override
    public Object getValue() {
        return null;
    }


    public NullLiteral() {
        super();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type type = new NullType(compiler.createSymbol("null"));
        this.setType(type);
        return type;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print("null");
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
    String prettyPrintNode() {
        return "null";
    }

    @Override
    protected Object getLiteralValue() {
        return null;
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        return new NullOperand();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R1));
    }
}
