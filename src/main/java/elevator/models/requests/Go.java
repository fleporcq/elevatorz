package elevator.models.requests;

public class Go{

    private int floor;

    public Go(int floorToGo) {
        floor = floorToGo;
    }

    public int getFloor() {
        return floor;
    }

}
