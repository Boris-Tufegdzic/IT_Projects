package fr.ensimag.deca;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl51
 * @date 01/01/2024
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println("---------------------------------------");
            System.out.println("---------------- DECAC ----------------");
            System.out.println("------------ Deca compiler ------------");
            System.out.println("--------- Projet GL 2023-2024 ---------");
            System.out.println("------------ gl51, Ensimag ------------");
            System.out.println("---------------------------------------");
            System.out.println("--------------- MADE BY ---------------");
            System.out.println("--------- Jean-Charles Granier --------");
            System.out.println("------------- Vishal Kumar ------------");
            System.out.println("------------ Thomas Serafin -----------");
            System.out.println("------------- Virgile Solt ------------");
            System.out.println("--------- AND Boris Tufegdzic ---------");
            System.out.println("---------------------------------------");
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            options.displayUsage();
            System.exit(0);
        }
        if (options.getParallel()) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (File source : options.getSourceFiles()) {
                Runnable compilingTask = new Runnable() {
                    @Override
                    public void run() {
                        DecacCompiler compiler = new DecacCompiler(options, source);
                        if (compiler.compile()) {
                            throw new RuntimeException("Error while compiling");
                        }
                    }
                };
                executorService.submit(compilingTask);
            }
            executorService.shutdown();
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
