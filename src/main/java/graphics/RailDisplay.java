package graphics;

import models.Tile;

import javax.swing.*;
import java.awt.*;

public class RailDisplay extends JPanel {

    private byte railData;

    public RailDisplay(byte rail) {

        railData = rail;

        //System.out.println(railData);

        add(new JLabel("" + rail));

        //repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke((float) (getWidth()/3)));

        //System.out.println(railData);

        if(Tile.isStation(railData)){
            g2d.fillRect((int) (getWidth()*0.1), (int) (getHeight()*0.1), (int) (getWidth()*0.9), (int) (getHeight()*0.9));
            return;
        }

        if(Tile.hasDirection(railData, Tile.LEFT_TILE)){
            g2d.drawLine(cx, cy, getWidth(), cy);
        }

        if(Tile.hasDirection(railData, Tile.RIGHT_TILE)){
            g2d.drawLine(cx, cy, 0, cy);
        }

        if(Tile.hasDirection(railData, Tile.UP_TILE)){
            g2d.drawLine(cx, cy, cx, 0);
        }

        if(Tile.hasDirection(railData, Tile.DOWN_TILE)){
            g2d.drawLine(cx, cy, cx, getHeight());
        }


    }
}
