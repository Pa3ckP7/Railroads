package railroads;

import dto.EvaluatedSolution;
import logging.KVHandlerFactory;
import logging.KVLoggerFactory;
import models.Agent;
import graphics.RailroadsWindow;
import models.Gene;
import models.Tile;
import dto.EvolutionResults;
import timing.TimerManager;

import java.util.*;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        KVLoggerFactory.addGlobalHandler("console", KVHandlerFactory.getConsoleHandler());
        var gLogger = KVLoggerFactory.getGlobalLogger(Main.class);
        long masterSeed = 19012025;
        RailroadsWindow window;
        if (Settings.ENABLE_GUI) window = new RailroadsWindow();

        gLogger.info("Starting.....");
        var scope = KVLoggerFactory.createScoped("darwin", false);
        var seed = masterSeed;
        Darwin darwin = new Darwin(scope, seed);
        long lastScore = Long.MAX_VALUE;
        int genCounter = 0;
        TimerManager.startTimer("milestone");
        EvolutionResults res = null;
        while (genCounter < 200) {
            res = darwin.evolve();
            var curScore = res.bestSolution().evaluation();
            if (curScore != lastScore) {
                lastScore = curScore;
                genCounter = 0;
            }
            genCounter++;
            if (Settings.ENABLE_GUI) {
                window.updateAgentDisplay(res);
            }
            if (res.generation() % 100 == 0) gLogger.info("gen " + res.generation());
        }
        gLogger.info(String.format("Done in %.2f ms", TimerManager.stopTimer("milestone") / 1_000_000.0));
    }


}
