package railroads;

import dto.EvolutionResults;
import graphics.RailroadsWindow;
import mpi.*;

public class MainMPJ {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        long seed = 19012025;

        DarwinMPJ darwin = new DarwinMPJ(seed);
        Board init_board = darwin.getInitBoard();
        RailroadsWindow window;
        if(rank == 0) window = new RailroadsWindow(init_board);
        else window=null;

        //System.out.println("Gen 0");

        while(true){
            MPI.COMM_WORLD.Barrier();
            if(rank == 0){
                EvolutionResults res = darwin.evolve();
                window.updateAgentDisplay(res);
            }else{
                darwin.evolve();
            }

        }
    }
}
