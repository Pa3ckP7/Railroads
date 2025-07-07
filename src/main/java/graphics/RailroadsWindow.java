package graphics;

import models.Agent;
import railroads.Board;
import dto.EvolutionResults;

import javax.swing.*;
import java.awt.*;

public class RailroadsWindow extends JFrame {

    AgentDisplay agentDisplay;
    JPanel statDisplay;
    JLabel generation;
    JLabel minScoreAll;
    JLabel genMaxScore;
    JLabel genMinScore;

    long minAllTime = -1;

    public RailroadsWindow(Board board) {
        super("Railroads");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        agentDisplay = new AgentDisplay(board);
        add(agentDisplay);

        statDisplay = new JPanel();
        //statDisplay.setLayout(new GridLayout(1, 4));
        generation = new JLabel("Generation: 0");
        minScoreAll = new JLabel("Min Score: None");
        genMaxScore = new JLabel("Generation Max Score: None");
        genMinScore = new JLabel("Generation Min Score: None");
        statDisplay.add(generation);
        statDisplay.add(minScoreAll);
        statDisplay.add(genMaxScore);
        statDisplay.add(genMinScore);
        add(statDisplay, BorderLayout.NORTH);

        revalidate();
        repaint();

        setVisible(true);
    }


    public void updateAgentDisplay(EvolutionResults results) {
        generation.setText("Generation: " + results.generation());
        if ( minAllTime == -1 || minAllTime > results.lowestScore() ) {
            minAllTime = results.lowestScore();
            minScoreAll.setText("Min Score: " + minAllTime);
        }
        genMaxScore.setText("Generation Max Score: " + results.highestScore());
        genMinScore.setText("Generation Min Score: " + results.lowestScore());

        Agent a = results.bestAgent();
        a.solve();
        remove(agentDisplay);
        agentDisplay = new AgentDisplay(a.getBoard().getAllTiles());
        add(agentDisplay);

        revalidate();
        repaint();
    }
}
