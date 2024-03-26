package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.ima.pseudocode.instructions.SGT;

/**
 *
 * @author gl51
 * @date 01/01/2024
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!type.isBoolean()){
            throw new ContextualError("Type error in a not operation", this.getLocation());
        }
        setType(type);
        return type;
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }

    @Override
    protected DVal getDVal(DecacCompiler compiler) {
        DVal immediate = super.getDVal(compiler);
        if (hasIdentifier()) {
            compiler.addInstruction(new OPP(Register.R1, Register.R1));
            compiler.addInstruction(new ADD(new ImmediateInteger(1), Register.R1));
            return Register.R1;
        }
        return immediate;
    }

    @Override
    protected Object getLiteralValue() {
        return !(Boolean) getOperand().getLiteralValue();
    }
}
