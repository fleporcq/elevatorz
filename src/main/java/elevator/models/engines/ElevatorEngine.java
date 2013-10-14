package elevator.models.engines;

import elevator.models.Command;

public interface ElevatorEngine {
    public Command computeNextCommand();
}
