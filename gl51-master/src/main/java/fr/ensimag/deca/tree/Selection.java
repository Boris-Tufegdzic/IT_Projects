package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.lang.reflect.Field;

public class Selection extends AbstractLValue{
    AbstractExpr expr;

    AbstractIdentifier fieldIdentifier;

    public Selection(AbstractExpr expr, AbstractIdentifier fieldIdentifier){
        Validate.notNull(expr);
        Validate.notNull(fieldIdentifier);
        this.expr = expr;
        this.fieldIdentifier = fieldIdentifier;
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);
        if (!type2.isClass()) {
            throw new ContextualError("Using a Selection on a non class Object", this.getLocation());
        }
        FieldDefinition fieldDef;
        // Case we are doing a selection into a class method
        if(currentClass.getType() != null) {
            //Contextual error checking
            if (!currentClass.getMembers().contains(fieldIdentifier.getName())) {
                throw new ContextualError(fieldIdentifier.getName().getName() + " used in a selection is not defined", this.getLocation());
            }
            if (!currentClass.getMembers().get(fieldIdentifier.getName()).isField()) {
                throw new ContextualError(fieldIdentifier.getName().getName() + " is used in a selection but it's not a field identifier", this.getLocation());
            }
            fieldDef = (FieldDefinition) currentClass.getMembers().get(fieldIdentifier.getName());
            expr.setType(currentClass.getType());
        }
        //Case we are doing a selection into the main block
        else{
            if(!(((ClassDefinition)compiler.environmentType.get(expr.getType().getName())).getMembers()).contains(fieldIdentifier.getName())){
                throw new ContextualError(fieldIdentifier.getName().getName() + " used in a selection is not defined", this.getLocation());
            }
            if(!((((ClassDefinition)compiler.environmentType.get(expr.getType().getName())).getMembers()).get(fieldIdentifier.getName()).isField())){
                throw new ContextualError(fieldIdentifier.getName().getName() + " is used in a selection but it's not a field identifier", this.getLocation());
            }
            fieldDef = (FieldDefinition) (((ClassDefinition)compiler.environmentType.get(expr.getType().getName())).getMembers()).get(fieldIdentifier.getName());
            expr.setType(((ClassDefinition)compiler.environmentType.get(expr.getType().getName())).getType());
        }
        //Now we can set the types and defs
        Visibility visib = fieldDef.getVisibility();
        Type fieldType = fieldDef.getType();
        fieldIdentifier.setDefinition(fieldDef);
        fieldIdentifier.setType(fieldType);
        this.setType(fieldType);

        ClassDefinition classField = fieldDef.getContainingClass();

        if(visib.equals(Visibility.PROTECTED)){
            if(currentClass.getType() == null){
                throw new ContextualError("Using a selection method on a protected field in main", this.getLocation());
            }
            else {
                if (!type2.isSubType(currentClass.getType(), compiler)) {
                    throw new ContextualError("Using a Selection method on a Class Object which is not a sub class of the current class", this.getLocation());
                }
                if (!currentClass.getType().isSubType(classField.getType(), compiler)) {
                    throw new ContextualError("Using a Selection method in a class which is not a sub class of the Field class", this.getLocation());
                }
            }
        }

        return fieldType;

    }
    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(".");
        fieldIdentifier.decompile(s);
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        expr.decompileToJava(s);
        s.print(".");
        fieldIdentifier.decompileToJava(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expr.iter(f);
        fieldIdentifier.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        //condition.prettyPrint(s, prefix, false);
        //thenBranch.prettyPrint(s, prefix, false);
        //elseBranch.prettyPrint(s, prefix, true);
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(getDVal(compiler), Register.R1));
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        DVal dVal = expr.getDVal(compiler);
        if (!(dVal instanceof Register)) compiler.getErrors().codeGenCheckNullDereference(dVal); //It is stored in Register.R1 if there is no error
        else if (!dVal.equals(Register.R1)) compiler.addInstruction(new LOAD(dVal, Register.R1));

        return new RegisterOffset(fieldIdentifier.getFieldDefinition().getIndex(), Register.R1);
    }

    @Override
    protected boolean hasIdentifier() {
        return true;
    }

    @Override
    protected Definition getDefinition() {
        return fieldIdentifier.getDefinition();
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
    protected boolean register0Used() {
        return expr.register0Used();
    }
}

