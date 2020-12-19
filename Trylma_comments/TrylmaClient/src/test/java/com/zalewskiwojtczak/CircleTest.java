package com.zalewskiwojtczak;

import org.junit.Test;

import java.awt.*;

import static junit.framework.TestCase.*;

public class CircleTest {
    @Test
    public void testCircle(){
        Circle circle = new Circle(0, 0, 2, 1);

        assertEquals(circle.getId(), 1);
        assertTrue(circle.contains(1, 1));
        assertFalse(circle.contains(3, 3));
        assertEquals(circle.getColor(), Color.GREEN.darker());
        circle.setColor(Color.MAGENTA);
        assertEquals(circle.getColor(), Color.MAGENTA);
    }
}
