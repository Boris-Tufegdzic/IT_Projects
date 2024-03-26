package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.REM;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    protected int lazyEvaluationFirstValue() {
        return 1;
    }

    @Override
    protected Object getLiteralValue() {
        return (Boolean) getLeftOperand().getLiteralValue() || (Boolean) getRightOperand().getLiteralValue();
    }
}
