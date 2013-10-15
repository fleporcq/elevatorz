package elevator.models;

import org.junit.Test;

import static elevator.models.assertions.ElevatorAssert.assertThat;


public class ElevatorTest {

    private Elevator elevator = Elevator.getInstance();

    @Test
    public void should_be_clean_after_reset() {
        assertThat(elevator).reset("Elevator should_be_clean_after_reset Test").isReset();
    }

    @Test
    public void should_be_open() {
        assertThat(elevator).reset("Elevator should_be_open Test")
                .open()
                .isOpen();
    }

    @Test
    public void should_be_closed() {
        assertThat(elevator).reset("Elevator should_be_closed Test")
                .open()
                .close()
                .isClosed();
    }

    @Test
    public void should_throw_already_closed() {
        try {
            assertThat(elevator).reset("Elevator should_throw_already_closed Test")
                    .close()
                    .fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void should_throw_already_open() {
        try {
            assertThat(elevator).reset("Elevator should_throw_already_open Test")
                    .open()
                    .open()
                    .fail();
        } catch (IllegalStateException e) {

        }
    }

    @Test
    public void should_throw_already_at_the_lowest_floor() {
        try {
            assertThat(elevator).reset("Elevator should_throw_already_at_the_lowest_floor Test")
                    .moveToDown()
                    .fail();
        } catch (IllegalStateException e) {

        }
    }

    @Test
    public void should_throw_already_at_the_highest_floor() {
        try {
            assertThat(elevator).reset("Elevator should_throw_already_at_the_highest_floor Test")
                    .moveToUp()
                    .moveToUp()
                    .moveToUp()
                    .moveToUp()
                    .moveToUp()
                    .moveToUp()
                    .fail();
        } catch (IllegalStateException e) {

        }
    }

    @Test
    public void should_do_things() {
        assertThat(elevator).reset("Elevator should_do_things Test")
                .call(4, Direction.DOWN)
                .tick().isAtFloor(1).isClosed()
                .tick().isAtFloor(2).isClosed()
                .tick().isAtFloor(3).isClosed()
                .tick().isAtFloor(4).isClosed()
                .tick().isOpen()
                .tick().isClosed()
                .go(2)
                .tick().isAtFloor(3).isClosed()
                .tick().isAtFloor(2).isClosed()
                .tick().isOpen()
                .tick().isClosed()
                .tick().isAtFloor(1).isClosed();
    }


}
