package railroads;

import models.Transform;
import models.StationTrack;
import models.Tile;

import java.util.Random;

public class Board {
    private final short WIDTH, HEIGHT;
    private final Tile[] board;
    private final int STATION_COUNT;
    private final StationTrack[] stations;

    private final Random rand;

    public Board(Board board) {
        this.WIDTH = board.WIDTH;
        this.HEIGHT = board.HEIGHT;
        this.STATION_COUNT = board.STATION_COUNT;
        this.board = new Tile[WIDTH * HEIGHT];
        this.rand = board.rand;
        this.stations = new StationTrack[STATION_COUNT];
        for (int y=0; y<HEIGHT; y++) {
            for (int x=0; x<WIDTH; x++) {
                setTile(x,y, board.getTile(x,y));
            }
        }
        for(int i=0; i<stations.length; i++){
            stations[i] = new StationTrack(board.stations[i]);
        }
    }

    public Board(long seed) {
        WIDTH = Settings.BOARD_WIDTH;
        HEIGHT = Settings.BOARD_HEIGHT;
        STATION_COUNT = Settings.MAX_TRAINS;
        stations = new StationTrack[STATION_COUNT];
        board = new Tile[WIDTH * HEIGHT];
        rand = new Random(seed);
        initBoard();
    }

    private void initBoard(){
        //i is row, j is col
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i * WIDTH + j] = Tile.None;
            }
        }

        for (int i = 0; i < STATION_COUNT; i++) {
            Transform start;
            Transform end;
            while(true){
                int beginx = rand.nextInt(0, (int)Math.ceil(WIDTH*0.3));
                int beginy = rand.nextInt(HEIGHT);
                if (getTile(beginx, beginy) == Tile.Station) continue;
                setTile(beginx, beginy, Tile.Station);
                start = new Transform(beginx, beginy);
                break;
            }
            while(true){
                int endx = rand.nextInt((int)Math.ceil(WIDTH*0.7), WIDTH);
                int endy = rand.nextInt(HEIGHT);
                if (getTile(endx, endy) == Tile.Station) continue;
                setTile(endx, endy, Tile.Station);
                end = new Transform(endx, endy);
                break;
            }
            stations[i] = new StationTrack(start, end);
        }
    }

    public Tile getTile(int x, int y){
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) return Tile.None;
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

    public StationTrack[] getStations(){
        return stations;
    }

    public Tile[] getAllTiles(){
        return board;
    }
}
