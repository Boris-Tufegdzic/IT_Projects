package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl51
 * @date 01/01/2024
 */
public class ListExpr extends TreeList<AbstractExpr> {

    @Override
    public void decompile(IndentPrintStream s) {
        for(AbstractExpr expr : this.getList()){
            expr.decompile(s);
            s.println();
        }
    }

    
    public void decompileAsParameters(IndentPrintStream s) {
        for(AbstractExpr expr : this.getList()){
            expr.decompile(s);
        }
    }

    public void decompileToJava(IndentPrintStream s) {
        for(AbstractExpr expr : this.getList()){
            expr.decompileToJava(s);
            s.println();
        }
    }

    public void decompileToJavaAsParameters(IndentPrintStream s) {
        int compt = 0;
        for (AbstractExpr c : getList()) {
            if(compt > 0){
                s.print(", ");
            }
            c.decompileToJava(s);
            compt++;
        }
    }
}
