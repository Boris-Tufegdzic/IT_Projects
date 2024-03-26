package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import org.apache.commons.lang.Validate;

import java.util.Scanner;

/**
 * Type defined by a class.
 *
 * @author gl51
 * @date 01/01/2024
 */
public class ClassType extends Type {
    
    protected ClassDefinition definition;
    
    public ClassDefinition getDefinition() {
        return this.definition;
    }
            
    @Override
    public ClassType asClassType(String errorMessage, Location l) {
        return this;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }

    /**
     * Standard creation of a type class.
     */
    public ClassType(Symbol className, Location location, ClassDefinition superClass) {
        super(className);
        this.definition = new ClassDefinition(this, location, superClass);
    }

    /**
     * Creates a type representing a class className.
     * (To be used by subclasses only)
     */
    protected ClassType(Symbol className) {
        super(className);
    }
    

    @Override
    public boolean sameType(Type otherType) {
        return otherType.isClass();
    }

    /**
     * Return true if potentialSuperClass is a superclass of this class.
     */
    //Implemented by us
    public boolean isSubClassOf(ClassType potentialSuperClass, DecacCompiler compiler) {

        if(this.definition.getSuperClass() != null) {
            if(this.definition.getSuperClass().getType().getName().equals(compiler.environmentType.OBJECT.getName())){
                return false;
            }
            else {
                if (this.definition.getSuperClass().getType().getName().equals(potentialSuperClass.getName())) {
                    return true;
                } else {
                    this.definition.getSuperClass().getType().isSubClassOf(potentialSuperClass, compiler);
                }
            }
        }
        return false;
    }


}
