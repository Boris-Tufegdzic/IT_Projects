package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl51
 * @date 01/01/2024
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        setLocation(getOperand().getLocation());
        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        DVal immediate = super.getDVal(compiler);
        if (hasIdentifier()) {
            compiler.addInstruction(new FLOAT(Register.R1, Register.R1));
            return Register.R1;
        }
        return immediate;
    }

    @Override
    protected Object getLiteralValue() {
        return (float) (Integer) getOperand().getLiteralValue();
    }
}
