package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Tree;
import fr.ensimag.deca.context.Type;

public abstract class AbstractDeclParam extends Tree {
    protected abstract Type verifyDeclParam(DecacCompiler compiler)
            throws ContextualError;

    protected abstract EnvironmentExp verifyDeclParamBody(DecacCompiler compiler) throws ContextualError;

    protected abstract void codeGenDeclParam(DecacCompiler compiler, int index);
}