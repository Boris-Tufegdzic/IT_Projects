package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;

/**
 * read...() statement.
 *
 * @author gl51
 * @date 01/01/2024
 */
public abstract class AbstractReadExpr extends AbstractExpr {

    public AbstractReadExpr() {
        super();
    }

    /**
     * Generates the code used to read something (storing the result to R1)
     * @param compiler  the DecacCompiler used
     */
    public abstract void codeGenRead(DecacCompiler compiler);
}
