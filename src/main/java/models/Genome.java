package models;

import java.util.*;

public class Genome {
    private final HashMap<Integer, Byte> genes;

    public Genome() {
        genes = new HashMap<>();
    }

    public Genome(byte[] serializedGenes){
        genes = new HashMap<>();
        for (int i = 0; i < serializedGenes.length; i+=5) {
            byte[] gene = Arrays.copyOfRange(serializedGenes, i, i+5);
            if(genes.containsKey(Gene.getPosition(gene))){
                Byte oldTile = genes.get(Gene.getPosition(gene));
                oldTile = (byte)(oldTile|Gene.getTile(gene));
                genes.put(Gene.getPosition(gene), oldTile);
            }else{
                genes.put(Gene.getPosition(gene), Gene.getTile(gene));
            }
        }
    }


    public byte[] serialize(){
        byte[] serializedGenes = new byte[genes.size()*5];
        Integer[] keySet = genes.keySet().toArray(Integer[]::new);

        for(int i = 0; i < keySet.length; i++){
            byte[] gene = Gene.makeGene(keySet[i], genes.get(keySet[i]));
            System.arraycopy(gene, 0, serializedGenes, i * 5, 5);
        }
        return serializedGenes;
    }

    public boolean hasGene(short x, short y){
        int key = Gene.xyToPosition(x, y);
        return genes.containsKey(key);
    }

    public void addGene(byte[] gene){
        int key = Gene.getPosition(gene);
        if(genes.containsKey(key)){
            byte oldTile = genes.get(key);
            oldTile = (byte)(oldTile|Gene.getTile(gene));
            genes.put(key, oldTile);
            return;
        }
        genes.put(key, Gene.getTile(gene));
    }

    public byte[] removeGene(short x, short y){
        int key = Gene.xyToPosition(x, y);
        byte[] gene = getGene(key);
        genes.remove(key);
        return gene;
    }

    public void removeGene(int position){
        genes.remove(position);
    }

    public int size(){
        return genes.size();
    }

    public byte[] getGene(short x, short y){
        int key = Gene.xyToPosition(x, y);
        return getGene(key);
    }

    public byte[] getGene(int position){
        if(!genes.containsKey(position)) return null;
        byte tile = genes.get(position);
        return Gene.makeGene(position, tile);
    }

    public Integer[] getGenePositions(){
        return genes.keySet().toArray(Integer[]::new);
    }



}
