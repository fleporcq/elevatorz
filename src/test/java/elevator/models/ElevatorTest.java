package elevator.models;

import org.junit.Assert;
import org.junit.Test;

public class ElevatorTest {

    @Test
    public void should_be_open(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_be_open Test");
        elevator.open();
        Assert.assertTrue(elevator.isOpen());
    }

    @Test
    public void should_be_closed(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_be_closed Test");
        elevator.open();
        elevator.close();
        Assert.assertFalse(elevator.isOpen());
    }

    @Test
    public void should_be_clean_after_reset(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_be_clean_after_reset Test");
        Assert.assertEquals(elevator.getFloor(), Elevator.LOWER_FLOOR);
        Assert.assertFalse(elevator.isOpen());
        Assert.assertEquals(elevator.getUserCount(), 0);
        Assert.assertEquals(elevator.getLastDirection(), null);
        Assert.assertEquals(elevator.getFloorHistory().size(), 0);
        Assert.assertEquals(elevator.getCalls().size(), 0);
        Assert.assertEquals(elevator.getGos().size(), 0);
    }

    @Test
    public void should_throw_already_closed(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_throw_already_closed Test");
        try {
            elevator.close();
            Assert.fail();
        }catch (IllegalStateException e){

        }
    }

    @Test
    public void should_throw_already_open(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_throw_already_open Test");
        try {
            elevator.open();
            elevator.open();
            Assert.fail();
        }catch (IllegalStateException e){

        }
    }

    @Test
    public void should_be_closed_after_reset(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_be_closed_after_reset Test");
        Assert.assertFalse(elevator.isOpen());
    }

    @Test
    public void should_throw_already_at_the_lowest_floor(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_throw_already_at_the_lowest_floor Test");
        elevator.goTo(0);
        try {
            elevator.moveToDown();
            Assert.fail();
        }catch (IllegalStateException e){

        }
    }

    @Test
    public void should_throw_already_at_the_highest_floor(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_throw_already_at_the_highest_floor Test");
        elevator.goTo(5);
        try {
            elevator.moveToUp();
            Assert.fail();
        }catch (IllegalStateException e){

        }
    }

    @Test
    public void should_throw_is_going_outside(){
        Elevator elevator = Elevator.getInstance();
        elevator.reset("Elevator should_throw_is_going_outside Test");
        try {
            elevator.goTo(-1);
            Assert.fail();
        }catch (IllegalStateException e){

        }
        try {
            elevator.goTo(6);
            Assert.fail();
        }catch (IllegalStateException e){

        }
    }
}
