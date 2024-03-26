package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
/**
 * Unary expression.
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractMethodCall extends AbstractExpr {
    public abstract void decompile(IndentPrintStream s);
    public abstract void decompileToJava(IndentPrintStream s);
}