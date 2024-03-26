package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.AbstractInitialization;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.Visibility;

/**
 * Definition of a field (data member of a class).
 *
 * @author gl51
 * @date 01/01/2024
 */
public class FieldDefinition extends ExpDefinition {
    public int getIndex() {
        return index;
    }

    private int index;

    @Override
    public boolean isField() {
        return true;
    }

    private final Visibility visibility;
    private final ClassDefinition containingClass;

    private final AbstractInitialization init;
    
    public FieldDefinition(Type type, Location location, Visibility visibility,
                           ClassDefinition memberOf, int index, AbstractInitialization init) {
        super(type, location);
        this.visibility = visibility;
        this.containingClass = memberOf;
        this.index = index;
        this.init = init;
    }

    public AbstractInitialization getInit() {
        return init;
    }
    
    @Override
    public FieldDefinition asFieldDefinition(String errorMessage, Location l)
            throws ContextualError {
        return this;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public ClassDefinition getContainingClass() {
        return containingClass;
    }

    @Override
    public String getNature() {
        return "field";
    }

    @Override
    public boolean isExpression() {
        return true;
    }
}
