package graphics;

import models.Tile;
import railroads.Board;
import railroads.Settings;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLOutput;

public class AgentDisplay extends JPanel {


    public AgentDisplay(Board board) {
        setLayout(new GridLayout(Settings.BOARD_HEIGHT, Settings.BOARD_WIDTH));

        for(Tile tile: board.getAllTiles()) {
            //System.out.println(tile);
            RailDisplay rd = new RailDisplay(tile);
            add(rd);
        }
    }
}
