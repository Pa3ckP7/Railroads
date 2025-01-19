package graphics;

import models.Tile;
import railroads.Settings;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLOutput;

public class AgentDisplay extends JPanel {


    public AgentDisplay(byte[] board) {
        setLayout(new GridLayout(Settings.BOARD_HEIGHT, Settings.BOARD_WIDTH));
        for(byte tile: board) {
            //System.out.println(tile);
            RailDisplay rd = new RailDisplay(tile);
            add(rd);
        }
    }
}
