package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;

import java.io.PrintStream;

/**
 * Empty main Deca program
 *
 * @author gl51
 * @date 01/01/2024
 */
public class EmptyMain extends AbstractMain {
    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        //Do nothing
    }
    protected void verifyBloc(DecacCompiler compiler, EnvironmentExp localEnv, EnvironmentExp envExpParam, SymbolTable.Symbol currentClass, Type returnType) throws ContextualError{
        //Do nothing
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        //Nothing to be done
    }

    /**
     * Contains no real information => nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{}");
    }

    @Override
    public void decompileAsBloc(IndentPrintStream s) {
        
    }

    public void decompileToJava(IndentPrintStream s) {
        s.println("public class Main {");
        s.indent();
        s.println("public static void main(String[] args) {}");
        s.unindent();
        s.println("}");
    }

    public void decompileToJavaAsBloc(IndentPrintStream s){
        //Nothing
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
    protected int getNumberPush() {
        return 0;
    }

    @Override
    protected void codeGenMainBloc(DecacCompiler compiler) {
        codeGenMain(compiler);
    }
}
