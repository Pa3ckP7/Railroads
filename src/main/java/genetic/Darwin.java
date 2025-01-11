package genetic;

import railroads.Board;
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
    private final int width;
    private final int height;
    private final int maxTrains;
    private final float minFill;
    private final float maxFill;
    private final int maxAgents;
    private final Random rand;
    private final CrossoverFunc<Agent> crossoverFunc;
    private final EvalFunc<Agent> evalFunc;
    private final RepopulateFunc<Agent> repopulateFunc;

    public Darwin(int agentCount, BoardSettings boardSettings, AgentSettings agentSettings,
                  CrossoverFunc<Agent> crossoverFunc, EvalFunc<Agent> evalFunc,
                  RepopulateFunc<Agent> repopulateFunc, long randomSeed) {
        this.boardSeed = boardSettings.boardSeed();
        this.width = boardSettings.width();
        this.height = boardSettings.height();
        this.maxTrains = boardSettings.maxTrains();
        this.minFill = agentSettings.initMinfill();
        this.maxFill = agentSettings.initMaxfill();
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
        int[] scores = new int[this.agents.size()];
        int maxScore = Integer.MAX_VALUE;
        Agent bestAgent = null;
        for (int i = 0; i < this.agents.size(); i++) {
            Agent agent = this.agents.get(i);
            agent.solve();
            scores[i] = this.evalFunc.eval(agent);
            if (scores[i] < maxScore) {
                maxScore = scores[i];
                bestAgent = agent;
            }
        }


//        int finalMaxScore = maxScore;
//        double[] relativeResults=Arrays.stream(scores).mapToDouble(x -> (double)x/finalMaxScore).toArray();
//
//        ArrayList<Agent> newGeneration = new ArrayList<>();
//        for (int i = 0; i < relativeResults.length && newGeneration.size() < this.maxAgents; i=i+1%relativeResults.length) {
//            double choice = rand.nextDouble();
//            if(choice > relativeResults[i] ) continue;
//            newGeneration.add(this.agents.get(i));
//            if((newGeneration.size()&1)!=0) continue;
//            Agent agentA = newGeneration.removeLast();
//            Agent agentB = newGeneration.removeLast();
//            Agent child1 = this.crossoverFunc.Cross(agentA, agentB);
//            Agent child2 = this.crossoverFunc.Cross(agentB, agentA);
//            newGeneration.add(child1);
//            newGeneration.add(child2);
//            newGeneration.add(agentA);
//            newGeneration.add(agentB);
//        }
        Collection<Agent> newPopulation = this.repopulateFunc.repopulate(this.agents, scores, this.crossoverFunc, this.maxAgents, this.rand.nextLong());
        ArrayList<Agent> newGeneration = new ArrayList<>(newPopulation);

        EvolutionResults evoResults = new EvolutionResults(this.generation, bestAgent, maxScore, scores);
        this.generation++;
        this.agents = newGeneration;
        return evoResults;
    }

}
