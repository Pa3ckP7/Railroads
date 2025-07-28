package railroads;

import dto.EvolutionResults;
import graphics.RailroadsWindow;

import javax.swing.*;

public class MainParallel {
    public static void main(String[] args) {
        long seed = 19012025;

        DarwinParallel darwin = new DarwinParallel(seed);
        Board init_board = darwin.getInitBoard();

        //Event dispatch thread apparently modifying the GUI from main is a bad idea. And so is having darwin on the main thread;
        SwingUtilities.invokeLater(() -> {
            RailroadsWindow window = new RailroadsWindow(init_board);

            new Thread(() -> {
                while(true){
                    EvolutionResults results = darwin.evolve();
                    SwingUtilities.invokeLater(() -> window.updateAgentDisplay(results));
                }
            }).start();
        });
    }
}
