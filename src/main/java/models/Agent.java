package models;

import dto.Solution;
import railroads.Board;

import java.util.*;

public class Agent {
    private TreeMap<Integer, Gene> genome;

    public Solution solve(Board board){
        var positions = genome.keySet();
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

    public Agent(TreeMap<Integer, Gene> genome){
        this.genome = genome;
    }

    public TreeMap<Integer, Gene> evolve(TreeMap<Integer, Gene> partner){
        var newGenome = new TreeMap<Integer, Gene>();
        for(var gene: genome.entrySet()){
            newGenome.put(gene.getKey(), new Gene(gene.getValue()));
        }
        for(var gene: partner.entrySet()){
            newGenome.put(gene.getKey(), new Gene(gene.getValue()));
        }
        return newGenome;
    }
    public TreeMap<Integer, Gene> getGenome() {
        return genome;
    }

    public static Agent generateAgent(long seed){
        Random rand = new Random(seed);
        //TODO finish generating
    }

}
