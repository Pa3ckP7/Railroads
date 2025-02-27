package railroads;

import models.Tile;
import models.TileType;

import java.util.Random;

public class Board {
    private final short WIDTH, HEIGHT;
    private final Tile[] board;
    private final int STATION_COUNT;
    private final short[][] stations;

    private final Random rand;

    public Board(Board board) {
        this.WIDTH = board.WIDTH;
        this.HEIGHT = board.HEIGHT;
        this.STATION_COUNT = board.STATION_COUNT;
        this.board = new Tile[WIDTH * HEIGHT];
        this.rand = board.rand;
        this.stations = new short[STATION_COUNT][4];

        for (int y=0; y<HEIGHT; y++) {
            for (int x=0; x<WIDTH; x++) {
                setTile(x,y, new Tile(board.getTile(x,y).getTileType()));
            }
        }

        for(int i=0; i<stations.length; i++){
            stations[i][0] = board.stations[i][0];
            stations[i][1] = board.stations[i][1];
            stations[i][2] = board.stations[i][2];
            stations[i][3] = board.stations[i][3];
        }

    }

    public Board(long seed) {
        WIDTH = Settings.BOARD_WIDTH;
        HEIGHT = Settings.BOARD_HEIGHT;
        STATION_COUNT = Settings.MAX_TRAINS;
        stations = new short[STATION_COUNT][4];
        board = new Tile[WIDTH * HEIGHT];
        rand = new Random(seed);
        initBoard();
    }

    private void initBoard(){
        //i is row, j is col
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i * WIDTH + j] = new Tile(TileType.NONE);
            }
        }

        for (int i = 0; i < STATION_COUNT; i++) {
            while(true){
                int beginx = rand.nextInt(WIDTH);
                int beginy = rand.nextInt(HEIGHT);
                if (getTile(beginx, beginy).isStation()) continue;
                setTile(beginx, beginy, new Tile(TileType.STATION));
                stations[i][0] = (short)beginx;
                stations[i][1] = (short)beginy;
                break;
            }
            while(true){
                int endx = rand.nextInt(WIDTH);
                int endy = rand.nextInt(HEIGHT);
                if (getTile(endx, endy).isStation()) continue;
                setTile(endx, endy, new Tile(TileType.STATION));
                stations[i][2] = (short)endx;
                stations[i][3] = (short)endy;
                break;
            }
        }
    }

    public Tile getTile(int x, int y){
        return board[y * WIDTH + x];
    }

    public void setTile(int x, int y, Tile tile){
        board[y * WIDTH + x] = tile;
    }


    public int getHeight(){
        return HEIGHT;
    }

    public int getWidth(){
        return WIDTH;
    }

    public int getSize(){
        return WIDTH * HEIGHT;
    }

    public short[][] getStations(){
        return stations;
    }

    public Tile[] getAllTiles(){
        return board;
    }
}
