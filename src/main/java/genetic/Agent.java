package genetic;

import models.Gene;
import models.Genome;
import models.Tile;
import models.TileType;
import railroads.Board;

import java.util.*;

public class Agent {
    private Genome genome;
    private Board board;
    private Board solution = null;
    private Optional<Long> score = Optional.empty();

    public void solve(){
        Set<Integer> positions = genome.getGenePositionsSet();
        solution = new Board(board);
        for (var pos: positions){
            int[] position = Gene.positionToXY(pos);
            var tile = solution.getTile(position[0], position[1]);
            if (tile.isStation()) {
                genome.removeGene(pos);
                continue;
            }
            solution.setTile(position[0], position[1],new Tile(tile.getTileType()));
        }
    }

    public Board getSolution() {
        if(solution == null){
            solve();
        }
        return solution;
    }

    public Agent(Board board, Genome genome){
        this.board = board;
        this.genome = genome;
    }

    public Agent(Board board, long seed){
        this.board = board;
        Random rand = new Random(seed);
        this.genome = new Genome();
        int w = board.getWidth();
        int h = board.getHeight();
        for(var y=0; y<h; y++){
            for(var x=0; x<w; x++){
                if(board.getTile(x, y).isStation()) continue;
                int tile = TileType.VALIDTILES[rand.nextInt(TileType.VALIDTILES.length)];
                genome.addGene(new Gene((short)x,(short)y,tile));
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public Optional<Long> getScore() {
        return score;
    }

    public void setScore(long score){
        this.score = Optional.of(score);
    }

    public Genome getGenome() {
        return genome;
    }

}
