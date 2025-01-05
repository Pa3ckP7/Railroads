package railroads;

import genetic.Agent;
import genetic.Darwin;
import models.Gene;
import models.Tile;
import util.DataContainers.AgentSettings;
import util.DataContainers.BoardSettings;
import util.helpers.CrossoverFunc;
import util.helpers.EvalFunc;
import util.helpers.RepopulateFunc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static models.Tile.isTJunction;

public class Main {
    public static void main(String[] args) {


        long seed = 420;

        CrossoverFunc<Agent> crossoverFunc =  (agent1 , agent2, board, mutateChance, crossSeed) -> {
            Random rand = new Random(crossSeed);
            int geneC = (int) (agent2.getGenome().size()* rand.nextDouble(0.5));
            Agent child = new Agent(board, agent1.getGenome());
            for (int i = 0; i < geneC; i++) {
                Gene extract = agent2.getGenome().get(rand.nextInt(agent2.getGenome().size()));
                child.insertGene(extract);
            }
            while ( rand.nextFloat() < mutateChance){
                int x = rand.nextInt(board.getWidth());
                int y = rand.nextInt(board.getHeight());
                Gene gene = child.getGene(x,y);
                if (gene == null){
                    child.insertGene(new Gene(x,y, Tile.validTiles[rand.nextInt(Tile.validTiles.length)]));
                    continue;
                }
                if (rand.nextBoolean()){
                    child.eraseGene(x, y);
                    continue;
                }
                gene.setTileType(Tile.validTiles[rand.nextInt(Tile.validTiles.length)]);
            }
            return child;
        };
        RepopulateFunc<Agent> repopulateFunc = (pop, scores, bboard, mutateC, crossF, max, repopSeed) -> {
            Random rand  = new Random(repopSeed);
            ArrayList<Agent> population = new ArrayList<>(pop);
            int maxScore = scores[0];
            for(int i=1; i<scores.length; i++){
                maxScore = Math.max(maxScore, scores[i]);
            }
            final double maxScoreD = maxScore;
            double[] normScores = Arrays.stream(scores).mapToDouble(x -> x/maxScoreD).toArray();
            ArrayList<Agent> newpop = new ArrayList<>();
            for (int i = 0; newpop.size() >= max; i=(i+1)%normScores.length) {
                double choice = rand.nextDouble();
                if(choice>normScores[i]) continue;
                newpop.add(population.get(i));
                if((newpop.size()&1)==0){
                    Agent a = newpop.removeLast();
                    Agent b = newpop.removeLast();
                    Agent c1 = crossF.Cross(a,b, bboard, mutateC, rand.nextLong());
                    Agent c2 = crossF.Cross(b,a, bboard, mutateC, rand.nextLong());
                    newpop.add(c1);
                    newpop.add(c2);
                    newpop.add(a);
                    newpop.add(b);
                }
            }
            return newpop;
        };

        EvalFunc<Agent> evalFunc = (agent) -> {
            //TODO BFS through board in agent
            return 0;
        };

        Darwin darwin = new Darwin(
                20,
                new BoardSettings(20, 20, 4, seed),
                new AgentSettings(0.2f, 0.4f),
                crossoverFunc,
                evalFunc,
                repopulateFunc,
                seed
        );
    }
}
