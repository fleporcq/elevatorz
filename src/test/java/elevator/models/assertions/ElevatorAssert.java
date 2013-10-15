package elevator.models.assertions;

import elevator.models.Direction;
import elevator.models.Elevator;
import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;



public class ElevatorAssert extends GenericAssert<ElevatorAssert, Elevator> {

    protected ElevatorAssert(Elevator actual) {
        super(ElevatorAssert.class, actual);
    }
    public static ElevatorAssert assertThat(Elevator actual) {
        return new ElevatorAssert(actual);
    }
    public ElevatorAssert call(Integer atFloor, Direction to) {
        actual.call(atFloor, to);
        return this;
    }

    public ElevatorAssert go(Integer floorToGo) {
        actual.go(floorToGo);
        return this;
    }

    public ElevatorAssert tick() {
        actual.nextCommand();
        return this;
    }

    public ElevatorAssert reset(String cause) {
        actual.reset(cause);
        return this;
    }

    public ElevatorAssert open() {
        actual.open();
        return this;
    }

    public ElevatorAssert moveToUp() {
        actual.moveToUp();
        return this;
    }

    public ElevatorAssert moveToDown() {
        actual.moveToDown();
        return this;
    }

    public ElevatorAssert close() {
        actual.close();
        return this;
    }

    public ElevatorAssert isAtFloor(int floor) {
        Assertions.assertThat(actual.getFloor()).isEqualTo(floor);
        return this;
    }

    public ElevatorAssert isOpen() {
        Assertions.assertThat(actual.isOpen()).isTrue();
        return this;
    }

    public ElevatorAssert isClosed() {
        Assertions.assertThat(actual.isOpen()).isFalse();
        return this;
    }

    public ElevatorAssert isReset() {
        Assertions.assertThat(actual.isOpen()).isFalse();
        Assertions.assertThat(actual.getFloor()).isEqualTo(Elevator.LOWER_FLOOR);
        Assertions.assertThat(actual.getUserCount()).isEqualTo(0);
        Assertions.assertThat(actual.getLastDirection()).isEqualTo(null);
        Assertions.assertThat(actual.getFloorHistory().size()).isEqualTo(0);
        Assertions.assertThat(actual.getCalls().size()).isEqualTo(0);
        Assertions.assertThat(actual.getGos().size()).isEqualTo(0);
        return this;
    }

    public ElevatorAssert fail() {
        fail();
        return this;
    }

}
