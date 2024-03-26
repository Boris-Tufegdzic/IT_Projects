package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractMethodBody;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;

public class MethodBody extends AbstractMethodBody{
    private final AbstractMain bloc;

    public MethodBody(AbstractMain bloc){
        Validate.notNull(bloc);
        this.bloc = bloc;
    }
    @Override
    public void decompile(IndentPrintStream s){
        bloc.decompileAsBloc(s);
    }
    @Override
    public void decompileToJava(IndentPrintStream s){
        bloc.decompileToJavaAsBloc(s);
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
        bloc.verifyBloc(compiler, localEnv, envExpParam, currentClass, returnType);
    }

    protected void codeGenMethodBody(DecacCompiler compiler){
        bloc.codeGenMainBloc(compiler);
    }

    @Override
    protected int getNumberPush() {
        return bloc.getNumberPush();
    }
}