package elevator.models;

import elevator.Logger;
import elevator.models.engines.BasicEngine;
import elevator.models.engines.ElevatorEngine;
import elevator.models.requests.Call;
import elevator.models.requests.Go;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Elevator {

    public final static int LOWER_FLOOR = 0;

    public final static int HIGHER_FLOOR = 5;

    private int floor = LOWER_FLOOR;

    private boolean open = false;

    private int userCount;

    private Direction lastDirection;

    private FloorHistory floorHistory = new FloorHistory();

    private CrashTest crashTest = new CrashTest();

    private ElevatorEngine engine = new BasicEngine(this);

    private List<Call> calls = new ArrayList<Call>();

    private List<Go> gos = new ArrayList<Go>();

    private static Elevator INSTANCE;

    private Elevator() {
    }

    public static Elevator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Elevator();
        }
        return INSTANCE;
    }

    public void userHasEntered() {
        userCount++;
    }

    public void userHasExited() {
        userCount--;
    }

    public int getUserCount() {
       return userCount;
    }

    public void addCall(Call call) {
        calls.add(call);
    }

    public List<Call> getCalls() {
        return calls;
    }

    public void addGo(Go go) {
        gos.add(go);
    }

    public List<Go> getGos() {
        return gos;
    }

    public int getFloor() {
        return floor;
    }

    public FloorHistory getFloorHistory() {
        return floorHistory;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public boolean isOpen() {
        return open;
    }

    public void moveToUp() {
        if (floor < HIGHER_FLOOR) {
            floor++;
            lastDirection = Direction.UP;
        } else {
            throw new IllegalStateException("the elevator is already at the highest floor");
        }
    }

    public void goTo(int floorToGo) {
        if (floorToGo >= LOWER_FLOOR && floorToGo <= HIGHER_FLOOR) {
            floor = floorToGo;
        } else {
            throw new IllegalStateException("the elevator is going outside");
        }
    }

    public void moveToDown() {
        if (floor > LOWER_FLOOR) {
            floor--;
            lastDirection = Direction.DOWN;
        } else {
            throw new IllegalStateException("the elevator is already at the lowest floor");
        }
    }

    public void open() {
        if (!open) {
            open = true;
            floorHistory.clear();
            removeDoneRequests();
        } else {
            throw new IllegalStateException("the door is already open");
        }
    }

    public void close() {
        if (open) {
            open = false;
            crashTest.reset();
        } else {
            throw new IllegalStateException("the door is already closed");
        }
    }

    public void reset(String cause) {
        floor = LOWER_FLOOR;
        open = false;
        calls = new ArrayList<Call>();
        gos = new ArrayList<Go>();
        userCount = 0;
        crashTest.reset();
        lastDirection = null;
        floorHistory.clear();
        Logger.info("Elevator reset, cause : " + cause);
    }

    private void removeDoneRequests() {
        for (Iterator<Call> itc = calls.iterator(); itc.hasNext(); ) {
            Call call = itc.next();
            if (call.getFloor() == floor) {
                itc.remove();
            }
        }
        for (Iterator<Go> itg = gos.iterator(); itg.hasNext(); ) {
            Go go = itg.next();
            if (go.getFloor() == floor) {
                itg.remove();
            }
        }
    }

    public Command getNextCommand() {
        Command nextCommand = engine.computeNextCommand();
        execute(nextCommand);
        return crashTest.isCrashed() ? Command.RESET : nextCommand;
    }

    private void execute(Command command) {

        crashTest.addAction();
        floorHistory.add(floor);

        switch (command) {
            case UP:
                moveToUp();
                break;
            case DOWN:
                moveToDown();
                break;
            case OPEN:
                open();
                break;
            case CLOSE:
                close();
                break;
            case NOTHING:
                break;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Floor : ").append(floor).append(" | ")
                .append("Opened : ").append(open).append(" | ")
                .append("User count : ").append(userCount).append("[");
        int count = 0;
        for (Call call : calls) {
            sb.append("Call from ").append(call.getFloor()).append(" to ").append(call.getDirection());
            count++;
            if (count < calls.size()) {
                sb.append(",");
            }
        }
        if (calls.size() > 0) {
            sb.append(" | ");
        }
        count = 0;
        for (Go go : gos) {
            sb.append("Go to ").append(go.getFloor());
            count++;
            if (count < gos.size()) {
                sb.append(",");
            }
        }
        sb.append("] ");
        sb.append(floorHistory.toString());

        return sb.toString();
    }
}
