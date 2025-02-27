package models;

public class Tile {
    private final int tileType;

    public Tile(int tileType) {
        this.tileType = tileType;
    }

    public int getTileType() {
        return tileType;
    }

    public boolean isStation(){
        return  (tileType&0b10000)>0;
    }

    public boolean isTJunction(){
        return switch (tileType) {
            case TileType.UP_T, TileType.DOWN_T, TileType.LEFT_T, TileType.RIGHT_T -> true;
            default -> false;
        };
    }

    public boolean isTurn(){
        return  switch (tileType) {
            case TileType.UP_RIGHT, TileType.UP_LEFT, TileType.DOWN_RIGHT, TileType.DOWN_LEFT -> true;
            default -> false;
        };
    }

    public int getValued(){
        if(isTJunction()) return 3;
        if(isTurn() || tileType == TileType.HORIZONTAL || tileType == TileType.VERTICAL) return 2;
        if(tileType == TileType.CROSS) return 4;
        return 0;
    }

    public boolean hasDirection(int tileType){
        return (this.tileType&tileType) > 0;
    }
}
