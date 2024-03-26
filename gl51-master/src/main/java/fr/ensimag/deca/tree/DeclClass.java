package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.ListDeclField;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.DecacCompiler;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl51
 * @date 01/01/2024
 */
public class DeclClass extends AbstractDeclClass {
    final private AbstractIdentifier currentClass;
    private AbstractIdentifier extensionClass;

    final private ListDeclField ListDeclField;

    final private ListDeclMethod ListDeclMethod;

    public DeclClass(AbstractIdentifier currentClass,
                     AbstractIdentifier extensionClass,
                     fr.ensimag.deca.tree.ListDeclField listDeclField,
                     fr.ensimag.deca.tree.ListDeclMethod listDeclMethod
                     ) {
        Validate.notNull(currentClass);
        this.currentClass = currentClass;
        Validate.notNull(listDeclField);
        Validate.notNull(listDeclMethod);
        ListDeclField = listDeclField;
        ListDeclMethod = listDeclMethod;
        this.extensionClass = extensionClass;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        currentClass.decompile(s);
        if(extensionClass != null){
            s.print(" extends ");
            extensionClass.decompile(s);
        }
        s.println(" {");
        s.indent();
        ListDeclField.decompile(s);
        ListDeclMethod.decompile(s);
        s.unindent();
        s.print("}");
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print("class ");
        currentClass.decompileToJava(s);
        if(extensionClass != null){
            s.print(" extends ");
            extensionClass.decompileToJava(s);
        }
        s.print(" {");
        s.indent();
        //Attributes and methods necessary to add to decompile successfully
        s.println("private static boolean ___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___BOOLEAN_VALUE;"); //Used for statements like "while(false)" or "if (0 != 0)", which javac refuses to compile (but is correct Deca code); so, we assign the value of the condition to this variable before testing it
        s.println("private static String ___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___printHex(Object x) {"); //Necessary method, since we don't know the type of expressions at the end of stage A!
        s.indent();
        s.println("if (x instanceof Integer) return Integer.toString((Integer) x); else if (x instanceof Float) return Float.toHexString((Float) x); else return (String) x;");
        s.unindent();
        s.println("}");
        ListDeclField.decompileToJava(s);
        ListDeclMethod.decompileToJava(s);
        s.unindent();
        s.print("}");
    }

    @Override
    //Rule 1.3
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        if(extensionClass == null){
            extensionClass = new Identifier(compiler.environmentType.OBJECT.getName());
            extensionClass.setType(compiler.environmentType.OBJECT.getDefinition().getType());
        }
        if(compiler.environmentType.contains(currentClass.getName())){
            throw new ContextualError("Redefinition of an already existing class", this.getLocation());
        }
        else if(!compiler.environmentType.contains(extensionClass.getName())){
            throw new ContextualError("Non-existent super class", this.getLocation());
        }
        else if(!(compiler.environmentType.get(extensionClass.getName()).isClass())){
            throw new ContextualError("Super class identifier must be a class type", this.getLocation());
        }
        else{
            //We add the definition of the currentClass in the environement
            ClassDefinition superClass = (ClassDefinition) compiler.environmentType.get(extensionClass.getName());
            ClassType currentClassType = new ClassType(currentClass.getName(), this.getLocation(), superClass);
            currentClass.setType(currentClassType);
            ClassDefinition currentClassDef = new ClassDefinition(currentClassType, this.getLocation(), superClass);
            currentClassDef.setNumberOfFields(ListDeclField.getList().size());
            currentClassDef.setNumberOfMethods(ListDeclMethod.getList().size());
            currentClass.setDefinition(currentClassDef);
            extensionClass.setDefinition(superClass);
            if (extensionClass.getName().equals(compiler.environmentType.OBJECT.getName())) extensionClass.getClassDefinition().setGBOffset(1);
            compiler.environmentType.put(currentClass.getName(), currentClassDef);
        }
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        //RULE 2.3
        ClassDefinition currentClassDef = (ClassDefinition) compiler.environmentType.get(currentClass.getName());
        EnvironmentExp envExpSuper = currentClassDef.getSuperClass().getMembers();
        EnvironmentExp envExpf = ListDeclField.verifyListFields(compiler, extensionClass.getName(), currentClass.getName());
        EnvironmentExp envExpm = ListDeclMethod.verifyListMethods(compiler, extensionClass.getName(), currentClass.getName());
        currentClassDef.getMembers().stack(envExpSuper);
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        ListDeclField.verifyListFieldsBody(compiler, ((ClassDefinition) compiler.environmentType.get(currentClass.getName())).getMembers(), currentClass.getName());
        EnvironmentExp methodBodyEnv = new EnvironmentExp(null);
        methodBodyEnv.stack(((ClassDefinition) compiler.environmentType.get(currentClass.getName())).getMembers());
        ListDeclMethod.verifyListMethodsBody(compiler, methodBodyEnv, currentClass.getName());
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        this.currentClass.prettyPrint(s, prefix, false);
        this.extensionClass.prettyPrint(s, prefix, false);
        this.ListDeclField.prettyPrint(s, prefix, false);
        this.ListDeclMethod.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        this.currentClass.iter(f);
        if(this.extensionClass != null) this.extensionClass.iter(f);
        this.ListDeclField.iter(f);
        this.ListDeclMethod.iter(f);
    }

    @Override
    protected void codeGenClassesInMemory(DecacCompiler compiler) {
        int GBOffset = compiler.incrementStack(currentClass.getClassDefinition().getNumberOfMethods() + 1);
        currentClass.getClassDefinition().setGBOffset(GBOffset);
        compiler.addComment("class " + currentClass.getName());
        compiler.addInstruction(new LEA(new RegisterOffset(extensionClass.getClassDefinition().getGBOffset(), Register.GB), Register.R1));
        compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(GBOffset, Register.GB)));
        currentClass.getClassDefinition().setInitLabel(compiler.newLabel("init"));

        ArrayList<ClassDefinition> superClassDefinitions = new ArrayList<>();
        ClassDefinition superClass = currentClass.getClassDefinition().getSuperClass();
        while (superClass != null) {
            EnvironmentExp inheritedMembers = ((ClassDefinition) compiler.environmentType.get(superClass.getType().getName())).getMembers();
            for (SymbolTable.Symbol s : inheritedMembers.getEnvExp().keySet()) { //Inherited methods and attributes
                if (inheritedMembers.get(s).isMethod()) { //But it is a method
                    MethodDefinition inheritedMethodDef = (MethodDefinition) inheritedMembers.get(s);
                    compiler.addInstruction(new LOAD(new LabelOperand(inheritedMethodDef.getLabel()), Register.R1));
                    compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(currentClass.getClassDefinition().getGBOffset() + inheritedMethodDef.getIndex(), Register.GB)));
                }
            }
            superClassDefinitions.add(superClass);
            superClass = superClass.getSuperClass();
        }
        for (AbstractDeclField f : ListDeclField.getList()) {
            f.codeGenDeclField(compiler);
        }
        for (AbstractDeclMethod m : ListDeclMethod.getList()) {
            m.codeGenMethodsInMemory(compiler);
        }
    }

    @Override
    protected void codeGenDeclClass(DecacCompiler compiler) {
        compiler.addComment("class " + currentClass.getName());
        compiler.addComment("--------");
        compiler.addComment("field initialization");
        compiler.addLabel(currentClass.getClassDefinition().getInitLabel());
        EnvironmentExp inheritedMembers = ((ClassDefinition) compiler.environmentType.get(extensionClass.getName())).getMembers();
        for(SymbolTable.Symbol s: inheritedMembers.getEnvExp().keySet()){
            if(inheritedMembers.get(s).isField()) {
                FieldDefinition inheritedFieldDef = (FieldDefinition) inheritedMembers.get(s);
                AbstractInitialization init = inheritedFieldDef.getInit();
                if (init instanceof Initialization) {
                    DVal dVal = ((Initialization) init).getExpression().getDVal(compiler);
                    if (dVal.equals(Register.R1)) {
                        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
                        compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(inheritedFieldDef.getIndex(), Register.R0)));
                    } else {
                        compiler.addInstruction(new LOAD(dVal, Register.R0));
                        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
                        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(inheritedFieldDef.getIndex(), Register.R1)));
                    }
                } else {
                    if (inheritedFieldDef.getType().isInt() || inheritedFieldDef.getType().isBoolean())
                        compiler.addInstruction(new LOAD(0, Register.R1));
                    else if (inheritedFieldDef.getType().isFloat())
                        compiler.addInstruction(new LOAD(new ImmediateFloat(0), Register.R1));
                    else if (inheritedFieldDef.getType().isClass())
                        compiler.addInstruction(new LOAD(new NullOperand(), Register.R1));
                    else throw new UnsupportedOperationException("not implemented yet");
                    compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
                    compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(inheritedFieldDef.getIndex(), Register.R0)));
                }
            }
        }
        for (AbstractDeclField f : ListDeclField.getList()) {
            f.codeGenInitField(compiler);
        }
        compiler.addInstruction(new RTS());
        compiler.addComment("--------");
        compiler.addComment("methods");
        for (AbstractDeclMethod m : ListDeclMethod.getList()) {
            m.codeGenDeclMethod(compiler);
        }
        compiler.addComment("--------");
    }

    @Override
    protected int getNumberPush() {
        int numberPush = 0;
        for (AbstractDeclMethod m : ListDeclMethod.getList()) {
            numberPush += m.getNumberPush();
        }
        return numberPush;
    }
}
