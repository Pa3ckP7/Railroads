package models;


public class Gene{
    private short x;
    private short y;
    Tile tile;

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public int getPosition(){
        return  xyToPosition(x, y);
    }

    public Tile getTile() {
        return tile;
    }

    public Gene(short x, short y, int tile) {
        this.x = x;
        this.y = y;
        this.tile = new Tile(tile);
    }

    public Gene(Gene gene){
        this.x = gene.getX();
        this.y = gene.getY();
        this.tile = new Tile(gene.getTile().getTileType());
    }

    public static int xyToPosition(short x, short y){
        int val = y<<16;
        val+=x;
        return val;
    }

    public static int[] positionToXY(int position){
        int x = position & 0xFFFF; // Extract the lower 16 bits
        int y = position >>> 16;   // Extract the upper 16 bits
        return new int[]{x, y};
    }
}