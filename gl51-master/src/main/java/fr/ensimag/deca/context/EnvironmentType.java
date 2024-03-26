package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.context.ClassType;

// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl51
 * @date 01/01/2024
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        envTypes.put(stringSymb, new TypeDefinition(STRING, Location.BUILTIN));
        // not added to envTypes, it's not visible for the user.

        //We had the object class in this predef env_type
        //Not sure needs to be tested
        Symbol objectSymb = compiler.createSymbol("Object");
        OBJECT = new ClassType(objectSymb, Location.BUILTIN, new ClassDefinition(null, Location.BUILTIN, null));
        ClassDefinition objectDef = new ClassDefinition(OBJECT, Location.BUILTIN, null);
        //We create the equals method
        Symbol equalsSymb = compiler.createSymbol("equals");
        Signature equalsSignature = new Signature();
        equalsSignature.add(OBJECT);
        objectDef.getMembers().put(equalsSymb, new MethodDefinition(BOOLEAN, Location.BUILTIN, equalsSignature, 1, objectDef));
        objectDef.setNumberOfMethods(1);
        envTypes.put(objectSymb, objectDef);
    }

    private final Map<Symbol, TypeDefinition> envTypes;

    public boolean contains(Symbol symb){
        return envTypes.containsKey(symb);
    }
    public void put(Symbol symb, TypeDefinition typeDef){
        envTypes.put(symb, typeDef);
    }
    public TypeDefinition get(Symbol symb){
        return envTypes.get(symb);
    }
    public Map<Symbol, TypeDefinition> getEnvTypes(){return envTypes;}
    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;

    public final ClassType OBJECT;
}
