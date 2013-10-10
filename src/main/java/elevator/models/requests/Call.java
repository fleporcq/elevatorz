package elevator.models.requests;

import elevator.models.Direction;

public class Call {

    private int floor;

    private Direction direction;

    public Call(int atFloor, Direction to) {
        floor = atFloor;
        direction = to;
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

}
