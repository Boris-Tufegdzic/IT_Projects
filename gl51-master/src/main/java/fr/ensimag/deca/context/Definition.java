package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of an identifier.
 * 
 * @author gl51
 * @date 01/01/2024
 */
public abstract class Definition {
    private String stringValue;

    @Override
    public String toString() {
        String res;
        res = getNature();
        if (location == Location.BUILTIN) {
            res += " (builtin)";
        } else {
            res += " defined at " + location;
        }
        res += ", type=" + type;
        return res;
    }

    public abstract String getNature();

    public Definition(Type type, Location location) {
        super();
        this.location = location;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private Location location;
    private Type type;
    public boolean isField() {
        return false;
    }
    
    public boolean isMethod() {
        return false;
    }

    public boolean isClass() {
        return false;
    }

    public boolean isParam() {
        return false;
    }

    /**
     * Return the same object, as type MethodDefinition, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     */
    public MethodDefinition asMethodDefinition(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }
    
    /**
     * Return the same object, as type FieldDefinition, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     */
    public FieldDefinition asFieldDefinition(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }

    public abstract boolean isExpression();

    /**
     * When identifiers are String, they are not actually stored in the memory, but are eliminated during compilation. Thus, every occurrence of a String identifier is replaced with its actual value.
     * @return  the value of the identifier at the time of execution
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * When identifiers are String, they are not actually stored in the memory, but are eliminated during compilation. Thus, every occurrence of a String identifier is replaced with its actual value.
     * @param stringValue   the value of the identifier at the time of execution
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
