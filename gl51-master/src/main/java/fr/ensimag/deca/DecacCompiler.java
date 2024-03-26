package fr.ensimag.deca;

import fr.ensimag.deca.codegen.Errors;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.log4j.Logger;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl51
 * @date 01/01/2024
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);
    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    private int labelNumber;
    private int GBOffset;
    private final Errors errors;
    private int maxStackSize;

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;

        this.labelNumber = 0;
        this.GBOffset = 0;
        this.maxStackSize = 0;
        if (compilerOptions != null ) this.errors = new Errors(this, compilerOptions.getCheck());
        else this.errors = new Errors(this, false);
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }
    
    /**
     * @see 
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }

    /**
     * Adds the specified instruction to the beginning of the program
     * @param instruction   the instruction to be added at the beginning
     */
    public void addFirst(Instruction instruction) {
        program.addFirst(new Line(instruction));
    }

    /**
     * Adds the specified comment to the beginning of the program
     * @param comment   the comment to be added at the beginning
     */
    public void addFirst(String comment) {
        program.addFirst(new Line(comment));
    }

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();
 

    /** The global environment for types (and the symbolTable) */
    public final SymbolTable symbolTable = new SymbolTable();
    public final EnvironmentType environmentType = new EnvironmentType(this);

    public Symbol createSymbol(String name) {
        return symbolTable.create(name);
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.replace(".deca", ".ass");
        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            LOG.debug("Location Exception while compiling file "+sourceFile+".", e);
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            LOG.debug("Fatal error while compiling file "+sourceFile+".", e);
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the sourdecompileIfDce (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);
        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }

        if (compilerOptions.getByteCode()) {
            //Decompiling the tree into a java program
            FileOutputStream javaStream;
            String javaFileName = new File(destName).getAbsoluteFile().getParent() + "/Main.java";
            try {
                javaStream = new FileOutputStream(javaFileName);
            } catch (FileNotFoundException exception) {
                throw new DecacFatalError("Failed to open output file: " + exception.getLocalizedMessage());
            }
            prog.decompileToJava(new PrintStream(javaStream));

            //Compiling the java program into bytecode
            try {
                ProcessBuilder processBuilder1 = new ProcessBuilder("javac", javaFileName);
                processBuilder1.start().waitFor();
                ProcessBuilder processBuilder2 = new ProcessBuilder("rm", javaFileName);
                processBuilder2.start().waitFor();
            } catch (Exception exception) {
                throw new DecacFatalError("Error during compilation with javac");
            }
            return false;
        }

        assert(prog.checkAllLocations());
        if(this.compilerOptions.getParse()) { 
            //prog.prettyPrint(out);
            prog.decompile(out);
            LOG.info("Stop after creating the tree");  
            return false;       // Option -p raise. Display decompil version of the tree
        }

        prog.verifyProgram(this);
        assert(prog.checkAllDecorations());
        if(this.compilerOptions.getVerif()) {
            LOG.info("Stop after verification of the tree");
            return false;       // Option -v raise nothing is return, program is just verify
        }
        addComment("start main program");
        prog.codeGenProgram(this);
        addComment("end main program");
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");


        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     * compiler.
     * @throws LocationException When a compilation error (incorrect program)
     * occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

    /**
     * Should be used for all label creation to ensure that all labels are unique
     * @param name      name used for the label
     * @return          a unique label whose name is the argument with a unique suffix
     */
    public Label newLabel(String name) {
        labelNumber++;
        return new Label(name + "_" + labelNumber);
    }

    /**
     * Increments GB offset and max stack size and returns (ancient GB offset + 1)
     * @param i the number of which to increment everything
     * @return  the offset of the new variable
     */
    public int incrementStack(int i) {
        GBOffset += i;
        maxStackSize += i;
        return GBOffset - i + 1;
    }

    public void decrementStack(int i) {
        GBOffset -= i;
    }

    /**
     * @return  the Errors object used to handle errors
     */
    public Errors getErrors() {
        return errors;
    }

    /**
     * Initializes SP offset to GB offset (otherwise, using PULL and POP would overwrite existing values in stack)
     */
    public int initializeSP() {
        if (GBOffset != 0) this.addInstruction(new ADDSP(GBOffset + 1));
        return GBOffset + 1;
    }

    /**
     * Returns the value of SP to GB
     */
    public void zeroSP() {
        this.addInstruction(new SUBSP(GBOffset + 1));
    }

    /**
     * @return  the GB pffset
     */
    public int getGBOffset() {
        return GBOffset;
    }

    /**
     * Increments max stack size used during the program
     */
    public void incrementMaxStackSize() {
        maxStackSize++;
    }
}
