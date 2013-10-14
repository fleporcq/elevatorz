package elevator.models;

public class CrashTest {

    private int actionCountSinceLastClose;

    public void addAction() {
        actionCountSinceLastClose++;
    }

    public void reset() {
        actionCountSinceLastClose = 0;
    }

    public boolean isCrashed() {
        return actionCountSinceLastClose > 2 * Math.abs(Elevator.HIGHER_FLOOR - Elevator.LOWER_FLOOR);
    }

}
