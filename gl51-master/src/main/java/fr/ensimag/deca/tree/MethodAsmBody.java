package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.DecacFatalError;
import fr.ensimag.deca.codegen.AsmBodyInstruction;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractMethodBody;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.UnaryInstruction;
import org.apache.commons.lang.Validate;

import java.io.*;
import java.util.Scanner;

public class MethodAsmBody extends AbstractMethodBody{
    private final StringLiteral bloc;

    private int numberPush;

    public MethodAsmBody(StringLiteral bloc){
        Validate.notNull(bloc);
        this.bloc = bloc;
        this.numberPush = 0;
    }

    @Override
    public void decompile(IndentPrintStream s){
        bloc.decompile(s);
    }
    @Override
    public void decompileToJava(IndentPrintStream s){
        bloc.decompileToJava(s);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        bloc.prettyPrintChildren(s, prefix);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        bloc.iter(f);
    }


    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, EnvironmentExp envExpParam, SymbolTable.Symbol currentClass, Type returnType) throws ContextualError {
        bloc.setType(compiler.environmentType.STRING);
    }

    protected void codeGenMethodBody(DecacCompiler compiler){
        compiler.addLabel(compiler.newLabel("assemblyMethod"));
        compiler.addInstruction(new AsmBodyInstruction(new ImmediateString(bloc.getValue())));
        numberPush = bloc.getValue().length() - bloc.getValue().replace("PUSH", "PSH").length();
    }

    @Override
    protected int getNumberPush() {
        return numberPush;
    }
}