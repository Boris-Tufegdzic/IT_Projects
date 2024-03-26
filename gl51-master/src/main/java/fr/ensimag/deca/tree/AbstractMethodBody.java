package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.context.Type;

public abstract class AbstractMethodBody extends Tree{
    protected abstract void codeGenMethodBody(DecacCompiler compiler);

    protected abstract void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, EnvironmentExp envExpParam, SymbolTable.Symbol currentClass, Type returnType) throws ContextualError;

    /**
     * @return  the number of elements that will be pushed into the stack
     */
    protected abstract int getNumberPush();
}