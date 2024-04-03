package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Event class
 */
public class EventTest {
    private Event event;
    private Date testdate;
    private Event anotherEvent;
    private Event sameEvent;

    @BeforeEach
    public void runBefore() {
        testdate = Calendar.getInstance().getTime();
        event =  new Event("Test Event");
        anotherEvent = new Event("Different Event");
        sameEvent =  new Event("Test Event");
    }

    @Test
    public void testEquals_Reflexive() {
        assertTrue(event.equals(event));
    }

    @Test
    public void testEquals_Symmetric() {
        assertTrue(event.equals(sameEvent) && sameEvent.equals(event));
    }

    @Test
    public void testEquals_Null() {
        assertFalse(event.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        Object otherObject = new Object();
        assertFalse(event.equals(otherObject));
    }

    @Test
    public void testEquals_DifferentDescription() {
        assertFalse(event.equals(anotherEvent));
    }

    @Test
    public void testHashCode_ConsistentWithEquals() {
        assertTrue(event.equals(sameEvent));
        assertEquals(event.hashCode(), sameEvent.hashCode());
    }

    @Test
    public void testHashCode_DifferentForDifferentEvents() {
        assertFalse(event.hashCode() == anotherEvent.hashCode());
    }



    @Test
    public void testEvent() {
        assertEquals("Test Event", event.getDescription());
        assertTrue(isSameTime(event.getDate(), testdate));
    }

    private boolean isSameTime(Date date1, Date date2) {
        long tolerance = 100;
        return Math.abs(date1.getTime() - date2.getTime()) < tolerance;
    }

    @Test
    public void testToString() {
        assertEquals(testdate.toString() + "\n" + "Test Event", event.toString());
    }
}
