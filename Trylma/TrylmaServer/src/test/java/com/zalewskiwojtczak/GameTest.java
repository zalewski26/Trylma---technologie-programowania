/*package com.zalewskiwojtczak;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class GameTest {
    @Test
    public void availableTest(){
        TrylmaGame game = new TrylmaGame();
        int row = 13;
        int column = 4;
        int[][] neighbours = game.getAvailable(row, column);

        int counter1 = 0;
        int counter2 = 0;
        int counter3 = 0;

        int expectedCounter1 = 1;
        int expectedCounter2 = 1;
        int expectedCounter3 = 4;

        int[] expected1 = {12, 4};
        int[] expected2 = {12, 5};
        int[] expected3 = {0, 0};

        for (int[] n: neighbours){
            if (n[0] == expected1[0] && n[1] == expected1[1])
                counter1++;
            else if (n[0] == expected2[0] && n[1] == expected2[1])
                counter2++;
            else if (n[0] == expected3[0] && n[1] == expected3[1])
                counter3++;
        }

        assertEquals(counter1, expectedCounter1);
        assertEquals(counter2, expectedCounter2);
        assertEquals(counter3, expectedCounter3);
    }
}*/
