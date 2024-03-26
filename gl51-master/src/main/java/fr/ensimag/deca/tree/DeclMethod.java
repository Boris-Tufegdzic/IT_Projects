package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.TreeFunction;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclMethod extends AbstractDeclMethod {
    final private AbstractIdentifier typeName;
    final private AbstractIdentifier methodName;
    final private ListDeclParam listDeclParam;
    final private AbstractMethodBody methodBody;

    public DeclMethod(AbstractIdentifier typeName, AbstractIdentifier methodName, ListDeclParam listDeclParam, AbstractMethodBody methodBody){
        Validate.notNull(typeName);
        Validate.notNull(methodName);
        Validate.notNull(listDeclParam);
        Validate.notNull(methodBody);
        this.methodBody = methodBody;
        this.typeName = typeName;
        this.methodName = methodName;
        this.listDeclParam = listDeclParam;
    }

    public void decompile(IndentPrintStream s) {
        typeName.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        listDeclParam.decompile(s);
        s.print(")");
        s.println("{");
        s.indent();
        methodBody.decompile(s);
        s.unindent();
        s.println("}");
    }

    public void decompileToJava(IndentPrintStream s) {
        typeName.decompileToJava(s);
        s.print(" ");
        methodName.decompileToJava(s);
        s.print("(");
        listDeclParam.decompileToJava(s);
        s.print(")");
        s.print("{");
        s.indent();
        methodBody.decompileToJava(s);
        s.unindent();
        s.print("}");
    }
    @Override
    public EnvironmentExp verifyDeclMethod(DecacCompiler compiler,
                                    SymbolTable.Symbol extensionClass, SymbolTable.Symbol currentClass, int[] index)
            throws ContextualError{
        //RULE 2.7
        Type type = typeName.verifyType(compiler);
        //Here we get the signature of the method (list of types of the parameters)
        Signature sig = listDeclParam.verifyListParams(compiler);
        //Here we get the environmentExp of the super class
        EnvironmentExp envExtensionClass = ((ClassDefinition) compiler.environmentType.get(extensionClass)).getMembers();
        //We check if the method is defined in the super class
        if(envExtensionClass.contains(methodName.getName())){
            //We check if the object "methodName" defined in the superClass is a method
            if(!(envExtensionClass.get(methodName.getName()).isMethod())){
                throw new ContextualError("The method name already exist in the super class as a non-method object", this.getLocation());
            }
            //Here we get the signature of the method named "methodName" defined in the superClass
            Signature sig2 = ((MethodDefinition) envExtensionClass.get(methodName.getName())).getSignature();
            //Here we check if the signature of the overridden method is the same as the initial one in the superClass
            if(!(sig2.getArgs().equals(sig.getArgs()))){
                throw new ContextualError("Overridden method hasn't the same signature as the initial one", this.getLocation());
            }
            //We get the returned type of the initial method named "methodName" in the superClass
            Type otherType = ((MethodDefinition)(envExtensionClass.get(methodName.getName()))).getType();
            //We check if the returned type in the overidden method is a subType of the type returned in the initial method
            if(!(type.isSubType(otherType, compiler))){
                throw new ContextualError("The return type isn't a subType of the type returned in the inherited method", this.getLocation());
            }
        }
        else{
            index[0]++;
        }
        //Here we have checked all the potential contextual errors we can now create the new Environment
        //Je suis pas sûr pour les parents
        EnvironmentExp envExpm = new EnvironmentExp(null);
        ClassDefinition currentClassDef = (ClassDefinition) compiler.environmentType.get(currentClass);
        methodName.setDefinition(new MethodDefinition(type, this.getLocation(), sig, index[0], currentClassDef));
        envExpm.put(methodName.getName(), methodName.getMethodDefinition());
        return envExpm;
    }
    protected void verifyDeclMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, SymbolTable.Symbol currentClass) throws ContextualError {
        //RULE 3.11
        Type type = typeName.verifyType(compiler);
        EnvironmentExp envExpParam = this.listDeclParam.verifyListParamsBody(compiler);
        methodBody.verifyMethodBody(compiler, localEnv, envExpParam, currentClass, type);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.typeName.prettyPrint(s, prefix, false);
        this.methodName.prettyPrint(s, prefix, false);
        this.listDeclParam.prettyPrint(s, prefix, false);
        this.methodBody.prettyPrint(s, prefix, true);
    }
    @Override
    protected void iterChildren(TreeFunction f) {
        this.typeName.iter(f);
        this.methodName.iter(f);
        this.listDeclParam.iter(f);
        this.methodBody.iter(f);
    }

    @Override
    protected void codeGenMethodsInMemory(DecacCompiler compiler) {
        methodName.getMethodDefinition().setLabel(compiler.newLabel(methodName.getName().toString()));
        methodName.getMethodDefinition().setOperand(new RegisterOffset(methodName.getMethodDefinition().getContainingClass().getGBOffset() + methodName.getMethodDefinition().getIndex(), Register.GB));
        compiler.addInstruction(new LOAD(new LabelOperand(methodName.getMethodDefinition().getLabel()), Register.R1));
        compiler.addInstruction(new STORE(Register.R1, methodName.getMethodDefinition().getOperand()));
    }

    @Override
    protected void codeGenDeclMethod(DecacCompiler compiler) {
        int index = 0;
        for (AbstractDeclParam p : listDeclParam.getList()) {
            p.codeGenDeclParam(compiler, index);
            index++;
        }
        int numberPush = methodBody.getNumberPush();
        compiler.addLabel(methodName.getMethodDefinition().getLabel());
        compiler.getErrors().codeGenCheckStackOverflow(numberPush);
        methodBody.codeGenMethodBody(compiler);
        if (!typeName.getType().isVoid()) compiler.getErrors().codeGenCheckNoReturnInMethod();
        if (typeName.getType().isVoid()) compiler.addInstruction(new RTS()); //If the method is of type void, it may not end with a "return" statement in Deca. Thus, we add this statement, just to be sure
    }

    @Override
    protected int getNumberPush() {
        return methodBody.getNumberPush();
    }
}