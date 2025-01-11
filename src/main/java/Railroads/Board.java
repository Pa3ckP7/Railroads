package railroads;

import models.Tile;

import java.util.Random;

public class Board {
    private final short WIDTH, HEIGHT;
    private final byte[] board;
    private final int STATION_COUNT;
    private final int[][] stations;

    private final Random rand;

    public Board(short width, short height, int stationCount) {
        WIDTH = width;
        HEIGHT = height;
        STATION_COUNT = stationCount;
        stations = new int[STATION_COUNT][4];
        board = new byte[WIDTH * HEIGHT];
        rand = new Random();
        init_board();
    }

    public Board(short width, short height, int stationCount, long seed) {
        WIDTH = width;
        HEIGHT = height;
        STATION_COUNT = stationCount;
        stations = new int[STATION_COUNT][4];
        board = new byte[WIDTH * HEIGHT];
        rand = new Random(seed);
        init_board();
    }

    private void init_board(){
        //i is row, j is col
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i * WIDTH + j] = Tile.NONE_TILE;
            }
        }

        for (int i = 0; i < STATION_COUNT; i++) {
            while(true){
                int beginx = rand.nextInt(WIDTH);
                int beginy = rand.nextInt(HEIGHT);
                if (getTile(beginx, beginy) == Tile.STATION) continue;
                setTile(beginx, beginy, Tile.STATION);
                stations[i][0] = beginx;
                stations[i][1] = beginy;
                break;
            }
            while(true){
                int endx = rand.nextInt(WIDTH);
                int endy = rand.nextInt(HEIGHT);
                if (getTile(endx, endy) == Tile.STATION) continue;
                setTile(endx, endy, Tile.STATION);
                stations[i][2] = endx;
                stations[i][3] = endy;
                break;
            }
        }
    }

    public byte getTile(int x, int y){
        return board[y * WIDTH + x];
    }

    public void setTile(int x, int y, byte tile){
        board[y * WIDTH + x] = tile;
    }

    public void printBoard(){
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(Tile.isStation(getTile(j, i))){
                    char c = 'S';
                    for (int k = 0; k < STATION_COUNT; k++) {
                        if(stations[k][0] == j && stations[k][1] == i){
                            c = (char)(k+'A');
                            break;
                        }
                        if(stations[k][2] == j && stations[k][3] == i){
                            c = (char)(k+'a');
                            break;
                        }
                    }
                    System.out.print(c);
                    continue;
                }
                System.out.print(Tile.toString(getTile(j, i)));
            }
            System.out.println();
        }
    }

    public void randomize(){
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(Tile.isStation(getTile(i, j)))continue;
                byte tile = Tile.validTiles[rand.nextInt(Tile.validTiles.length)];
                setTile(i, j, tile);
            }
        }
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
}
