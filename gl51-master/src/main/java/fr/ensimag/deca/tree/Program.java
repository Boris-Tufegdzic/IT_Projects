package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        //RULE 1.1
        //Pass 1
        classes.verifyListClass(compiler);
        //Pass 2
        classes.verifyListClassMembers(compiler);
        //Pass 3
        classes.verifyListClassBody(compiler);
        //Main
        main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        int numberPush = main.getNumberPush();
        for (AbstractDeclClass a : classes.getList()) numberPush += a.getNumberPush();
        compiler.getErrors().codeGenCheckStackOverflow(numberPush);

        compiler.addComment("Main program");
        Label mainLabel = compiler.newLabel("main");
        compiler.addComment("------------------------------");
        compiler.addComment("----- class declarations -----");
        compiler.addComment("------------------------------");

        //Object class
        int GBOffset = compiler.incrementStack(2); //Should return 1(GB)
        compiler.environmentType.OBJECT.getDefinition().setGBOffset(GBOffset);
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R1));
        compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(1, Register.GB)));
        //Object.equals
        MethodDefinition equalsDefinition = (MethodDefinition) ((ClassDefinition) compiler.environmentType.get(compiler.createSymbol("Object"))).getMembers().get(compiler.createSymbol("equals"));
        equalsDefinition.setLabel(compiler.newLabel("Object.equals"));
        equalsDefinition.setOperand(new RegisterOffset(2, Register.GB));
        compiler.addInstruction(new LOAD(new LabelOperand(equalsDefinition.getLabel()), Register.R1));
        compiler.addInstruction(new STORE(Register.R1, equalsDefinition.getOperand()));

        //Other classes
        for (AbstractDeclClass a : classes.getList()) {
            a.codeGenClassesInMemory(compiler);
        }
        compiler.addInstruction(new BRA(mainLabel));

        compiler.addComment("-----------------------------");
        compiler.addComment("---------- classes ----------");
        compiler.addComment("-----------------------------");
        //Object
        compiler.addComment("class Object");
        compiler.addComment("--------");
        Label labelEqualsFalse = compiler.newLabel("equalsFalse");
        compiler.addLabel(equalsDefinition.getLabel());
        compiler.addInstruction(new LEA(new RegisterOffset(1, Register.GB), Register.R1));
        compiler.addInstruction(new CMP(new RegisterOffset(0, Register.SP), Register.R1));
        compiler.addInstruction(new BNE(labelEqualsFalse));
        compiler.addInstruction(new LOAD(1, Register.R1));
        compiler.addInstruction(new RTS());
        compiler.addLabel(labelEqualsFalse);
        compiler.addInstruction(new LOAD(0, Register.R1));
        compiler.addInstruction(new RTS());
        compiler.addComment("--------");

        //Other classes
        for (AbstractDeclClass a : classes.getList()) {
            a.codeGenDeclClass(compiler);
        }

        compiler.addComment("-----------------------------");
        compiler.addComment("----- main instructions -----");
        compiler.addComment("-----------------------------");
        compiler.addLabel(mainLabel);
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());
        compiler.getErrors().codeGenErrors();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }

    @Override
    public void decompileToJava(IndentPrintStream s) {
        //Import library to use ReadInt()
        s.println("import java.util.Scanner;");
        getClasses().decompileToJava(s);
        getMain().decompileToJava(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
