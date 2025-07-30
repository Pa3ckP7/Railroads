package railroads;

import dto.EvolutionResults;
import graphics.RailroadsWindow;
import logging.KVHandlerFactory;
import logging.KVLoggerFactory;
import timing.TimerManager;

import javax.swing.*;
import java.util.Random;
import java.util.logging.Level;

public class MainParallel {
    public static void main(String[] args) {
        KVLoggerFactory.addGlobalHandler("console", KVHandlerFactory.getConsoleHandler());
        var gLogger = KVLoggerFactory.getGlobalLogger(Main.class);
        long masterSeed = 19012025L;
        var mrng  = new Random(masterSeed);
        RailroadsWindow window;
        if(Settings.ENABLE_GUI) window = new RailroadsWindow();
        for(int i = 0; i < 1; i++){
            gLogger.info("Starting run " + i);
            var scope = KVLoggerFactory.createScoped("test"+i, false);
            try {
                scope.addHandler("file", KVHandlerFactory.getFileHandler("test" + i));
            }catch (Exception e){
                gLogger.log(Level.SEVERE, String.format("File handler failed to add for %d: %s", i,e.getMessage()), e);
            }
            var logger = scope.getLogger(Main.class, "milestone");
            var seed = mrng.nextLong();
            DarwinParallel darwin = new DarwinParallel(scope, seed);
            Board init_board = darwin.getInitBoard();


            //System.out.println("Gen 0");
            long lastScore = Long.MAX_VALUE;
            int genCounter = 0;
//            TimerManager.startTimer("milestone");
            EvolutionResults res = null;
            while(genCounter < 200){
                res = darwin.evolve();
                var curScore = res.bestSolution().evaluation();
                if(curScore != lastScore){
                    lastScore = curScore;
                    //gLogger.info("Best score is " + curScore);
                    genCounter = 0;
                }
//                genCounter++;
                logger.info(String.format("%d %d", res.generation(), res.bestSolution().evaluation()));
//                if(res.generation() == 100) logger.info(String.format("M100\t%d", TimerManager.lapTimer("milestone")));
//                if(res.generation() == 500) logger.info(String.format("M500\t%d", TimerManager.lapTimer("milestone")));
//                if(res.generation() == 1000) logger.info(String.format("M1000\t%d", TimerManager.lapTimer("milestone")));
                if(res.generation()%100 == 0) gLogger.info("run"+i+" gen "+res.generation());
                if(res.generation() == 2000)break;
                if(Settings.ENABLE_GUI)window.updateAgentDisplay(res);
            }
//            var ftime = TimerManager.stopTimer("milestone");
//            logger.info(String.format("MEND%d\t%d", res.generation(), ftime));
//            gLogger.info("Ended after " + res.generation() + " generations");
//            gLogger.info(String.format("Needed %.3fms", ftime/1_000_000_000.0));
            darwin.shutdown();
        }


    }

}
