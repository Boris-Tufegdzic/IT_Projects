package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        for (AbstractExpr expr : arguments.getList()) {
            Type type = expr.verifyExpr(compiler, localEnv, currentClass);
            if (!type.isInt() && !type.isFloat() && !type.isString()) throw new ContextualError("Error: type of expression in print is neither int nor float nor string!", getLocation());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            if (this.getPrintHex()) a.codeGenPrintX(compiler);
            else a.codeGenPrint(compiler);
        }
    }

    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("print");
        s.print(getSuffix());
        if(getPrintHex()){
            s.print("x");
        }
        s.print("(");
        int l = 0;
        for(AbstractExpr expr : arguments.getList()){
            expr.decompile(s);
            if(l<arguments.getList().size()-1){
                s.print(",");
            }
            l++;
        }
        s.print(")");
    }


    public void decompileToJava(IndentPrintStream s) {
        s.print("System.out.print");
        if (arguments.size() == 0) s.print("ln"); //System.out.print() doesn't exist
        else s.print(getSuffix());
        s.print("(");
        int l = 0;
        for(AbstractExpr expr : arguments.getList()){
            if (getPrintHex()) s.print("___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___printHex("); //method defined in Program.decompileToJava
            expr.decompileToJava(s);
            if (getPrintHex()) s.print(")");
            if(l<arguments.getList().size()-1){
                s.print("+");
            }
            l++;
        }
        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
