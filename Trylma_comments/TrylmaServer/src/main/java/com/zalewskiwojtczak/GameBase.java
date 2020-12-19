package com.zalewskiwojtczak;

import static java.lang.Math.abs;
/** Klasa odpowiedzialna za implementacje mechaniki gry, a konkretniej ruchów */
public class GameBase {
    public final int[][] board = { {8,8,8,8,8,8,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,6,8,8,8,8,8},
            {8,8,8,8,6,6,6,6,8,8,8,8,8}, {5,5,5,5,0,0,0,0,0,4,4,4,4}, {5,5,5,0,0,0,0,0,0,4,4,4,8}, {8,5,5,0,0,0,0,0,0,0,4,4,8},
            {8,5,0,0,0,0,0,0,0,0,4,8,8}, {8,8,0,0,0,0,0,0,0,0,0,8,8}, {8,3,0,0,0,0,0,0,0,0,2,8,8}, {8,3,3,0,0,0,0,0,0,0,2,2,8},
            {3,3,3,0,0,0,0,0,0,2,2,2,8}, {3,3,3,3,0,0,0,0,0,2,2,2,2}, {8,8,8,8,1,1,1,1,8,8,8,8,8}, {8,8,8,8,8,1,1,1,8,8,8,8,8},
            {8,8,8,8,8,1,1,8,8,8,8,8,8}, {8,8,8,8,8,8,1,8,8,8,8,8,8},
    };
    /** Zmienna przechowująca informacje na temat aktualnego gracza */
    protected TrylmaGame.playerHandler current;
    /** tablica dwuwymiarowa przechowująca pola na które dany pion może wykonać ruch*/
    protected int[][] available;
    /** Funkcja odpowiedzialna za wykonanie ruchu
     * @param row rząd, na którym początkowo znajdował się pion, którego chcemy przesunąć
     * @param column kolumna, na której początkowo znajdował się pion, którego chcemy przesunąć
     * @param player gracz, który przesuwa danego piona
     */
    public synchronized void action(int row, int column, TrylmaGame.playerHandler player){
    	// Zabraniamy graczowi wykonwania ruchu, gdy nie jest jego kolej...
        if (player != current){
            throw new IllegalStateException("Not your turn!");
        }
        // ... lub gdy brakuje przeciwników
        else if (player.opponent == null){
            throw new IllegalStateException("You have no opponent yet!");
        }
        //Oznaczenie pionka
        if (!current.marked){
        	// Zabraniamy graczowi wykonywania ruchu nie swoimi pionami
            if (board[row][column] != current.id)
                throw new IllegalStateException("This is not your pawn!");
            current.prev[0] = row;
            current.prev[1] = column;
            available = getAvailable(current.prev[0], current.prev[1]);
        }
        else{
        	//Zabraniamy poruszania się na pola już zajęte przez innych graczy
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
                        current = current.opponent;
                    }
                    current = current.opponent;
                    return;
                }
            }
            throw new IllegalStateException("Place is not available for move!");
        }
    }
    /** Funkcja zwracająca tablicę zawierająca możliwe ruchy dla podanego piona
     * @param row wiersz pionka, dla którego należy zwrócić możliwe ruchy
     * @param column kolumna pionka, dla którego należy zwrócić możliwe ruchy
     * @return result tablica dwuwymiarowa zawierająca współrzędne możliwych pól
     */
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
            if (a[0] < 0 || a[0] > 16 || a[1] < 0 || a[1] > 13){
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
}
