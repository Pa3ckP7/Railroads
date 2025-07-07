package models;


public class Gene{
    private Transform transform;
    private Tile tile;

    public Transform getTransform() {
        return transform;
    }
    public Tile getTile() {
        return tile;
    }

    public Gene(Transform transform, Tile tile) {
        this.transform = transform;
        this.tile = tile;
    }

    public Gene(Gene gene){
        var transform = gene.getTransform();
        this.transform = new Transform(transform);
        this.tile = gene.getTile();
    }
}