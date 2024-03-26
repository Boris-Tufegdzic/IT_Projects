package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.SLT;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        DVal immediate = super.getDVal(compiler);
        if (hasIdentifier()) {
            compiler.addInstruction(new SLT(Register.R1));
            return Register.R1;
        }
        return immediate;
    }

    @Override
    protected Object getLiteralValue() {
        if (getLeftOperand().getType().isInt()) {
            return (Integer) getLeftOperand().getLiteralValue() < (Integer) getRightOperand().getLiteralValue();
        }
        else {
            return (Float) getLeftOperand().getLiteralValue() < (Float) getRightOperand().getLiteralValue();
        }
    }
}
