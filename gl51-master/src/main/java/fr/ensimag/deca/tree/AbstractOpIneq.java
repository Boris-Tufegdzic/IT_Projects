package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractOpIneq extends AbstractOpCmp {

    public AbstractOpIneq(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        //Checking contextual error (same as the ones in AbstractOpArith)
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if((!type1.isInt() && !type1.isFloat()) || (!type2.isInt() && !type2.isFloat())){
            throw new ContextualError("Type error in a inequality comparison", this.getLocation());
        }
        if(type1.isFloat() || type2.isFloat()){
            if(type1.isInt()){
                setLeftOperand(new ConvFloat(getLeftOperand()));
                getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
            }
            if(type2.isInt()){
                setRightOperand(new ConvFloat(getRightOperand()));
                getRightOperand().verifyExpr(compiler, localEnv, currentClass);
            }
        }
        Type type = compiler.environmentType.BOOLEAN;
        setType(type);
        return type;
    }


}
