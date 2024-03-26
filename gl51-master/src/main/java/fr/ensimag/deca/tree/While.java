package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.commons.lang.Validate;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addComment("while loop");
        Label beginWhileLabel = compiler.newLabel("beginWhile");
        if (condition.hasIdentifier()){
            Label endWhileLabel = compiler.newLabel("endWhile");
            //Test condition
            compiler.addLabel(beginWhileLabel);
            compiler.addComment("test condition of while");
            DVal dVal = condition.getDVal(compiler);
            if (!dVal.equals(Register.R1)) compiler.addInstruction(new LOAD(dVal, Register.R1));
            compiler.addInstruction(new CMP(1, Register.R1));
            compiler.addInstruction(new BNE(endWhileLabel));
            //Body
            body.codeGenListInst(compiler);
            compiler.addInstruction(new BRA(beginWhileLabel));
            compiler.addLabel(endWhileLabel);
        }
        else { //Creating the test and all the branches is useless, since it is already known at compilation that condition is always true or always false
            Boolean dVal = (Boolean) condition.getLiteralValue();
            if (dVal) { //Then branch
                compiler.addComment("condition always true, loop forever");
                body.codeGenListInst(compiler);
                compiler.addInstruction(new BRA(beginWhileLabel));
            }
            else { //Else branch
                compiler.addComment("condition always false, skipped loop");
            }
        }
        compiler.addComment("end of while loop");
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        condition.verifyCondition(compiler, localEnv, currentClass);
        body.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    public void decompileToJava(IndentPrintStream s) {
        s.print("___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___BOOLEAN_VALUE = ");
        getCondition().decompileToJava(s);
        s.print(";");
        s.println();
        s.println("while (___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___BOOLEAN_VALUE) {");
        s.indent();
        getBody().decompileToJava(s);
        s.print("___reff58e4f6se4rs54gr86eruugrioutilsoeksfiougq87546qsd5dqzeg___BOOLEAN_VALUE = ");
        getCondition().decompileToJava(s);
        s.print(";");
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
