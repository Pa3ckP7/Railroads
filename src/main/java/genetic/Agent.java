package genetic;

import models.Gene;
import models.Tile;
import railroads.Board;
import java.util.ArrayList;
import java.util.Random;

public class Agent {
    private ArrayList<Gene> genome;
    private Board board;
    private float boardFill;

    public Agent (Board board, ArrayList<Gene> genome) {
        this.board = board;
        this.genome = genome;
        this.boardFill = (float)(genome.size()/board.getSize());
    }

    public static Agent withRandomGenome(Board board, float minFill, float maxFill, long seed) {
        Random rand = new Random(seed);
        ArrayList<Gene> genome = new ArrayList<>();
        float boardFill = rand.nextFloat(minFill,maxFill);
        int geneCount = (int) (board.getSize()*boardFill);
        int w = board.getWidth();
        int h = board.getHeight();
        for (int i = 0; i < geneCount; i++) {
            byte tile = (byte) (rand.nextInt(Tile.NONE_TILE, Tile.ALL_TILE)&0b1111);
            int x = rand.nextInt(w);
            int y = rand.nextInt(h);
            if(Tile.isStation(board.getTile(x, y)))continue;
            genome.add(new Gene(x, y, tile));
        }
        return new Agent (board, genome);
    }

    public Board solve(){
        for (Gene gene : genome) {
            board.setTile(gene.getX(), gene.getY(), gene.getTileType());
        }
        return board;
    }
}
