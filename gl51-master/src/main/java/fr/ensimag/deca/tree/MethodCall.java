package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.AbstractMethodCall;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MethodCall extends AbstractMethodCall{
    private AbstractExpr expr;

    private final AbstractIdentifier methodIdentifier;

    private final ListExpr rValueStar;

    public MethodCall(AbstractExpr expr, AbstractIdentifier methodIdentifier, ListExpr rValueStar){
        Validate.notNull(methodIdentifier);
        Validate.notNull(rValueStar);
        Validate.notNull(expr);
        this.expr = expr;
        this.methodIdentifier = methodIdentifier;
        this.rValueStar = rValueStar;
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError{
        Type type1 = expr.verifyExpr(compiler, localEnv, currentClass);
        if (!type1.isClass()) {
            throw new ContextualError("Trying to call a method on a non class object", this.getLocation());
        }
        expr.setType(type1);
        EnvironmentExp members = ((ClassDefinition)compiler.environmentType.get(expr.getType().getName())).getMembers();
        if(!members.contains(methodIdentifier.getName())){
            throw new ContextualError("Non-existent method identifier", this.getLocation());
        }
        if(!members.get(methodIdentifier.getName()).isMethod()){
            throw new ContextualError(methodIdentifier.getName().getName() + "is not a method identifier", this.getLocation());
        }
        MethodDefinition methodDef = (MethodDefinition) members.get(methodIdentifier.getName());
        methodIdentifier.setDefinition(methodDef);
        methodIdentifier.setType(methodDef.getType());
        Signature sig = methodDef.getSignature();
        Type returnType = methodDef.getType();
        //We check if the types of the list of parameters contained in rValueStar matches the list of types of sig
        if(rValueStar.getList().size() != sig.size()){
            throw new ContextualError("Incorrect number of parameters in a method call", this.getLocation());
        }
        for(int i = 0; i<sig.size(); i++){
            Type type = rValueStar.getList().get(i).verifyExpr(compiler, localEnv, currentClass);
        }
        for(int i = 0; i < sig.size(); i++){
            if(!rValueStar.getList().get(i).getType().isSubType(sig.getArgs().get(i), compiler)){
                throw new ContextualError("Incorrect parameter type in a method call", this.getLocation());
            }
            rValueStar.getList().get(i).setType(sig.getArgs().get(i));
        }
        this.setType(returnType);
        return returnType;
    }

    @Override
    public void decompile(IndentPrintStream s){
        if(expr != null){
            expr.decompile(s);
            s.print(".");
        }
        methodIdentifier.decompile(s);
        s.print("(");
        rValueStar.decompileAsParameters(s);
        s.print(")");
    }

    @Override
    public void decompileToJava(IndentPrintStream s){
        if(expr != null) {
            expr.decompileToJava(s);
            s.print(".");
        }
        methodIdentifier.decompileToJava(s);
        s.print("(");
        rValueStar.decompileToJavaAsParameters(s);
        s.print(")");
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        expr.prettyPrintChildren(s, prefix);
        methodIdentifier.prettyPrintChildren(s, prefix);
        rValueStar.prettyPrintChildren(s, prefix);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        methodIdentifier.iter(f);
        rValueStar.iter(f);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal dVal = getDVal(compiler);
        if (!(dVal.equals(Register.R1))) compiler.addInstruction(new LOAD(dVal, Register.R1));
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        compiler.addComment("Method call");

        List<AbstractExpr> list = rValueStar.getList();
        for (int i = list.size() - 1; i >= 0; i--) {
            DVal dVal = list.get(i).getDVal(compiler);
            if (!(dVal instanceof GPRegister)) {
                compiler.addInstruction(new LOAD(dVal, Register.R1));
                compiler.addInstruction(new PUSH(Register.R1));
            }
            else compiler.addInstruction(new PUSH((GPRegister) dVal));
        }

        DVal dVal = expr.getDVal(compiler);
        if (dVal.equals(Register.R1)) {
            compiler.addInstruction(new PUSH(Register.R1));
        }
        else if (dVal instanceof GPRegister) {
            compiler.addInstruction(new PUSH((GPRegister) dVal));
        }
        else {
            compiler.addInstruction(new LOAD(dVal, Register.R1));
            compiler.addInstruction(new PUSH(Register.R1));
        }

        compiler.addInstruction(new LOAD(expr.getDVal(compiler), Register.getR(2)));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(2)), Register.getR(2)));
        compiler.addInstruction(new BSR(new RegisterOffset(methodIdentifier.getMethodDefinition().getIndex(), Register.getR(2))));
        compiler.addInstruction(new SUBSP(1 + methodIdentifier.getMethodDefinition().getSignature().getArgs().size()));
        compiler.addComment("End of method call");
        return Register.R1;
    }

    @Override
    protected boolean hasIdentifier() {
        return true;
    }

    @Override
    protected boolean register0Used() {
        return true;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else if (getType().isFloat()) compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        codeGenInst(compiler);
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else if (getType().isFloat()) compiler.addInstruction(new WFLOATX());
    }

    @Override
    protected int getNumberPush() {
         return 1 + methodIdentifier.getMethodDefinition().getSignature().getArgs().size() + methodIdentifier.getNumberPush();
    }
}