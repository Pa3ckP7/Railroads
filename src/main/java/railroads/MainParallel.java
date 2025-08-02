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
        RailroadsWindow window;
        if (Settings.ENABLE_GUI) window = new RailroadsWindow();
        gLogger.info("Starting.....");
        var scope = KVLoggerFactory.createScoped("darwin", false);
        var seed = masterSeed;
        DarwinParallel darwin = new DarwinParallel(scope, seed);
        long lastScore = Long.MAX_VALUE;
        int genCounter = 0;
        EvolutionResults res = null;
        TimerManager.startTimer("milestone");
        while (genCounter < 200) {
            res = darwin.evolve();
            var curScore = res.bestSolution().evaluation();
            if (curScore != lastScore) {
                lastScore = curScore;
                genCounter = 0;
            }
            genCounter++;
            if (res.generation() == 2000) break;
            if (Settings.ENABLE_GUI) window.updateAgentDisplay(res);
            if (res.generation() % 100 == 0) gLogger.info("gen " + res.generation());
        }
        darwin.shutdown();
        gLogger.info(String.format("Done in %.2f ms", TimerManager.stopTimer("milestone") / 1_000_000.0));
    }


}


