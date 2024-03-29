package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Register;
import org.apache.commons.lang.Validate;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        Type type2 = verifyExpr(compiler, localEnv, currentClass);
        if(!expectedType.assignCompatible(type2, compiler)) {
            throw new ContextualError("Incompatible assignment", this.getLocation());
        }
        return this;
    }

    public AbstractExpr verifyReturnValue(DecacCompiler compiler,
                                     EnvironmentExp localEnv, ClassDefinition currentClass,
                                     Type expectedType)
            throws ContextualError {
        Type type2 = verifyExpr(compiler, localEnv, currentClass);
        if(!expectedType.assignCompatible(type2, compiler)) {
            throw new ContextualError("Wrong return type", this.getLocation());
        }
        return this;
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        Type type = verifyExpr(compiler, localEnv, currentClass);
        setType(type);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type returnedType = this.verifyExpr(compiler, localEnv, currentClass);
        if(!(returnedType.isBoolean())){
            throw new ContextualError("Non boolean expression in a condition statement", this.getLocation());
        }
        setType(returnedType);

    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Generate code to printx (hexadecimal print) the expression 
     * If not implemented by a sub-class, returns codeGenPrint 
     * @param compiler
     */   
    protected void codeGenPrintX(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }

    /**
     * @return  a DVal corresponding to the AbstractExpr. Has to be redefined in subclasses.
     * @param compiler     the compiler (useful if we have to add instructions, for example when we have identifiers)
     */
    protected DVal getDVal(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * @return  an Object (for example, an Integer or a Boolean) corresponding to the value of the AbstractExpr. Has to be redefined in subclasses.
     */
    protected Object getLiteralValue() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * @return  true if the expression (or a subexpression) contains an identifier or a readInt or a readFloat, false otherwise (in this case, the operation can be made directly during compilation)
     */
    protected boolean hasIdentifier() {
        throw new UnsupportedOperationException("not yet implemented");
    };

    /**
     * @return  true if the register 0 will be overwritten by the expression or a subexpression, false otherwise
     */
    protected boolean register0Used() {
        return false;
    }

    protected Definition getDefinition() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
