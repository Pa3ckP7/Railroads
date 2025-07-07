package models;

public class Transform{

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
}
