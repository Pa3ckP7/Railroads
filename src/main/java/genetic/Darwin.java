package genetic;

import railroads.Board;
import railroads.Settings;
import util.DataContainers.AgentSettings;
import util.DataContainers.BoardSettings;
import util.DataContainers.EvolutionResults;
import util.helpers.CrossoverFunc;
import util.helpers.EvalFunc;
import util.helpers.RepopulateFunc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class Darwin {

    private int generation = 0;
    private ArrayList<Agent> agents;
    private final long boardSeed;
    private final short width;
    private final short height;
    private final int maxTrains;
    private final float minFill;
    private final float maxFill;
    private final int maxAgents;
    private final Random rand;
    private final CrossoverFunc<Agent> crossoverFunc;
    private final EvalFunc<Agent> evalFunc;
    private final RepopulateFunc<Agent> repopulateFunc;

    public Darwin(int agentCount, long seed,
                  CrossoverFunc<Agent> crossoverFunc, EvalFunc<Agent> evalFunc,
                  RepopulateFunc<Agent> repopulateFunc, long randomSeed) {
        this.boardSeed = seed;
        this.width = Settings.BOARD_WIDTH;
        this.height = Settings.BOARD_HEIGHT;
        this.maxTrains = Settings.MAX_TRAINS;
        this.minFill = Settings.AGENT_MIN_FILL;
        this.maxFill = Settings.AGENT_MAX_FILL;
        this.agents = new ArrayList<>();
        this.maxAgents = agentCount;
        this.crossoverFunc = crossoverFunc;
        this.evalFunc = evalFunc;
        this.repopulateFunc = repopulateFunc;
        this.rand = new Random(randomSeed);
        initGen0();
    }

    private void initGen0(){
        for (int i = 0; i < this.maxAgents; i++) {
            Board board = new Board(this.width, this.height, this.maxTrains, this.boardSeed);
            Agent agent = Agent.withRandomGenome(board, this.minFill, this.maxFill, rand.nextLong());
            this.agents.add(agent);
        }
    }

    private EvolutionResults evolve(){
        long[] scores = new long[this.agents.size()];
        long maxScore = Integer.MAX_VALUE;
        Agent bestAgent = null;
        for (int i = 0; i < this.agents.size(); i++) {
            Agent agent = this.agents.get(i);
            agent.solve();
            scores[i] = this.evalFunc.eval(agent);
            if (scores[i] < maxScore) {
                maxScore = scores[i];
                bestAgent = agent;
            }
            agent.setScore(scores[i]);
        }
        Collection<Agent> newPopulation = this.repopulateFunc.repopulate(this.agents, this.crossoverFunc, this.rand.nextLong());
        ArrayList<Agent> newGeneration = new ArrayList<>(newPopulation);

        EvolutionResults evoResults = new EvolutionResults(this.generation, bestAgent, maxScore, scores);
        this.generation++;
        this.agents = newGeneration;
        return evoResults;
    }

}
