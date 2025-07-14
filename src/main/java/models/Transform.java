package models;

import java.util.Objects;

public class Transform implements Comparable<Transform>{

    public Transform(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Transform(Transform transform){
        this.x = transform.x;
        this.y = transform.y;
    }
    public int x;
    public int y;

    @Override
    public int compareTo(Transform o) {
        int cmp = Integer.compare(this.x, o.x);
        if (cmp != 0) return cmp;
        return Integer.compare(this.y, o.y);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Transform other)) return false;
        return x == other.x && y == other.y;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
