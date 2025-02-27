package models;

public class TileType {
    // bit breakdown stationflag,up,down,left,right
    public static final int NONE = 0b00000;
    public static final int CROSS = 0b01111;
    public static final int VERTICAL = 0b01100;
    public static final int HORIZONTAL = 0b00011;
    public static final int UP_T = 0b01011;
    public static final int DOWN_T = 0b00111;
    public static final int LEFT_T = 0b01110;
    public static final int RIGHT_T = 0b01101;
    public static final int UP_LEFT = 0b01010;
    public static final int UP_RIGHT = 0b01001;
    public static final int DOWN_LEFT = 0b00110;
    public static final int DOWN_RIGHT = 0b00101;
    public static final int STATION = 0b10000;
    public static final int UP = 0b1000;
    public static final int DOWN = 0b0100;
    public static final int LEFT = 0b0010;
    public static final int RIGHT = 0b0001;

    public static final int[] VALIDTILES = new int[]{
            NONE,
            CROSS,
            VERTICAL,
            HORIZONTAL,
            UP_T,
            DOWN_T,
            LEFT_T,
            RIGHT_T,
            UP_LEFT,
            UP_RIGHT,
            DOWN_LEFT,
            DOWN_RIGHT,
    };
    
}
