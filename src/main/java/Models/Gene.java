package models;

public class Gene {
    private final int x;
    private final int y;
    private byte tileType;

    public byte getTileType() {
        return tileType;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public byte addDirection(byte direction) {
        tileType = Tile.add(tileType, direction);
        return tileType;
    }

    public byte removeDirection(byte direction) {
        tileType = Tile.subtract(tileType, direction);
        return tileType;
    }


    public Gene() {
        this.x = 0;
        this.y = 0;
        this.tileType = Tile.NONE_TILE;
    }

    public Gene(int x, int y) {
        this.x = x;
        this.y = y;
        this.tileType = Tile.NONE_TILE;
    }

    public Gene(int x, int y, byte tileType) {
        this.x = x;
        this.y = y;
        this.tileType = tileType;
    }

    @Override
    public String toString() {
        return String.format("Gene(%d,%d,%s)", x, y, Tile.toString(tileType));
    }

    public int compareTo(Gene gene) {
        if (Tile.isStation(tileType)) return -1;
        if (Tile.isStation(gene.tileType)) return -1;
        int positionalDifference_x = this.x - gene.x;
        int positionalDifference_y = this.y - gene.y;
        int positionalDifference = (int)Math.ceil(Math.sqrt(positionalDifference_x * positionalDifference_x + positionalDifference_y * positionalDifference_y));
        int geneDifference = Tile.compare(tileType, gene.tileType);

        return positionalDifference+geneDifference;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Gene gene && compareTo(gene) == 0;
    }
}
