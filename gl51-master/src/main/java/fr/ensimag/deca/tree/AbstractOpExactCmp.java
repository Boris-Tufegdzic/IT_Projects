package fr.ensimag.deca.tree;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        //Checking contextual error
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if(((!type1.isFloat() && !type1.isInt()) || (!type2.isFloat() && !type2.isInt()))
            && (!(type1.isBoolean() && type2.isBoolean()))
            && ((!type1.isClass() && !type1.isNull()) || (!type2.isClass() && !type2.isNull()))){
            throw new ContextualError("Type error in an exact comparison", this.getLocation());
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
