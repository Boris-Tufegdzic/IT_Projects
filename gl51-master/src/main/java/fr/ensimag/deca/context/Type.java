package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Deca Type (internal representation of the compiler)
 *
 * @author gl51
 * @date 01/01/2024
 */

public abstract class Type {


    /**
     * True if this and otherType represent the same type (in the case of
     * classes, this means they represent the same class).
     */
    public abstract boolean sameType(Type otherType);

    private final Symbol name;

    public Type(Symbol name) {
        this.name = name;
    }

    public Symbol getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName().toString();
    }

    public boolean isClass() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isVoid() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isClassOrNull() {
        return false;
    }

    /**
     * Returns the same object, as type ClassType, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     *
     * Can be seen as a cast, but throws an explicit contextual error when the
     * cast fails.
     */
    public ClassType asClassType(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }

    /**
     * @param other
     * @return true if this is a SubType of other
     */
    public boolean isSubType(Type other, DecacCompiler compiler){
        if(other.name == this.name){
            //Objects have the same type so in particular other is a subType of this
            return true;
        }
        else {
            if(this.isNull() && other.isClass()){
                return true;
            }
            if(this.isClass() && other.isClass()){
                return ((ClassType) this).isSubClassOf((ClassType) other, compiler);
            }
        }
        return false; //false by default ?
    }

    /**
     * T1: this, T2: other
     * @param other
     * @return true if we can assign a type "other" object to a type "this" object
     */
    public boolean assignCompatible(Type other, DecacCompiler compiler){
        /*  T1 (this) is float and T2 (other) is int */
        if (this.isFloat() && other.isInt()) {
            return true;

            /*or  T1 (this) is subtype of T2 (other) */
        } else if (other.isSubType(this, compiler)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param other
     * @return true if we can cast a "this" type object into a "other" type object.
     */
    public boolean castCompatible(Type other, DecacCompiler compiler){
        return this.assignCompatible(other, compiler) || other.assignCompatible(this, compiler);
    }

}
