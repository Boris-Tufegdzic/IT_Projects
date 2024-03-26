package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclField extends AbstractDeclField {
    final private AbstractIdentifier typeName;
    private AbstractIdentifier fieldName;

    final private Visibility visibility;
    final private AbstractInitialization init;

    public DeclField(AbstractIdentifier typeName, AbstractIdentifier fieldName, Visibility visibility, AbstractInitialization init){
        Validate.notNull(typeName);
        Validate.notNull(fieldName);
        Validate.notNull(visibility);
        Validate.notNull(init);
        this.typeName = typeName;
        this.fieldName = fieldName;
        this.visibility = visibility;
        this.init = init;
    }
    public void decompile(IndentPrintStream s) {
        if(visibility.equals(Visibility.PUBLIC)){
            s.print("public");
        }
        else{
            s.print("protected");
        }
        s.print(" ");
        typeName.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        init.decompile(s);
        s.print(";");
    }

    public void decompileToJava(IndentPrintStream s) {
        if(visibility.equals(Visibility.PUBLIC)){
            s.print("public");
        }
        else{
            s.print("protected");
        }
        s.print(" ");
        typeName.decompileToJava(s);
        s.print(" ");
        fieldName.decompileToJava(s);
        init.decompileToJava(s);
        s.print(";");
    }

    protected EnvironmentExp verifyDeclField(DecacCompiler compiler,
                                             SymbolTable.Symbol extensionClass, SymbolTable.Symbol currentClass, int index)
            throws ContextualError {
        //RULE 2.5
        //Here we check if the type of the field is defined in the environmentType
        Type type = typeName.verifyType(compiler);
        //We check if the type of the field is Void
        if(type.isVoid()){
            throw new ContextualError( "The type of field"+fieldName.getName().getName()+"cannot be void", this.getLocation());
        }
        if(compiler.environmentType.contains(extensionClass)) {
            if (((ClassDefinition) compiler.environmentType.get(extensionClass)).getMembers().contains(fieldName.getName())) {
                if (!((ClassDefinition) compiler.environmentType.get(extensionClass)).getMembers().get(fieldName.getName()).isField()) {
                    throw new ContextualError("The identifier "+fieldName.getName().getName()+" in "+currentClass.getName()+"is already defined in the super class but his type is not field", this.getLocation());
                }
            }
        }
        //Verify initialization
        ClassDefinition currentClassDef = (ClassDefinition) compiler.environmentType.get(currentClass);
        EnvironmentExp localEnv = currentClassDef.getMembers();
        init.verifyInitialization(compiler, type, localEnv, currentClassDef);
        //New environment
        EnvironmentExp envExpf = new EnvironmentExp(null);
        //We put the definition of the current class
        fieldName.setDefinition(new FieldDefinition(type, this.getLocation(), visibility, currentClassDef, index, init));
        //(AbstractLValue) fieldName = new Selection(new This() , fieldName) ;
        envExpf.put(fieldName.getName(), fieldName.getFieldDefinition());
        return envExpf;
    }

    protected void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp localEnv, SymbolTable.Symbol currentClass) throws ContextualError {
        //RULE 3.7
        Type type = typeName.verifyType(compiler);
        init.verifyInitialization(compiler, type, localEnv, (ClassDefinition) compiler.environmentType.get(currentClass));
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.typeName.prettyPrint(s, prefix, false);
        if(this.visibility == Visibility.PROTECTED) s.print(" protected ");
        else s.print(" public ");
        this.fieldName.prettyPrint(s, prefix, false);
        this.init.prettyPrint(s, prefix, true);
    }
    @Override
    protected void iterChildren(TreeFunction f) {
        this.typeName.iter(f);
        this.fieldName.iter(f);
        this.init.iter(f);
    }

    @Override
    protected void codeGenDeclField(DecacCompiler compiler) {
        if (typeName.getType().isString()) {
            if (!(init instanceof Initialization)) fieldName.getFieldDefinition().setStringValue("");
            else if (((Initialization) init).getExpression() instanceof StringLiteral) fieldName.getFieldDefinition().setStringValue(((StringLiteral) ((Initialization) init).getExpression()).getValue());
            else fieldName.getFieldDefinition().setStringValue(((Identifier) ((Initialization) init).getExpression()).getDefinition().getStringValue());
        }
    }

    @Override
    protected void codeGenInitField(DecacCompiler compiler) {
        fieldName.getFieldDefinition().setOperand(new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.R0));
        if (init instanceof Initialization) {
            DVal dVal = ((Initialization) init).getExpression().getDVal(compiler);
            if (dVal.equals(Register.R1)) {
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
                compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.R0)));
            }
            else {
                compiler.addInstruction(new LOAD(dVal, Register.R0));
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
                compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.R1)));
            }
        }
        else {
            if (typeName.getType().isInt() || typeName.getType().isBoolean()) compiler.addInstruction(new LOAD(0, Register.R1));
            else if (typeName.getType().isFloat()) compiler.addInstruction(new LOAD(new ImmediateFloat(0), Register.R1));
            else if (typeName.getType().isClass()) compiler.addInstruction(new LOAD(new NullOperand(), Register.R1));
            else throw new UnsupportedOperationException("not implemented yet");
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
            compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.R0)));
        }
    }
}

