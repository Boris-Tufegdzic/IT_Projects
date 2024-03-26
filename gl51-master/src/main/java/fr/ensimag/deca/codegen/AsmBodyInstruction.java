package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Operand;
import fr.ensimag.ima.pseudocode.UnaryInstruction;

import java.io.PrintStream;

public class AsmBodyInstruction extends UnaryInstruction {
    public AsmBodyInstruction(Operand operand) {
        super(operand);
    }

    @Override
    protected String getName() {
        return "";
    }


   @Override
    protected void displayOperands(PrintStream s) {
        String soperand = this.getOperand().toString();
        s.print(soperand.substring(1, soperand.length()-1 ));
    }

}
