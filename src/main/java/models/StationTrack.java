package models;

public class StationTrack {
    private Transform startpoint;
    private Transform endpoint;

    public StationTrack(Transform startpoint, Transform endpoint){
        this.startpoint = startpoint;
        this.endpoint = endpoint;
    }

    public StationTrack(StationTrack stationTrack){
        this.startpoint = new Transform(stationTrack.startpoint);
        this.endpoint = new Transform(stationTrack.endpoint);
    }

    public Transform getStartpoint() {
        return startpoint;
    }

    public Transform getEndpoint() {
        return endpoint;
    }
}
