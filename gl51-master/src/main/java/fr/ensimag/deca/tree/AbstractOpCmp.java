package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.CMP;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        super.getDVal(compiler);
        if (hasIdentifier()) compiler.addInstruction(new CMP(Register.R0, Register.R1));
        return null;
    }
}
