package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl51
 * @date 01/01/2024
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean getCheck() {
        return this.noCheck;
    } 

    public boolean getParse() {
        return this.parseOnly;
    }

    public boolean getVerif() {
        return this.verificationOnly;
    }

    public boolean getByteCode() {
        return this.byteCode;
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private List<File> sourceFiles = new ArrayList<File>();
    private boolean noCheck = false; 
    private boolean parseOnly = false;
    private boolean verificationOnly = false;
    private boolean byteCode = false;

    
    public void parseArgs(String[] args) throws CLIException {
        boolean lastArgWasR = false; //When true, means that the last argument was "-r"
        for (String c : args) {
            if (lastArgWasR) {
                //c should be an int between 4 and 16
                try {
                    int i = Integer.parseInt(c);
                    if (i < 4 || i > 16) System.out.println("-r option should be followed by an integer between 4 and 16, skipping option");
                } catch (Exception e) {
                    System.out.println("-r option should be followed by an integer, skipping option");
                }
                lastArgWasR = false;
            }
            else {
                switch (c) {
                    case "-b":
                        printBanner = true;
                        return;
                    case "-n" :
                        this.noCheck = true;
                        break;
                    case "-p":
                        this.parseOnly = true;
                        break;
                    case "-v":
                        for (String s : args)
                            if (s.equals("-p")) break;
                        this.verificationOnly = true;
                        break;
                    case "-r":
                        lastArgWasR = true;
                        break;
                    case "-d":
                        if (debug <= 3) debug++;
                        break;
                    case "-P":
                        parallel = true;
                        break;
                    case "-byte":
                        byteCode = true;
                        break;
                    default: //It's a file
                        if (!c.endsWith(".deca")) System.out.println("File doesn't end with .deca, skipping file");
                        else {
                            File f = new File(c);
                            if (!f.isFile()) System.out.println("Cannot find file " + c + ", skipping file");
                            else sourceFiles.add(f);
                        }
                }
            }
        }
        if (lastArgWasR) System.out.println("-r option should be followed by integer! Skipping option.");

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayUsage() {
        System.out.println("Usage : decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <deca file>...] | [-b]");
        System.out.println("OPTIONS :");
        System.out.println("-b \t \t (banner) \t \t prints a banner with the team members' names");
        System.out.println("-p \t \t (parse) \t \t stops the program after having constructed the tree, and shows the decompilation of it");
        System.out.println("-v \t \t (verification) \t stops decac after verification (no output if there is no error)");
        System.out.println("-n \t \t (no check) \t \t doesn't check tests specified in points 11.1 and 11.3 of Deca semantics");
        System.out.println("-r X \t \t (registers) \t \t limits available registers to R0, ..., R{X-1}, where 4 <= X <= 16");
        System.out.println("-d \t \t (debug) \t \t activates debug traces (-d for info ; -d -d for debug ; -d -d -d for traces)");
        System.out.println("-P \t \t (parallel) \t \t if there are several source files, launches file compiling in parallel");
        System.out.println("-byte \t \t (bytecode) \t \t generates Java bytecode instead of ima assembly code");
    }
}
