package graphics;

import models.Tile;

import javax.swing.*;
import java.awt.*;

public class RailDisplay extends JPanel {

    private Tile railData;
    private Color color;

    public RailDisplay(Tile rail, Color color) {

        railData = rail;
        this.color = color;
        //System.out.println(railData);

        //add(new JLabel("" + rail));

        //repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke((float) (getWidth()/3)));
        g2d.setColor(color);

        //System.out.println(railData);

        if(railData == Tile.Station){
            g2d.fillRect((int) (getWidth()*0.1), (int) (getHeight()*0.1), (int) (getWidth()*0.9), (int) (getHeight()*0.9));
            return;
        }

        if(railData.left){
            g2d.drawLine(cx, cy, 0, cy);
        }

        if(railData.right){
            g2d.drawLine(cx, cy, getWidth(), cy);
        }

        if(railData.up){
            g2d.drawLine(cx, cy, cx, 0);
        }

        if(railData.down){
            g2d.drawLine(cx, cy, cx, getHeight());
        }


    }
}
