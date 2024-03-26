package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
/**
 * Main block of a Deca program.
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractMain extends Tree {

    protected abstract void codeGenMain(DecacCompiler compiler);

    protected abstract void codeGenMainBloc(DecacCompiler compiler);


    /**
     * Implements non-terminal "main" of [SyntaxeContextuelle] in pass 3 
     */
    protected abstract void verifyMain(DecacCompiler compiler) throws ContextualError;

    protected abstract void verifyBloc(DecacCompiler compiler, EnvironmentExp localEnv, EnvironmentExp envExpParam, SymbolTable.Symbol currentClass, Type returnType) throws ContextualError;

    public abstract void decompileToJavaAsBloc(IndentPrintStream s);
    public abstract void decompileAsBloc(IndentPrintStream s);

    /**
     * @return  the number of elements that will be pushed into the stack
     */
    protected abstract int getNumberPush();
}
