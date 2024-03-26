package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!(type1.isBoolean() && type2.isBoolean())){
            throw new ContextualError("Type error in a boolean operation", this.getLocation());
        }
        Type type = compiler.environmentType.BOOLEAN;
        setType(type);
        return type;
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        if (hasIdentifier()) {
            Label endLabel = compiler.newLabel("endBooleanExpression");

            DVal leftValue = getLeftOperand().getDVal(compiler);
            if (!leftValue.equals(Register.R1)) compiler.addInstruction(new LOAD(leftValue, Register.R1));
            compiler.addInstruction(new CMP(lazyEvaluationFirstValue(), Register.R1));
            compiler.addInstruction(new BEQ(endLabel));

            DVal rightValue = getRightOperand().getDVal(compiler);
            if (!rightValue.equals(Register.R1)) compiler.addInstruction(new LOAD(rightValue, Register.R1));
            compiler.addLabel(endLabel);
            return Register.R1;
        }
        else { //We can compute it once during compilation, and never do it again during execution
            return new ImmediateInteger((Integer) getLiteralValue());
        }
    }

    @Override
    protected boolean register0Used() {
        return false;
    }

    /**
     * @return  the value of the first operand of the boolean expression, so that the evaluation of the second is skipped (for example, it is 1 for and, because 1 && x can be simplified into 1)
     */
    protected abstract int lazyEvaluationFirstValue();
}
