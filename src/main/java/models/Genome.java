package models;

import java.util.*;

public class Genome {
    private final TreeMap<Integer, Gene> genes;

    public Genome() {
        genes = new TreeMap<>();
    }

    public Genome(Genome genome) {
        genes = new TreeMap<>();
        for(var entry : genome.genes.sequencedEntrySet()){
            var geneTemp = entry.getValue();
            genes.put(entry.getKey(), new Gene(geneTemp));
        }
    }

    public Genome(Gene[] genes){
        this.genes = new TreeMap<>();
        for(Gene gene : genes){
            addGene(gene);
        }
    }


    public Gene[] toArray(){
        return genes.values().toArray(Gene[]::new);
    }

    public boolean hasGene(int position){
        return genes.containsKey(position);
    }

    public boolean hasGene(short x, short y){
        int key = Gene.xyToPosition(x, y);
        return hasGene(key);
    }

    public boolean addGene(Gene gene){
        int key = gene.getPosition();
        if(hasGene(key)) return false;
        genes.put(key, gene);
        return true;
    }

    public void overrideGene(Gene gene){
        int key = gene.getPosition();
        genes.put(key, gene);
    }

    public Gene removeGene(int position){
        return genes.remove(position);
    }

    public Gene removeGene(short x, short y){
        int key = Gene.xyToPosition(x, y);
        return removeGene(key);
    }

    public int size(){
        return genes.size();
    }

    public Gene getGene(short x, short y){
        int key = Gene.xyToPosition(x, y);
        return getGene(key);
    }

    public Gene getGene(int position){
       return genes.getOrDefault(position, null);
    }

    public Set<Integer> getGenePositionsSet(){
        return genes.keySet();
    }

    public int[] getGenePositions(){
        return genes.keySet().stream().mapToInt(Integer::intValue).toArray();
    }



}
