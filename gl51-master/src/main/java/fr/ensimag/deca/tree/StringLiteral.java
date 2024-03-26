package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * String literal
 *
 * @author gl51
 * @date 01/01/2024
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = compiler.environmentType.STRING;
        setType(type);
        return type;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(new ImmediateString(value)));
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        codeGenPrint(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print('"');
        s.print(value);
        s.print('"');
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print('"');
        s.print(value);
        s.print('"');
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
        return "StringLiteral (" + value + ")";
    }

    @Override
    protected Object getLiteralValue() {
        return this.value;
    }

    @Override
    protected boolean hasIdentifier() {
        return false;
    }
}
