package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl51
 * @date 01/01/2024
 */
public class EnvironmentExp {
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).

    /* Implemented made by Willy Wonka */
    private HashMap<Symbol, ExpDefinition> envExp = new HashMap<>();

    EnvironmentExp parentEnvironment;
    
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    public HashMap<Symbol, ExpDefinition> getEnvExp(){
        return this.envExp;
    }

    /**
     * Fill envExp with another hahsmap in param
     * @param couples: hashmap
     */
    public void fillMap(HashMap<Symbol, ExpDefinition> couples){
        for(Map.Entry<Symbol, ExpDefinition> entries : couples.entrySet()){
            this.envExp.put(entries.getKey(), entries.getValue());
        }
    }

    public boolean contains(Symbol symb){
        //return this.envExp.containsKey(symb);
        if(envExp == null){
            return false;
        }
        else{
            if(envExp.containsKey(symb)){
                return true;
            }
            else{
                if(parentEnvironment == null){
                    return false;
                }
                return parentEnvironment.contains(symb);
            }
        }
    }

    public void put(Symbol symb, ExpDefinition expDef){
        envExp.put(symb, expDef);
    }

    public EnvironmentExp getParentEnvironment(){
        return parentEnvironment;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    /* Implemented by Willy Wonka, recursif*/
    public ExpDefinition get(Symbol key) {
        /*Looking for the definition in the current environment*/
        if(envExp.containsKey(key)){
            return envExp.get(key);
        }
        /*Looking into the parent environment if exists*/
        else{
            if(parentEnvironment == null){return null;}
            return parentEnvironment.get(key);
        }
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
        if(envExp.containsKey(name)) {
            throw new DoubleDefException();
        }
        else {
            envExp.put(name, def);
        }
    }

    /**
     *
     * @param other
     * @param location
     * @throws ContextualError
     */
    public void UnionDisjointe(EnvironmentExp other, Location location) throws ContextualError{ //Not sure if it is operational
        //Checking if the two environments are disjoint - not twice the same symbol  (key)
        // -> Check if exists at least a commum key in both hashmap envExp
        for(Symbol sThis : this.envExp.keySet()){
            for(Symbol otherSymbol : other.getEnvExp().keySet()){
                if (sThis.equals(otherSymbol)){
                    throw new ContextualError("Two symbol have the same name\n" +
                            "Symbol : "+sThis.toString()+" is declared several times", location);
                }
            }
        }
        //Fills "this" EnvironmentExp with the content of "other" EnvironmentExp
        this.fillMap(other.getEnvExp());
    }

    /**
     * does the operation this / other
     * @param other
     */
    public void stack(EnvironmentExp other) {
        /* Complete with env2 (other), not adding duplicated symbol*/
        for (Map.Entry<Symbol, ExpDefinition> entriesOther : other.getEnvExp().entrySet()) {
            if (!this.contains(entriesOther.getKey())) {
                this.put(entriesOther.getKey(), entriesOther.getValue());
            }
        }
    }
}
