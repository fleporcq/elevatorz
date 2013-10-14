package elevator.models.engines;

import elevator.Logger;
import elevator.models.Command;
import elevator.models.Direction;
import elevator.models.Elevator;
import elevator.models.requests.Call;
import elevator.models.requests.Go;

public class BasicEngine implements ElevatorEngine{

    private Elevator elevator;

    public BasicEngine(Elevator elevator){
        this.elevator = elevator;
    }

    private int countUserWantExit() {
        int currentFloor = elevator.getFloor();
        int count = 0;
        for (Go go : elevator.getGos()) {
            if (go.getFloor() == currentFloor) {
                count++;
            }
        }

        return count;
    }

    private int countUserWantEnterAndGoTo(Direction nextDirection) {
        int currentFloor = elevator.getFloor();
        int count = 0;
        for (Call call : elevator.getCalls()) {
            if (call.getFloor() == currentFloor && (nextDirection == null || call.getDirection().equals(nextDirection))) {
                count++;
            }
        }

        return count;
    }

    private boolean stopToCurrentFloor(Direction nextDirection) {
        return countUserWantExit() + countUserWantEnterAndGoTo(nextDirection) > 0;
    }

    private int computeDistanceTo(int toFloor) {
        return Math.abs(elevator.getFloor() - toFloor);
    }

    private int countGoTo(Direction direction) {
        int currentFloor = elevator.getFloor();
        int count = 0;
        for (Go go : elevator.getGos()) {
            int floor = go.getFloor();
            if ((Direction.UP.equals(direction) && floor > currentFloor) ||
                    (Direction.DOWN.equals(direction) && floor < currentFloor)) {
                count += elevator.HIGHER_FLOOR - computeDistanceTo(floor);
            }
        }

        return count;
    }

    private int countCallFrom(Direction direction) {
        int currentFloor = elevator.getFloor();
        int count = 0;
        for (Call call : elevator.getCalls()) {
            int floor = call.getFloor();
            if ((Direction.UP.equals(direction) && floor > currentFloor) ||
                    (Direction.DOWN.equals(direction) && floor < currentFloor)) {
                count += elevator.HIGHER_FLOOR - computeDistanceTo(floor);
            }
        }

        return count;
    }

    private Direction getNextDirection() {
        Direction nextDirection = null;
        int currentFloor = elevator.getFloor();

        int scoreToUp = 2 * countGoTo(Direction.UP) + countCallFrom(Direction.UP);
        int scoreToDown = 2 * countGoTo(Direction.DOWN) + countCallFrom(Direction.DOWN);

        if (elevator.getFloorHistory().hasCycle()) {
            Logger.info("Pre-cycle detected! ");
            if (Direction.DOWN.equals(elevator.getLastDirection()) && currentFloor > elevator.LOWER_FLOOR) {
                nextDirection = Direction.DOWN;
            }
            if (Direction.UP.equals(elevator.getLastDirection()) && currentFloor < elevator.HIGHER_FLOOR) {
                nextDirection = Direction.UP;
            }
        } else if (scoreToUp > scoreToDown) {
            nextDirection = Direction.UP;
        } else if (scoreToUp < scoreToDown) {
            nextDirection = Direction.DOWN;
        } else if (currentFloor > elevator.LOWER_FLOOR) {
            nextDirection = Direction.DOWN;
        } else if (currentFloor == elevator.LOWER_FLOOR) {
            nextDirection = Direction.UP;
        }

        return nextDirection;
    }

    public Command computeNextCommand(){
        Command nextCommand = null;

        Direction nextDirection = getNextDirection();

        if (elevator.isOpen()) {
            nextCommand = Command.CLOSE;
        } else if (stopToCurrentFloor(nextDirection)) {
            nextCommand = Command.OPEN;
        } else if (Direction.UP.equals(nextDirection)) {
            nextCommand = Command.UP;
        } else if (Direction.DOWN.equals(nextDirection)) {
            nextCommand = Command.DOWN;
        }

        if (nextCommand == null) {
            nextCommand = Command.NOTHING;
        }

        return nextCommand;
    }

}
