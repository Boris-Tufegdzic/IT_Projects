package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractDeclClass;
import fr.ensimag.deca.tree.AbstractDeclField;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.AbstractDeclParam;
import fr.ensimag.deca.tree.TreeList;
import org.apache.log4j.Logger;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {

    @Override
    public void decompile(IndentPrintStream s) {
        int compt = 0;
        for (AbstractDeclParam c : getList()) {
            if(compt > 0){
                s.print(", ");
            }
            c.decompile(s);
            compt++;

        }
    }

    public void decompileToJava(IndentPrintStream s) {
        int compt = 0;
        for (AbstractDeclParam c : getList()) {
            if(compt > 0){
                s.print(", ");
            }
            c.decompileToJava(s);
            compt++;

        }
    }

    public Signature verifyListParams(DecacCompiler compiler) throws ContextualError {
        //RULE 2.8
        Signature sign = new Signature();
        for (AbstractDeclParam declParam : this.getList()) {
            Type newParamType = declParam.verifyDeclParam(compiler);
            sign.add(newParamType);
        };
        return sign;
    }
    protected EnvironmentExp verifyListParamsBody(DecacCompiler compiler) throws ContextualError{
        //RULE 3.12
        EnvironmentExp envExpParam = new EnvironmentExp(null);
        for(AbstractDeclParam declParam : this.getList()){
            EnvironmentExp newEnvParam = declParam.verifyDeclParamBody(compiler);
            envExpParam.UnionDisjointe(newEnvParam, this.getLocation());
        }
        return envExpParam;
    }
}