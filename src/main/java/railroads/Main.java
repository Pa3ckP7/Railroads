package railroads;

import genetic.Agent;
import genetic.Darwin;
import graphics.RailroadsWindow;
import models.Gene;
import models.Genome;
import models.Tile;
import util.DataContainers.AgentSettings;
import util.DataContainers.BoardSettings;
import util.DataContainers.EvolutionResults;
import util.helpers.CrossoverFunc;
import util.helpers.EvalFunc;
import util.helpers.RepopulateFunc;

import java.sql.SQLOutput;
import java.util.*;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        long seed = 19012025;

        Darwin darwin = new Darwin(Settings.MAX_AGENTS, Main::crossover, Main::evaluation, Main::repopulate, seed);
        byte[] init_board = darwin.getInitBoard().getAllTiles();
        RailroadsWindow window = new RailroadsWindow(init_board);

        //System.out.println("Gen 0");

        while(true){

            EvolutionResults res = darwin.evolve();
            //if (res.generation() > 1) break;

            //System.out.println("Gen " + res.generation());
            window.updateAgentDisplay(res);

        }
    }

    public static ArrayList<Agent> repopulate(Collection<Agent> agents, CrossoverFunc<Agent> crossoverFunc, long seed) {
        ArrayList<Agent> newAgents = new ArrayList<>();
        ArrayList<Agent> exAgents = new ArrayList<>(agents);
        Random rand = new Random(seed);
        final long maxScore = exAgents.stream().mapToLong(Agent::getScore).max().orElse(1);
        final long minScore = exAgents.stream().mapToLong(Agent::getScore).min().orElse(0);

        //System.out.println("RMAX " + maxScore);
        //System.out.println("RMIN " + minScore);

        //double[] scores = exAgents.stream().mapToDouble( x -> 1.0 - (x.getScore() - minScore)/(double)(maxScore - minScore)).toArray();

        for(int i = 0; newAgents.size() < railroads.Settings.MAX_AGENTS; i=(i+1)%exAgents.size()) {

            Agent agent = exAgents.get(i);

            long agentScoreRaw = agent.getScore();
            double agentScore = 1 - (agentScoreRaw - minScore)/(double)(maxScore - minScore);

            //System.out.println("RAW: "  + agentScoreRaw);
            //System.out.println("NORM: " + agentScore);

            if(rand.nextDouble() > agentScore) continue;
            //System.out.println("Added");
            newAgents.add(exAgents.get(i));
            if(newAgents.size()%2==0){
                Agent a = exAgents.removeLast();
                Agent b = exAgents.removeLast();
                Agent c1 = crossoverFunc.Cross(a,b, rand.nextLong());
                Agent c2 = crossoverFunc.Cross(b,a, rand.nextLong());
                newAgents.add(c1);
                newAgents.add(c2);
                newAgents.add(a);
                newAgents.add(b);
            }
        }

        return newAgents;
    }

    public static Agent crossover(Agent a, Agent b, long seed) {
        Random rand = new Random(seed);
        Genome genomeA = a.getGenome();
        byte[] genomeB = b.getGenome().serialize();
        Genome child = new Genome(genomeA.serialize());
        int spliceStart = rand.nextInt(genomeB.length/ Gene.GENE_SIZE);
        int spliceEnd = rand.nextInt(spliceStart,genomeB.length/ Gene.GENE_SIZE);
        for(int i=spliceStart; i < spliceEnd; i+=3){
            child.addGene(Arrays.copyOfRange(genomeB,(i*Gene.GENE_SIZE),(i*Gene.GENE_SIZE)+Gene.GENE_SIZE));
        }

        while (rand.nextFloat() <= Settings.MUTATION_CHANCE){
            short x = (short) rand.nextInt(Settings.BOARD_WIDTH);
            short y = (short) rand.nextInt(Settings.BOARD_HEIGHT);
            child.removeGene(x,y);
            if(rand.nextFloat() < 0.3) continue;
            byte tile = Tile.validTiles[rand.nextInt(Tile.validTiles.length)];
            byte[] gene = Gene.makeGene(x,y,tile);
            child.addGene(gene);
        }
        return new Agent(a.getInitialBoard(), child);

    }

    public static long evaluation(Agent agent){
        boolean allFinished = true;
        HashSet<Integer> visitedTiles = new HashSet<>();
        Board board = agent.getBoard();
        for(short[] station: board.getStations()){
            List<Integer> path = evalStation(board, station);
            if(path.isEmpty()){
                allFinished = false;
                break;
            }
            visitedTiles.addAll(path);
        }

        long pathScore = 0;
        if (allFinished || true){
            pathScore = visitedTiles.stream().mapToInt(pos -> {
                short[] xy = Gene.positionToXY(pos);
                byte tile = board.getTile(xy[0], xy[1]);
                return Tile.getValue(tile);
            }).sum();
        }
        long fullScore = 0;
        for(int i = 0; i < Settings.BOARD_WIDTH; i++)
            for (int j = 0; j < Settings.BOARD_HEIGHT; j++)
                fullScore += Tile.getValue(board.getTile(i, j));
        if(allFinished){
            System.out.println("SUCCESS");
            return pathScore*pathScore + (long)Math.sqrt(fullScore);
        }
        //System.out.println("FAIL: " + fullScore*fullScore);
        return (pathScore*pathScore + (long)Math.sqrt(fullScore)) * (pathScore*pathScore + (long)Math.sqrt(fullScore));
    }

    public static List<Integer> evalStation(Board board, short[] station){

        boolean found = false;

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parents = new HashMap<>();
        List<Integer> shortestPath = new ArrayList<>();

        int goal = Gene.xyToPosition(station[2], station[3]);

        queue.add(Gene.xyToPosition(station[0], station[1]));

        while(!queue.isEmpty()){
            int pos = queue.remove();
            if(visited.contains(pos)) continue;
            if (pos == goal){
                found = true;
                break;
            }
            short[] xy = Gene.positionToXY(pos);
            byte tile = board.getTile(xy[0], xy[1]);
            if(Tile.hasDirection(tile, Tile.LEFT_TILE)) {
                int nx = xy[0]-1;
                if(nx>=0) {
                    int npos = Gene.xyToPosition((short) nx, xy[1]);
                    queue.add(npos);
                    parents.put(npos, pos);
                }
            }
            if(Tile.hasDirection(tile, Tile.RIGHT_TILE)) {
                int nx = xy[0]+1;
                if(nx<=Settings.BOARD_WIDTH) {
                    int npos = Gene.xyToPosition((short) nx, xy[1]);
                    queue.add(npos);
                    parents.put(npos, pos);
                }
            }
            if(Tile.hasDirection(tile, Tile.UP_TILE)) {
                int ny = xy[1]-1;
                if(ny>=0) {
                    int npos = Gene.xyToPosition(xy[0], (short) ny);
                    queue.add(npos);
                    parents.put(npos, pos);
                }
            }
            if(Tile.hasDirection(tile, Tile.DOWN_TILE)) {
                int ny = xy[1]+1;
                if(ny<=Settings.BOARD_HEIGHT) {
                    int npos = Gene.xyToPosition(xy[0], (short) ny);
                    queue.add(npos);
                    parents.put(npos, pos);
                }
            }
            visited.add(pos);
        }

        if(found){
            Integer node = goal;
            while(node != null){
                shortestPath.add(node);
                node = parents.get(node);
            }
            return shortestPath;
        }
        return shortestPath;
    }


}
