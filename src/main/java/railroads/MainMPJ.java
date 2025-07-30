package railroads;

import dto.EvolutionResults;
import graphics.RailroadsWindow;
import logging.KVHandlerFactory;
import logging.KVLoggerFactory;
import mpi.*;
import timing.TimerManager;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMPJ {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        long masterSeed = 19012025;
        var mrng  = new Random(masterSeed);
        if(rank==0){
            KVLoggerFactory.addGlobalHandler("console", KVHandlerFactory.getConsoleHandler());
        }
        var gLogger = KVLoggerFactory.getGlobalLogger(MainMPJ.class);

        RailroadsWindow window;
        if (rank == 0) window = new RailroadsWindow();
        else window = null;

        for(int i=0; i<10; i++) {
            gLogger.info("Starting run " + i);
            var scope = KVLoggerFactory.createScoped("test"+i, false);
            try {
                if(rank == 0){
                    scope.addHandler("file", KVHandlerFactory.getFileHandler("test" + i));
                }
            }catch (Exception e){
                gLogger.log(Level.SEVERE, String.format("File handler failed to add for %d: %s", i,e.getMessage()), e);
            }
            var logger = scope.getLogger(Main.class, "milestone");
            var seed = mrng.nextLong();
            DarwinMPJ darwin = new DarwinMPJ(scope,seed);
            Board init_board = darwin.getInitBoard();


            //System.out.println("Gen 0");
            long lastScore = Long.MAX_VALUE;
            int genCounter = 0;
            TimerManager.startTimer("milestone");
            EvolutionResults res = null;
            while (genCounter < 200) {
                MPI.COMM_WORLD.Barrier();
                res = darwin.evolve();
                if (Settings.ENABLE_GUI && rank == 0) {
                    window.updateAgentDisplay(res);
                }
                var curScore = res.bestSolution().evaluation();
                if (curScore != lastScore) {
                    lastScore = curScore;
                    genCounter = 0;
                }
                genCounter++;
                if (rank == 0) {
                    if (res.generation() == 100)
                        logger.info(String.format("M100\t%d", TimerManager.lapTimer("milestone")));
                    if (res.generation() == 500)
                        logger.info(String.format("M500\t%d", TimerManager.lapTimer("milestone")));
                    if (res.generation() == 1000)
                        logger.info(String.format("M250\t%d", TimerManager.lapTimer("milestone")));
                    if (res.generation() % 100 == 0) gLogger.info("run" + i + " gen " + res.generation());
                }

            }
            if(rank==0){
                var ftime = TimerManager.stopTimer("milestone");
                logger.info(String.format("MEND%d\t%d", res.generation(), ftime));
                gLogger.info("Ended after " + res.generation() + " generations");
                gLogger.info(String.format("Needed %.3fms", ftime/1_000_000_000.0));
            }
        }
        MPI.Finalize();
    }
}
