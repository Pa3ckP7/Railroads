package genetic;

import models.Gene;
import models.Genome;
import models.Tile;
import railroads.Board;
import railroads.Settings;
import util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Agent {
    private Genome genome;

    public Board getBoard() {
        return board;
    }

    private Board board;
    private float boardFill;

    private Board initialBoard;
    private long Score = 0;

    public Agent (Board board, Genome genome) {
        this.board = board;
        this.initialBoard = board;
        this.genome = genome;
        this.boardFill = (float)(genome.size()/board.getSize());
    }

    public Genome getGenome() {
        return genome;
    }

    public byte[] getGene(short x, short y){
        return genome.getGene(x, y);
    }

    public byte[] eraseGene(short x, short y){
        byte[] gene = getGene(x, y);
        genome.removeGene(x, y);
        return gene;
    }

    public void insertGene(byte[] gene){
        genome.addGene(gene);
    }

    public static Agent withRandomGenome(Board board, float minFill, float maxFill, long seed) {
        Random rand = new Random(seed);
        Genome genome = new Genome();
        float boardFill = rand.nextFloat(minFill,maxFill);
        int geneCount = (int) (board.getSize()*boardFill);
        int w = board.getWidth();
        int h = board.getHeight();
        for (int i = 0; i < geneCount; i++) {
            byte tile = Tile.validTiles[rand.nextInt(Tile.validTiles.length)];
            short x = (short)rand.nextInt(w);
            short y = (short)rand.nextInt(h);
            byte[] gene = Gene.makeGene(x,y, tile);
            genome.addGene(gene);
        }
        return new Agent (board, genome);
    }

    public Board solve(){
        Integer[] positions = genome.getGenePositions();
        for(int position: positions){
            byte[] gene = genome.getGene(position);

            //for(byte b: gene)
            //    System.out.println(Utils.byteToString(b));

            short x = Gene.getX(gene);
            short y = Gene.getY(gene);

            //System.out.println("X: " + x + " Y: " + y);

            byte btile = board.getTile(x, y);
            if (Tile.isStation(btile)) continue;

            board.setTile(x, y, Gene.getTile(gene));
        }
        return board;
    }

    public void setScore(long score) {
        this.Score = score;
    }

    public long getScore() {
        return Score;
    }

    public Board getInitialBoard() {
        return initialBoard;
    }
}
