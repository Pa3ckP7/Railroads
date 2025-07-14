package models;

import dto.Solution;
import railroads.Board;
import railroads.Settings;

import java.util.*;

public class Agent {
    private TreeMap<Transform, Gene> genome;

    public Solution solve(Board board){
        var positions = new ArrayList<>(genome.keySet());
        for (var pos: positions){
            Gene gene = genome.get(pos);
            var transform = gene.getTransform();
            var tile = board.getTile(transform.x, transform.y);
            if (tile == Tile.Station) {
                genome.remove(pos);
                continue;
            }
            board.setTile(transform.x, transform.y, gene.getTile());
        }
        return new Solution(this, board);
    }

    public Agent(TreeMap<Transform, Gene> genome){
        this.genome = genome;
    }

    public TreeMap<Transform, Gene> evolve(TreeMap<Transform, Gene> partner){
        var newGenome = new TreeMap<Transform, Gene>();
        for(var gene: genome.entrySet()){
            newGenome.put(new Transform(gene.getKey()), new Gene(gene.getValue()));
        }
        for(var gene: partner.entrySet()){
            newGenome.put(new Transform(gene.getKey()), new Gene(gene.getValue()));
        }
        return newGenome;
    }
    public TreeMap<Transform, Gene> getGenome() {
        return genome;
    }

    public static Agent generateAgent(long seed){

        var tiles = Tile.values();

        Random rand = new Random(seed);

        TreeMap<Transform, Gene> genome =  new TreeMap<>();
        for(int i = 0; i < Settings.BOARD_WIDTH; i++){
            for(int j = 0; j < Settings.BOARD_HEIGHT; j++){
                if(rand.nextDouble() > Settings.AGENT_FILL) continue;
                Transform t = new Transform(i,j);
                Tile v = tiles[rand.nextInt(tiles.length)];
                if(v ==  Tile.Station) continue;
                genome.put(t,new Gene(t,v));
            }
        }
        return new Agent(genome);
    }

}
