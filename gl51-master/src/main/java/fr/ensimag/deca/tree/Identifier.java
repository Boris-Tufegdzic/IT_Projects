package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca Identifier
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Identifier extends AbstractIdentifier {

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + "at "+this.getLocation().toString() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    @Override
    public ParamDefinition getParamDefinition() {
        try {
            return (ParamDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a parameter identifier, you can't call getParamDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        ExpDefinition identifierDef = localEnv.get(this.getName());
        if(identifierDef == null && currentClass.getType() != null){
            identifierDef = ((ClassDefinition) compiler.environmentType.get(currentClass.getType().getName())).getMembers().get(this.getName());
        }
        if(identifierDef == null){
            throw new ContextualError("Unknown identifier "+this.getName().getName()+",", this.getLocation());
        }
        setDefinition(identifierDef);
        setType(identifierDef.getType());
        return identifierDef.getType();
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        //We check if the type is in the environment
        if(!compiler.environmentType.contains(getName())) {
            throw new ContextualError("Unknown type identifier", this.getLocation());
        }
        setDefinition(compiler.environmentType.get(this.getName()));
        Type idType = compiler.environmentType.get(this.getName()).getType();
        setType(idType);
        return idType;
    }



    
    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    public void decompileToJava(IndentPrintStream s) {
        if (name.toString().equals("string")) s.print("String");
        else s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        if (getDefinition().getType().isString()) { //String variables are eliminated during compilation
            compiler.addInstruction(new WSTR(getDefinition().getStringValue()));
            return;
        }
        loadInRegister(compiler, Register.R1);
        if (getDefinition().getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        if (getDefinition().getType().isString()) { //String variables are eliminated during compilation
            compiler.addInstruction(new WSTR(getDefinition().getStringValue()));
            return;
        }
        loadInRegister(compiler, Register.R1);
        if (getDefinition().getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOATX());
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        if (getDefinition().isField()) return getFieldDefinition().getOperand();
        else if (getDefinition().isParam()) return getParamDefinition().getOperand();
        else if (getDefinition().isMethod()) {
            compiler.addInstruction(new BSR(getMethodDefinition().getLabel()));
            return Register.R1;
        }
        return getVariableDefinition().getOperand();
    }

    @Override
    protected void loadInRegister(DecacCompiler compiler, GPRegister register) {
        DVal dVal = getDVal(compiler);
        if (!dVal.equals(register)) compiler.addInstruction(new LOAD(dVal, register));
    }

    @Override
    protected boolean hasIdentifier() {
        return true;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal dVal = getDVal(compiler);
        if (!dVal.equals(Register.R1)) compiler.addInstruction(new LOAD(dVal, Register.R1));
    }
}
