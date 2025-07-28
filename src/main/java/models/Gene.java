package models;


import java.io.Serializable;
import java.util.Objects;

public class Gene implements Serializable {
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Gene other)) return false;
        return this.transform.equals(other.transform) && this.tile == other.tile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transform.x, transform.y, tile);
    }

    @Override
    public String toString() {
        return "Gene{" +
                "transform=" + transform +
                ", tile=" + tile +
                '}';
    }
}