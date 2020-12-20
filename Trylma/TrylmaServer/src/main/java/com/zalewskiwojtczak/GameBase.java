package com.zalewskiwojtczak;

import java.util.Random;

import static java.lang.Math.abs;

public class GameBase {
    public final int[][] board = { {8,8,8,8,8,8,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,6,8,8,8,8,8},
            {8,8,8,8,6,6,6,6,8,8,8,8,8}, {5,5,5,5,0,0,0,0,0,4,4,4,4}, {5,5,5,0,0,0,0,0,0,4,4,4,8}, {8,5,5,0,0,0,0,0,0,0,4,4,8},
            {8,5,0,0,0,0,0,0,0,0,4,8,8}, {8,8,0,0,0,0,0,0,0,0,0,8,8}, {8,3,0,0,0,0,0,0,0,0,2,8,8}, {8,3,3,0,0,0,0,0,0,0,2,2,8},
            {3,3,3,0,0,0,0,0,0,2,2,2,8}, {3,3,3,3,0,0,0,0,0,2,2,2,2}, {8,8,8,8,1,1,1,1,8,8,8,8,8}, {8,8,8,8,8,1,1,1,8,8,8,8,8},
            {8,8,8,8,8,1,1,8,8,8,8,8,8}, {8,8,8,8,8,8,1,8,8,8,8,8,8},
    };
    protected TrylmaGame.playerHandler current;
    protected int[][] available;
    protected TrylmaGame.playerHandler[] players = new TrylmaGame.playerHandler[NUMOF];
    protected static final int NUMOF = 3;
    private final Random rand = new Random();
    protected final int first = rand.nextInt(3) + 1;

    public synchronized void action(int row, int column, TrylmaGame.playerHandler player){
        if (player != current){
            throw new IllegalStateException("Not your turn!");
        }
        for (TrylmaGame.playerHandler opponent: players){
            if (opponent == null)
                throw new IllegalStateException("You have no opponent yet!");
        }

        if (!current.marked){
            if (board[row][column] != current.id)
                throw new IllegalStateException("This is not your pawn!");
            current.prev[0] = row;
            current.prev[1] = column;
            available = getAvailable(current.prev[0], current.prev[1]);
        }
        else{
            if (board[row][column] != 0) {
                throw new IllegalStateException("Place is already occupied!");
            }

            for (int[] av: available){
                if (av[0] == row && av[1] == column) {
                    board[current.prev[0]][current.prev[1]] = 0;
                    board[row][column] = current.id;
                    int diffrow = abs(current.prev[0] - row);
                    int diffcol = abs(current.prev[1] - column);
                    if (diffrow == 2 || diffcol == 2){
                        current.prev[0] = row;
                        current.prev[1] = column;
                        current.jump = true;
                    }
                    if (!current.jump)
                        current = players[current.id % NUMOF];
                        //current = current.opponent;
                    return;
                }
            }
            throw new IllegalStateException("Place is not available for move!");
        }
    }

    public synchronized int[][] getAvailable(int row, int column){
        int[][] result = { {row - 1, column}, {row - 1, column + 1}, {row, column - 1}, {row, column + 1},
                {row + 1, column}, {row + 1, column + 1}};
        if (row % 2 == 0) {
            result[0][1] = column - 1;
            result[1][1] = column;
            result[4][1] = column - 1;
            result[5][1] = column;
        }
        for (int i = 0; i < result.length; i++){
            if (result[i][0] < 0 || result[i][0] > 16 || result[i][1] < 0 || result[i][1] > 12)
                continue;
            if (board[result[i][0]][result[i][1]] != 0){
                int diffrow = result[i][0] - row;
                int diffcol = result[i][1] - column;
                if (diffrow == 0 && diffcol == -1)
                    result[i][1] -= 1;
                else if (diffrow == 0 && diffcol == 1)
                    result[i][1] += 1;
                else if (diffrow == -1 && diffcol == -1)
                    result[i][0] -= 1;
                else if (diffrow == 1 && diffcol == -1)
                    result[i][0] += 1;
                else if (diffrow == -1 && diffcol == 1)
                    result[i][0] -= 1;
                else if (diffrow == 1 && diffcol == 1)
                    result[i][0] += 1;
                else if (diffrow == -1 && diffcol == 0){
                    if (row % 2 == 1){
                        result[i][0] -= 1;
                        result[i][1] -= 1;
                    }
                    else{
                        result[i][0] -= 1;
                        result[i][1] += 1;
                    }
                }
                else if (diffrow == 1 && diffcol == 0){
                    if (row % 2 == 1){
                        result[i][0] += 1;
                        result[i][1] -= 1;
                    }
                    else{
                        result[i][0] += 1;
                        result[i][1] += 1;
                    }
                }
            }
        }
        for (int[] a: result){
            if (a[0] < 0 || a[0] > 16 || a[1] < 0 || a[1] > 12){
                a[0] = 0;
                a[1] = 0;
            }
            else if (board[a[0]][a[1]] != 0){
                a[0] = 0;
                a[1] = 0;
            }
        }
        return result;
    }

    public boolean checkWin(int id){
        switch (id){
            /*case 1:
                if (board[0][6] == 1 && board[1][5] == 1 && board[1][6] == 1
                        && board[2][5] == 1 && board[2][6] == 1 && board[2][7] == 1
                        && board[3][4] == 1 && board[3][5] == 1 && board[3][6] == 1 && board[3][7] == 1)
                    return true;
            case 6:
                if (board[16][6] == 6 && board[15][5] == 6 && board[15][6] == 6
                        && board[14][5] == 6 && board[14][6] == 6 && board[14][7] == 6
                        && board[13][4] == 6 && board[13][5] == 6 && board[13][6] == 6 && board[13][7] == 6)
                    return true;
            */
            case 1:
                if (board[12][4] == 1 && board[12][5] == 1)
                    return true;
        }
        return false;
    }
}
