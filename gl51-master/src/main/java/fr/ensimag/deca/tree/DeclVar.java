package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import org.apache.commons.lang.Validate;

/**
 * @author gl51
 * @date 01/01/2024
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        type.setType(type.verifyType(compiler));
        //We check if the type is void
        if(type.getType().isVoid()){
            throw new ContextualError("Variable type cannot be void", this.getLocation());
        }
        //We check if the variable isn't already defined
        if(localEnv.contains(varName.getName()) && !localEnv.get(varName.getName()).isField() && !localEnv.get(varName.getName()).isMethod()){
            throw new ContextualError("Variable already defined", this.getLocation());
        }
        initialization.verifyInitialization(compiler, type.getType(), localEnv, currentClass);

        //We create the definition of the new declared variable
        VariableDefinition newVarDef = new VariableDefinition(type.getType(), this.getLocation());

        varName.setDefinition(newVarDef);
        //We put the definition in the environment
        localEnv.put(varName.getName(), newVarDef);

    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.print(";");
    }

    public void decompileToJava(IndentPrintStream s) {
        type.decompileToJava(s);
        s.print(" ");
        varName.decompileToJava(s);
        initialization.decompileToJava(s);
        s.print(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenDeclVar(DecacCompiler compiler) {
        if (!varName.getDefinition().getType().isString()) {
            int GBOffset = compiler.incrementStack(1);

            RegisterOffset registerOffset = new RegisterOffset(GBOffset, Register.GB);
            varName.getVariableDefinition().setOperand(registerOffset);

            initialization.codeGenInitialization(compiler, registerOffset);
        }
        else { //String identifiers are removed during compilation
            if (!(initialization instanceof Initialization)) varName.getVariableDefinition().setStringValue("");
            else if (((Initialization) initialization).getExpression() instanceof StringLiteral) varName.getVariableDefinition().setStringValue(((StringLiteral) ((Initialization) initialization).getExpression()).getValue());
            else varName.getVariableDefinition().setStringValue(((Identifier) ((Initialization) initialization).getExpression()).getVariableDefinition().getStringValue());
        }
    }
}
