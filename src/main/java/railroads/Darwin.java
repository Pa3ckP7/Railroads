package railroads;

import dto.EvaluatedSolution;
import dto.Solution;
import models.Agent;
import dto.EvolutionResults;

import java.util.*;

public class Darwin {

    private int generation = 0;
    private ArrayList<Agent> agents;
    private final short width;
    private final short height;
    private final int maxTrains;
    private final float minFill;
    private final float maxFill;
    private final int maxAgents;
    private final Random rand;
    private Board initBoard;

    public Darwin(long randomSeed) {
        this.width = Settings.BOARD_WIDTH;
        this.height = Settings.BOARD_HEIGHT;
        this.maxTrains = Settings.MAX_TRAINS;
        this.minFill = Settings.AGENT_MIN_FILL;
        this.maxFill = Settings.AGENT_MAX_FILL;
        this.agents = new ArrayList<>();
        this.maxAgents = Settings.MAX_AGENTS;
        this.rand = new Random(randomSeed);
        initGen0();
    }

    private void initGen0(){
        long board_seed = rand.nextLong();
        this.initBoard = new Board(board_seed);
        for (int i = 0; i < this.maxAgents; i++) {
            Board board = new Board(initBoard);
            Agent agent = Agent.generateAgent(rand.nextLong());
            this.agents.add(agent);
        }
    }

    public EvolutionResults evolve(){
        var results = runAgents(this.agents);
        var evaluatedResults = evaluateSolutions(results);
        evaluatedResults.sort(Comparator.comparingLong(EvaluatedSolution::evaluation));
        var newGeneration = repopulateAgents(evaluatedResults, rand.nextLong());
        generation++;
        return new EvolutionResults(
                generation,
                evaluatedResults.getFirst(),
                evaluatedResults.getLast()
        );
    }

    private ArrayList<Solution> runAgents(ArrayList<Agent> agents){
        var solutions = new ArrayList<Solution>();
        for (Agent agent : agents) {
            var board = new Board(initBoard);
            solutions.add(agent.solve(board));
        }
        return solutions;
    }
    private ArrayList<EvaluatedSolution> evaluateSolutions(ArrayList<Solution> solutions){

    }

    private ArrayList<Agent> repopulateAgents(ArrayList<EvaluatedSolution> evaluatedSolutions, long seed){

    }

    public Board getInitBoard() {
        return initBoard;
    }
}
