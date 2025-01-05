package genetic;

import models.Gene;
import models.Tile;
import railroads.Board;
import java.util.ArrayList;
import java.util.Random;

public class Agent {
    private ArrayList<Gene> genome;

    public Board getBoard() {
        return board;
    }

    private Board board;
    private float boardFill;

    public Agent (Board board, ArrayList<Gene> genome) {
        this.board = board;
        this.genome = genome;
        this.boardFill = (float)(genome.size()/board.getSize());
    }

    public ArrayList<Gene> getGenome() {
        return genome;
    }

    public Gene getGene(int x, int y){
        for(Gene gene : genome){
            if(gene.getX() == x && gene.getY() == y) return gene;
        }
        return null;
    }

    public Gene eraseGene(int x, int y){
        Gene gene = getGene(x, y);
        genome.remove(gene);
        return gene;
    }

    public void insertGene(Gene gene){
        Gene ogene = getGene(gene.getX(), gene.getY());
        if(ogene != null){
            ogene.setTileType((byte) (gene.getTileType()| ogene.getTileType()));
        }
        genome.add(gene);
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
