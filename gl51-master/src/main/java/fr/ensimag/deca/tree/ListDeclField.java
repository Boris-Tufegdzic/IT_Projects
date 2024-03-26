package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.log4j.Logger;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    public void decompileToJava(IndentPrintStream s) {
        for (AbstractDeclField c : getList()) {
            c.decompileToJava(s);
            s.println();
        }
    }

    public EnvironmentExp verifyListFields(DecacCompiler compiler,
                                           SymbolTable.Symbol extensionClass,
                                           SymbolTable.Symbol currentClass) throws ContextualError {
        //RULE 2.4
        EnvironmentExp envExpf = new EnvironmentExp(((ClassDefinition) compiler.environmentType.get(extensionClass)).getMembers());
        int index = ((ClassDefinition) compiler.environmentType.get(extensionClass)).getNumberOfFields();
        for (AbstractDeclField declField : this.getList()) {
            index++;
            EnvironmentExp newEnvExpf = declField.verifyDeclField(compiler, extensionClass, currentClass, index);
            (((ClassDefinition) compiler.environmentType.get(currentClass)).getMembers()).UnionDisjointe(newEnvExpf, this.getLocation());
        };
        ((ClassDefinition) compiler.environmentType.get(currentClass)).setNumberOfFields(index);
        return envExpf;
    }
    public void verifyListFieldsBody(DecacCompiler compiler, EnvironmentExp localEnv, SymbolTable.Symbol currentClass) throws ContextualError {
        //RULE 3.6
        for(AbstractDeclField declField : this.getList()){
            declField.verifyDeclFieldBody(compiler, localEnv, currentClass);
        }
    }
}