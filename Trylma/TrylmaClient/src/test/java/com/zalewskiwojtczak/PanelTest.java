package com.zalewskiwojtczak;

import org.junit.Test;

import java.awt.*;

import static junit.framework.TestCase.assertEquals;

public class PanelTest {
    @Test
    public void testPanel(){
        TrylmaPanel panel = new TrylmaPanel();
        assertEquals(panel.getCircles()[9][6].getColor(), Color.LIGHT_GRAY);
        panel.mark(9, 6);
        assertEquals(panel.getCircles()[9][6].getColor(), Color.MAGENTA);
        panel.unmark();
        assertEquals(panel.getCircles()[9][6].getColor(), Color.LIGHT_GRAY);

        assertEquals(panel.getCircles()[13][4].getColor(), Color.GREEN);
        panel.mark(13, 4, true);
        assertEquals(panel.getCircles()[13][4].getColor(), Color.CYAN);
        panel.unmark();
        panel.makeMove(12, 4);
        assertEquals(panel.getCircles()[13][4].getColor(), Color.LIGHT_GRAY);
        assertEquals(panel.getCircles()[12][4].getColor(), Color.GREEN);
    }
}
