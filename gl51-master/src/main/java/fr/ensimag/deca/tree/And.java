package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.MUL;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected int lazyEvaluationFirstValue() {
        return 0;
    }

    @Override
    protected Object getLiteralValue() {
        return (Boolean) getLeftOperand().getLiteralValue() && (Boolean) getRightOperand().getLiteralValue();
    }
}
