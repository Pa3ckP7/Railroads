package models;

public enum Tile {
    Horizontal{
        public final int evalValue = 2;
    },
    Vertical{
        public final int evalValue = 2;
    },
    Cross {
        public final int evalValue = 4;
    },
    UpT{
        public final int evalValue = 3;
    },
    DownT{
        public final int evalValue = 3;
    },
    LeftT{
        public final int evalValue = 3;
    },
    RightT{
        public final int evalValue = 3;
    },
    None,
    Station;
    public final int evalValue = 0;
}
