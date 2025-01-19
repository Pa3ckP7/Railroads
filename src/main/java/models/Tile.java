package models;

public class Tile {

    public static final byte NONE_TILE = 0b000000;
    public static final byte UP_TILE = 0b001000;
    public static final byte DOWN_TILE = 0b000100;
    public static final byte LEFT_TILE = 0b000010;
    public static final byte RIGHT_TILE = 0b000001;
    public static final byte ALL_TILE = 0b001111;
    public static final byte HORIZONTAL_TILE = LEFT_TILE | RIGHT_TILE;
    public static final byte VERTICAL_TILE = UP_TILE | DOWN_TILE;
    public static final byte UP_RIGHT_TILE = UP_TILE | RIGHT_TILE;
    public static final byte DOWN_RIGHT_TILE = DOWN_TILE | RIGHT_TILE;
    public static final byte DOWN_LEFT_TILE = DOWN_TILE | LEFT_TILE;
    public static final byte UP_LEFT_TILE = UP_TILE | LEFT_TILE;
    public static final byte UP_T_TILE = HORIZONTAL_TILE | UP_TILE;
    public static final byte DOWN_T_TILE = HORIZONTAL_TILE | DOWN_TILE;
    public static final byte LEFT_T_TILE = VERTICAL_TILE | LEFT_TILE;
    public static final byte RIGHT_T_TILE = VERTICAL_TILE | RIGHT_TILE;
    public static final byte STATION = 0b10000;
    public static final byte[] validTiles = {
            NONE_TILE,
            ALL_TILE,
            HORIZONTAL_TILE,
            VERTICAL_TILE,
            UP_RIGHT_TILE,
            DOWN_RIGHT_TILE,
            DOWN_LEFT_TILE,
            UP_LEFT_TILE,
            UP_T_TILE,
            DOWN_T_TILE,
            LEFT_T_TILE,
            RIGHT_T_TILE,
    };

    public static boolean isStation(byte tile){
        return (tile&STATION)>0;
    }

    public static byte join(byte tileA, byte tileB){
        if(isStation(tileA) || isStation(tileB)) return tileA;
        return (byte)(tileA&tileB);
    }

    public static int compare(byte tileA, byte tileB) {
        if(isStation(tileA)) return -1;
        if(isStation(tileB)) return -1;
        byte similarity = (byte) (tileA ^ tileB); // different bits return become 1 same become 0
        int diffCount = 0;
        for (int i= 0; i < 8; i++){
            if(((similarity >>> i)&1) == 1) diffCount++; //shifts the bit i spaces then masks the last bit. If the result equals one. The i-th bit is the same
        }
        return diffCount;
    }

    public static String toString(byte tile) {
        return switch (tile) {
            case NONE_TILE -> " ";
            case UP_TILE -> "UP TILE";
            case DOWN_TILE -> "DOWN TILE";
            case LEFT_TILE -> "LEFT TILE";
            case RIGHT_TILE -> "RIGHT TILE";
            case HORIZONTAL_TILE -> "HORIZONTAL";
            case VERTICAL_TILE -> "VERTICAL";
            case ALL_TILE -> "CROSS";
            case UP_RIGHT_TILE -> "UP RIGHT TILE";
            case UP_LEFT_TILE -> "UP LEFT TILE";
            case DOWN_RIGHT_TILE -> "DOWN RIGHT TILE";
            case DOWN_LEFT_TILE -> "DOWN LEFT TILE";
            case UP_T_TILE -> "UP T TILE";
            case DOWN_T_TILE -> "DOWN T TILE";
            case LEFT_T_TILE -> "LEFT T TILE";
            case RIGHT_T_TILE -> "RIGHT T TILE";
            case STATION -> "STATION";
            default -> "UNKNOWN";
        };
    }

    public static boolean isTurn(byte tileType){
        return switch (tileType){
            case UP_LEFT_TILE, UP_RIGHT_TILE, DOWN_LEFT_TILE, DOWN_RIGHT_TILE -> true;
            default -> false;
        };
    }

    public static boolean isTJunction(byte tileType){
        return switch (tileType){
            case UP_T_TILE, RIGHT_T_TILE, LEFT_T_TILE, DOWN_T_TILE -> true;
            default -> false;
        };
    }

    public static int getValue(byte tile){
        return switch ((Byte)tile){
            case Tile.ALL_TILE -> 4;
            case Byte b when Tile.isTJunction(b) -> 3;
            case Byte b when Tile.isTJunction(b)-> 2;
            case Tile.HORIZONTAL_TILE, Tile.VERTICAL_TILE -> 2;
            default -> 0;
        };
    }

    public static boolean hasDirection(byte tile, byte direction){
        return (tile&direction)>0;
    }
}
