package graphics;

import dto.EvaluatedSolution;
import models.Tile;
import models.Transform;
import railroads.Board;
import railroads.Settings;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLOutput;

public class AgentDisplay extends JPanel {


    public AgentDisplay(EvaluatedSolution solution) {
        setLayout(new GridLayout(Settings.BOARD_HEIGHT, Settings.BOARD_WIDTH));
        var special = solution.rawEvaluation();
        var board = solution.solution().solution();
        for(int y = 0; y < Settings.BOARD_HEIGHT; y++){
            for(int x = 0; x < Settings.BOARD_WIDTH; x++){
                var tile = board.getTile(x,y);
                RailDisplay rd;
                var et = new Transform(x,y);
                if (special.stream().anyMatch( g -> g.getTransform().equals(et))){
                    rd = new RailDisplay(tile, Color.BLUE);
                }else{
                    rd = new RailDisplay(tile, Color.black);
                }
                add(rd);
            }
        }
    }
}
