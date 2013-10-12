package elevator.models;

import elevator.Logger;
import elevator.models.requests.Call;
import elevator.models.requests.Go;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Elevator {

    private final static Integer LOWER_FLOOR = 0;

    private final static Integer HIGHER_FLOOR = 5;

    private int floor = LOWER_FLOOR;

    private boolean opened = false;

    private int userCount;

    private int actionCountSinceLastClose;

    private Direction lastDirection;

    private FloorHistory floorHistory = new FloorHistory();

    private List<Call> calls = new ArrayList<Call>();

    private List<Go> gos = new ArrayList<Go>();

    private static Elevator INSTANCE;

    private Elevator(){}

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

    public int getFloor() {
        return floor;
    }

    public boolean isOpened() {
        return opened;
    }

    public List<Call> getCalls() {
        return calls;
    }

    public void addCall(Call call) {
        calls.add(call);
    }

    public List<Go> getGos() {
        return gos;
    }

    public void addGo(Go go) {
        gos.add(go);
    }

    public void reset(String cause) {
        floor = LOWER_FLOOR;
        opened = false;
        calls = new ArrayList<Call>();
        gos = new ArrayList<Go>();
        userCount = 0;
        actionCountSinceLastClose = 0;
        lastDirection = null;
        floorHistory.clear();
        Logger.info(cause);
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

    private int countUserWantExit(){
        int count = 0;
        for (Go go : gos) {
            if (go.getFloor() == floor) {
                count++;
            }
        }
        return count;
    }

    private int countUserWantEnterAndGoTo(Direction nextDirection){
        int count = 0;
        for (Call call : calls) {
            if (call.getFloor() == floor && (nextDirection == null || call.getDirection().equals(nextDirection))) {
                count++;
            }
        }
        return count;
    }

    private boolean stopToCurrentFloor(Direction nextDirection){
        return countUserWantExit() + countUserWantEnterAndGoTo(nextDirection) > 0;
    }

    public int computeDistanceTo(int toFloor){
        return Math.abs(floor - toFloor);
    }

    private int countGoTo(Direction direction) {
        int count = 0;
        for (Go go : gos) {
            if ((Direction.UP.equals(direction) && go.getFloor() > floor) ||
                    (Direction.DOWN.equals(direction) && go.getFloor() < floor)) {
                count += HIGHER_FLOOR-computeDistanceTo(go.getFloor());
            }
        }


        return count;
    }

    private int countCallFrom(Direction direction) {
        int count = 0;
        for (Call call : calls) {
            if ((Direction.UP.equals(direction) && call.getFloor() > floor) ||
                    (Direction.DOWN.equals(direction) && call.getFloor() < floor)) {
                count += HIGHER_FLOOR-computeDistanceTo(call.getFloor());
            }
        }

        return count;
    }

    private Direction getNextDirection(){
        Direction nextDirection = null;

        int scoreToUp = 2 * countGoTo(Direction.UP) + countCallFrom(Direction.UP);
        int scoreToDown = 2 * countGoTo(Direction.DOWN) + countCallFrom(Direction.DOWN);

        if(floorHistory.hasCycle()){
            Logger.info("Pre-cycle detected! " + floorHistory.toString());
            if(Direction.DOWN.equals(lastDirection) && floor > LOWER_FLOOR){
                nextDirection = Direction.DOWN;
            }
            if(Direction.UP.equals(lastDirection) && floor < HIGHER_FLOOR){
                nextDirection = Direction.UP;
            }
        }
        else if (scoreToUp > scoreToDown) {
            nextDirection = Direction.UP;
        }
        else if (scoreToUp < scoreToDown) {
            nextDirection = Direction.DOWN;
        }
        else if(floor >  LOWER_FLOOR){
            nextDirection = Direction.DOWN;
        }
        else if(floor ==  LOWER_FLOOR){
            nextDirection = Direction.UP;
        }

        return nextDirection;
    }


    public Command getNextCommand() {

        Command nextCommand = null;

        Direction nextDirection = getNextDirection();

        if (opened) {
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

        execute(nextCommand);

        //si le nombre d'actions depuis la derniere fermeture de la porte est supérieur à un aller-retour
        if(actionCountSinceLastClose > 2 *(HIGHER_FLOOR - LOWER_FLOOR) ){
            Logger.info(nextDirection != null ? nextDirection.name() : "NEXT DIRECTION IS NULL");
            nextCommand = Command.RESET;
        }

        return nextCommand;
    }

    private void execute(Command command) {

        if(Command.CLOSE.equals(command)){
            actionCountSinceLastClose = 0;
        }else{
            actionCountSinceLastClose++;
        }

        floorHistory.add(floor);

        switch (command) {
            case UP:
                floor++;
                lastDirection = Direction.UP;
                break;
            case DOWN:
                floor--;
                lastDirection = Direction.DOWN;
                break;
            case OPEN:
                opened = true;
                floorHistory.clear();
                removeDoneRequests();
                break;
            case CLOSE:
                opened = false;
                break;
            case NOTHING:
                break;

        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Floor : ").append(floor).append(" | ")
                .append("Opened : ").append(opened).append(" | ")
                .append("User count : ").append(userCount).append("[");
        int count = 0;
        for(Call call : calls){
            sb.append("Call from ").append(call.getFloor()).append(" to ").append(call.getDirection());
            count++;
            if(count < calls.size()){
                sb.append(",");
            }
        }
        if(calls.size() > 0){
            sb.append(" | ");
        }
        count = 0;
        for(Go go : gos){
            sb.append("Go to ").append(go.getFloor());
            count++;
            if(count < gos.size()){
                sb.append(",");
            }
        }
        sb.append("] ");
        sb.append(floorHistory.toString());
        return sb.toString();
    }
}
