package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Tree;

public abstract class AbstractDeclField extends Tree {
    protected abstract EnvironmentExp verifyDeclField(DecacCompiler compiler,
                                                       SymbolTable.Symbol extensionClass, SymbolTable.Symbol currentClass, int index)
            throws ContextualError;

    protected abstract void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp localEnv, SymbolTable.Symbol currentClass) throws ContextualError;

    protected abstract void codeGenDeclField(DecacCompiler compiler);

    protected abstract void codeGenInitField(DecacCompiler compiler);
}
