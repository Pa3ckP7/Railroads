package railroads;

import models.Agent;
import graphics.RailroadsWindow;
import models.Gene;
import models.Tile;
import dto.EvolutionResults;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        long seed = 19012025;

        Darwin darwin = new Darwin(seed);
        Board init_board = darwin.getInitBoard();
        RailroadsWindow window = new RailroadsWindow(init_board);

        //System.out.println("Gen 0");

        while(true){

            EvolutionResults res = darwin.evolve();
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            //System.out.println("Gen " + res.generation());
            window.updateAgentDisplay(res);

        }
    }
}
