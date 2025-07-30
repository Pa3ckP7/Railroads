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
    public static void main(String[] args){
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        long masterSeed = 19012025;
        if (rank == 0) {
            KVLoggerFactory.addGlobalHandler("console", KVHandlerFactory.getConsoleHandler());
        }
        var gLogger = KVLoggerFactory.getGlobalLogger(MainMPJ.class);

        RailroadsWindow window;
        if (Settings.ENABLE_GUI && rank == 0) window = new RailroadsWindow();
        else window = null;


        gLogger.info("Starting...");
        var scope = KVLoggerFactory.createScoped("darwin", false);
        var seed = masterSeed;
        DarwinMPJ darwin = new DarwinMPJ(scope, seed);


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
                if (res.generation() % 100 == 0) gLogger.info("gen " + res.generation());
            }

        }
        if(rank==0)gLogger.info("Done.");
        MPI.Finalize();
    }
}
