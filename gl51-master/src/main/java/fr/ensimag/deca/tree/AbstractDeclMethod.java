package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Tree;

public abstract class AbstractDeclMethod extends Tree {
    protected abstract EnvironmentExp verifyDeclMethod(DecacCompiler compiler,
                                                       SymbolTable.Symbol extensionClass, SymbolTable.Symbol currentClass, int[] index)
            throws ContextualError;

    protected abstract void verifyDeclMethodBody(DecacCompiler compiler,
                                                 EnvironmentExp localEnv, SymbolTable.Symbol currentClass)
            throws ContextualError;

    protected abstract void codeGenMethodsInMemory(DecacCompiler compiler);

    protected abstract void codeGenDeclMethod(DecacCompiler compiler);

    protected abstract int getNumberPush();
}