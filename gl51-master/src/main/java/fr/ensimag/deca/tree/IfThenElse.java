package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl51
 * @date 01/01/2024
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        condition.verifyCondition(compiler, localEnv, currentClass);
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addComment("if structure");
        if (condition.hasIdentifier()) {
            Label elseLabel = compiler.newLabel("else");
            Label endifLabel = compiler.newLabel("endif");
            //Test condition
            compiler.addComment("test if condition");
            DVal dVal = condition.getDVal(compiler);
            if (!dVal.equals(Register.R1)) compiler.addInstruction(new LOAD(dVal, Register.R1));
            compiler.addInstruction(new CMP(1, Register.R1));
            compiler.addInstruction(new BNE(elseLabel));
            //Then branch
            compiler.addComment("then branch");
            thenBranch.codeGenListInst(compiler);
            compiler.addInstruction(new BRA(endifLabel));
            //Else branch
            compiler.addLabel(elseLabel);
            elseBranch.codeGenListInst(compiler);
            compiler.addLabel(endifLabel);
        }
        else { //Creating the test and all the branches is useless, since it is already known at compilation which branch we wil go to
            Boolean dVal = (Boolean) condition.getLiteralValue();
            if (dVal) { //Then branch
                compiler.addComment("then branch (resolved during compilation)");
                thenBranch.codeGenListInst(compiler);
            }
            else { //Else branch
                compiler.addComment("else branch (resolved during compilation)");
                elseBranch.codeGenListInst(compiler);
            }
        }
        compiler.addComment("end if structure");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.println(") {");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.println("}");
        s.println("else {");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("}");
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print("___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___BOOLEAN_VALUE = ");
        condition.decompileToJava(s);
        s.print(";");
        s.println();
        s.println("if (___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___BOOLEAN_VALUE) {");
        s.indent();
        thenBranch.decompileToJava(s);
        s.unindent();
        s.println("}");
        s.println("else {");
        s.indent();
        elseBranch.decompileToJava(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }

    public void setElseBranch(ListInst elseBranch) {
        this.elseBranch = elseBranch;
    }
}
