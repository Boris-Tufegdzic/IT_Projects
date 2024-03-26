package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //Error checking
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if((!type1.isInt() && !type1.isFloat()) || (!type2.isInt() && !type2.isFloat())){
            throw new ContextualError("Type error in an arithmetic operation", this.getLocation());
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
            setType(compiler.environmentType.FLOAT);
            return compiler.environmentType.FLOAT;
        }
        else{
            setType(compiler.environmentType.INT);
            return compiler.environmentType.INT;
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        codeGenInst(compiler);
        if (getType().isInt()) compiler.addInstruction(new WINT());
        else compiler.addInstruction(new WFLOATX());
    }
}
