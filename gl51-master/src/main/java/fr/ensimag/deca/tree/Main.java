package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;

import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl51
 * @date 01/01/2024
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    public ListDeclVar getDeclVariables(){return declVariables;}

    public ListInst getInsts(){return insts;}

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).
        ClassDefinition currentClass = new ClassDefinition(null, this.getLocation(), null);
        EnvironmentExp localEnv = new EnvironmentExp(null);
        this.declVariables.verifyListDeclVariable(compiler, localEnv, currentClass);
        this.insts.verifyListInst(compiler, localEnv, currentClass, new VoidType(null));
        LOG.debug("verify Main: end");
    }

    @Override
    protected void verifyBloc(DecacCompiler compiler, EnvironmentExp localEnv, EnvironmentExp envExpParam, SymbolTable.Symbol currentClass, Type returnType) throws ContextualError {
        //RULE 3.18
        ClassDefinition currentClassDefinition = (ClassDefinition) compiler.environmentType.get(currentClass);
        this.declVariables.verifyListDeclVariable(compiler, localEnv, currentClassDefinition);
        envExpParam.stack(localEnv);
        this.insts.verifyListInst(compiler, envExpParam, currentClassDefinition, returnType);
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        int i = compiler.initializeSP();
        for (AbstractDeclVar c : declVariables.getList()) { //Declare variables
            c.codeGenDeclVar(compiler);
        }
        compiler.decrementStack(i);
        compiler.initializeSP();
        insts.codeGenListInst(compiler);
    }

    @Override
    protected void codeGenMainBloc(DecacCompiler compiler) {
        for (AbstractDeclVar c : declVariables.getList()) { //Declare variables
            c.codeGenDeclVar(compiler);
        }
        insts.codeGenListInst(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    public void decompileAsBloc(IndentPrintStream s) {
        declVariables.decompile(s);
        insts.decompile(s);
    }

    public void decompileToJava(IndentPrintStream s) {
        s.println("public class Main {");
        s.indent();
        //Attributes and methods necessary to add to decompile successfully
        s.println("private static boolean ___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___BOOLEAN_VALUE;"); //Used for statements like "while(false)" or "if (0 != 0)", which javac refuses to compile (but is correct Deca code); so, we assign the value of the condition to this variable before testing it
        s.println("private static String ___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___printHex(Object x) {"); //Necessary method, since we don't know the type of expressions at the end of stage A!
        s.indent();
        s.println("if (x instanceof Integer) return Integer.toString((Integer) x); else if (x instanceof Float) return Float.toHexString((Float) x); else return (String) x;");
        s.unindent();
        s.println("}");

        s.println("public static void main(String[] args) {");
        s.indent();
        declVariables.decompileToJava(s);
        insts.decompileToJava(s);
        s.unindent();
        s.println("}");
        s.unindent();
        s.println("}");
    }

    public void decompileToJavaAsBloc(IndentPrintStream s){
        declVariables.decompileToJava(s);
        insts.decompileToJava(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }

    @Override
    protected int getNumberPush() {
        return insts.getNumberPush();
    }
}
