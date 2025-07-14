package models;

public enum Tile {
    Horizontal(2, false, false, true, true),
    Vertical(2, true, true, false, false),
    Cross(10, true, true, true, true),
    UpT(5, true, false, true, true),
    DownT(5, false, true, true, true),
    LeftT(5, true, true, true, false),
    RightT(5, true, true, false, true),
    CornerTL(2, false, true, false, true),
    CornerTR(2, false, true, true, false),
    CornerBL(2, true, false, false, true),
    CornerBR(2, true, false, true, false),
    None(0, false, false, false, false),
    Station(0, true, true, true, true);

    public final int evalValue;
    public final boolean up, down, left, right;

    Tile(int evalValue, boolean up, boolean down, boolean left, boolean right) {
        this.evalValue = evalValue;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }
}
