package railroads;

import models.Agent;
import graphics.RailroadsWindow;
import models.Gene;
import models.Tile;
import dto.EvolutionResults;
import util.helpers.CrossoverFunc;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        long seed = 19012025;

        Darwin darwin = new Darwin(Settings.MAX_AGENTS, Main::crossover, Main::evaluation, Main::repopulate, seed);
        Board init_board = darwin.getInitBoard();
        RailroadsWindow window = new RailroadsWindow(init_board);

        //System.out.println("Gen 0");

        while(true){

            EvolutionResults res = darwin.evolve();
            //if (res.generation() > 1) break;

            //System.out.println("Gen " + res.generation());
            window.updateAgentDisplay(res);

        }
    }

    public static ArrayList<Agent> repopulate(Collection<Agent> agents, CrossoverFunc crossoverFunc, long seed) {
        ArrayList<Agent> newAgents = new ArrayList<>();
        ArrayList<Agent> exAgents = new ArrayList<>(agents);
        Random rand = new Random(seed);
        exAgents.sort( Comparator.comparingLong(a -> a.getScore().orElse(Long.MAX_VALUE)));
        long[] tickets = generateTickets(exAgents.size());
        long alltickets = Arrays.stream(tickets).sum();

        Set<Agent> usedAgents = new HashSet<>();

        while(exAgents.size() < Settings.MAX_AGENTS){
            for (int i=0; i< exAgents.size(); i++){
                Agent a = exAgents.get(i);
                if(usedAgents.contains(a)) continue;
                alltickets -= tickets[i];
                usedAgents.add(a);
                newAgents.add(a);
                break;
            }
            for(int i=1; i< exAgents.size(); i++){
                Agent a = exAgents.get(i);
                if(usedAgents.contains(a)) continue;
                double chance = 1-tickets[i]/(double)alltickets;
                if(rand.nextDouble() < chance) continue;
                Agent b = newAgents.getLast();
                Agent c = crossoverFunc.Cross(a,b, rand.nextLong());
                Agent d = crossoverFunc.Cross(b, a, rand.nextLong());
                newAgents.add(a);
                newAgents.add(b);
                newAgents.add(c);
                newAgents.add(d);
                usedAgents.add(b);
                alltickets-= tickets[i];
                break;
            }
        }



        return newAgents;
    }

    public static long[] generateTickets(int count){

        int MAX_SCORE = count;
        double ratio = 0.95;

        double score = MAX_SCORE;

        long[] tickets = new long[count];
        for (int i = 0; i < count; i++) {
            tickets[i] =  Math.max((long)Math.ceil(score), 1);
            score *= ratio;
        }

        return tickets;
    }

    public static Agent crossover(Agent a, Agent b, long seed) {
        Random rand = new Random(seed);
        Genome newGenome = new Genome(a.getGenome());
        int[] genomeB = a.getGenome().getGenePositions();
        int[] genomeA = b.getGenome().getGenePositions();
        int spliceA = rand.nextInt(genomeA.length);
        int spliceB = rand.nextInt(genomeB.length);
        for (int i = 0; i < spliceA; i++) {
            newGenome.addGene(new Gene(a.getGenome().getGene(genomeA[i])));
        }
        for (int i = spliceB; i < genomeB.length; i++) {
            newGenome.addGene(new Gene(b.getGenome().getGene(genomeB[i])));
        }
        return new Agent(a.getBoard(), newGenome);
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
