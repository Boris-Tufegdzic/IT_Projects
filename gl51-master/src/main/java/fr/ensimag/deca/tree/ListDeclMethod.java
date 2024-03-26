package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractDeclClass;
import fr.ensimag.deca.tree.AbstractDeclField;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.TreeList;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    public void decompileToJava(IndentPrintStream s) {
        for (AbstractDeclMethod c : getList()) {
            c.decompileToJava(s);
            s.println();
        }
    }

    public EnvironmentExp verifyListMethods(DecacCompiler compiler,
                                            SymbolTable.Symbol extensionClass,
                                            SymbolTable.Symbol currentClass
                                            ) throws ContextualError {
        //RULE 2.6
        EnvironmentExp envExpm = new EnvironmentExp(((ClassDefinition) compiler.environmentType.get(extensionClass)).getMembers());
        //Initiating the index at the number of methods of the super class
        int index = ((ClassDefinition) compiler.environmentType.get(extensionClass)).getNumberOfMethods();
        int[] tabIndex = {index};
        //For each declMethod we calculate the environment containing the definition of the declared method and we add it to the envExpm
        for (AbstractDeclMethod declMethod : this.getList()) {
            EnvironmentExp newEnvExpm = declMethod.verifyDeclMethod(compiler, extensionClass, currentClass, tabIndex);
            envExpm.UnionDisjointe(newEnvExpm, this.getLocation());
            (((ClassDefinition) compiler.environmentType.get(currentClass)).getMembers()).UnionDisjointe(newEnvExpm, this.getLocation());
        };
        ((ClassDefinition) compiler.environmentType.get(currentClass)).setNumberOfMethods(tabIndex[0]);
        return envExpm;
    }

    protected void verifyListMethodsBody(DecacCompiler compiler, EnvironmentExp localEnv, SymbolTable.Symbol currentClass) throws ContextualError{
        //RULE 3.10
        for(AbstractDeclMethod declMethod : this.getList()){
            declMethod.verifyDeclMethodBody(compiler, localEnv, currentClass);
        }
    }


}