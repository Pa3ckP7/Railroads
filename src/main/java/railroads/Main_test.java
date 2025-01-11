package railroads;

import models.Gene;
import util.logging.LoggerFactory;

import java.util.Random;
import java.util.logging.Logger;

public class Main_test {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getClassLogger(Main_test.class);

        byte[] gene = Gene.makeGene((short) 10, (short) 10, (byte) 0);

        System.out.println("Position: " + Gene.getPosition(gene));
        System.out.println("X: " + Gene.getX(gene));
        System.out.println("Y: " + Gene.getY(gene));


    }
}
