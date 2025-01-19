package models;

public class Gene {

    public static final int GENE_SIZE = 5;

    public static byte[] makeGene(short x, short y, byte tile){
        byte[] gene = new byte[5];
        gene[1] = (byte)x;
        gene[0] = (byte)(x>>8);
        gene[3] = (byte)y;
        gene[2] = (byte)(y>>8);
        gene[4] = tile;

        return gene;
    }

    public static byte[] makeGene(int position, byte tile){
        short y = (short)(position);
        short x = (short)(position>>16);
        return makeGene(x, y, tile);
    }

    public static byte getTile(byte[] gene){
        return gene[4];
    }

    public static int getPosition(byte[] gene){
        int position = getX(gene);
        position = position << 16;
        position = position | (short)(getY(gene));
        return position;
    }

    public static short getX(byte[] gene){
        short x = 0;
        x = (short) (x|gene[0]);
        x = (short) (x<<8);
        x = (short) (x|gene[1]);
        return x;
    }

    public static short getY(byte[] gene){
        short y = 0;
        y = (short) (y|gene[2]);
        y = (short) (y<<8);
        y = (short) (y|gene[3]);
        return y;
    }

    public static int xyToPosition(short x, short y){
        int position = x;
        position = position << 16;
        position = position|y;
        return position;
    }

    public static short[] positionToXY(int position){
        short x = (short)(position>>16);
        short y = (short)(position);

        return new short[]{x, y};
    }
}
