package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractDeclParam;
import fr.ensimag.deca.tree.TreeFunction;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclParam extends AbstractDeclParam {
    final private AbstractIdentifier typeName;
    final private AbstractIdentifier paramName;

    public DeclParam(AbstractIdentifier typeName, AbstractIdentifier paramName){
        Validate.notNull(typeName);
        Validate.notNull(paramName);
        this.typeName = typeName;
        this.paramName = paramName;
    }
    public void decompile(IndentPrintStream s) {
        typeName.decompile(s);
        s.print(" ");
        paramName.decompile(s);
    }

    public void decompileToJava(IndentPrintStream s) {
        typeName.decompileToJava(s);
        s.print(" ");
        paramName.decompileToJava(s);
    }
    @Override
    protected Type verifyDeclParam(DecacCompiler compiler)
            throws ContextualError{
        Type type = typeName.verifyType(compiler);
        if(type.isVoid()){
            throw new ContextualError("Parameter cannot be void in a method", this.getLocation());
        }
        return type;
    }

    protected EnvironmentExp verifyDeclParamBody(DecacCompiler compiler) throws ContextualError{
        EnvironmentExp envExpParam = new EnvironmentExp(null);
        Type type = typeName.verifyType(compiler);
        ParamDefinition paramDef = new ParamDefinition(type, this.getLocation());
        envExpParam.put(paramName.getName(), paramDef);
        paramName.setDefinition(paramDef);
        return envExpParam;
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.typeName.prettyPrint(s, prefix, false);
        this.paramName.prettyPrint(s, prefix, true);
    }
    @Override
    protected void iterChildren(TreeFunction f) {
        this.typeName.iter(f);
        this.paramName.iter(f);
    }

    @Override
    protected void codeGenDeclParam(DecacCompiler compiler, int index) {
        if (!paramName.getDefinition().getType().isString()) {
            RegisterOffset registerOffset = new RegisterOffset(- index - 3, Register.LB);
            paramName.getParamDefinition().setOperand(registerOffset);
        }
        else { //String identifiers are removed during compilation
            paramName.getParamDefinition().setStringValue("");
        }
    }
}