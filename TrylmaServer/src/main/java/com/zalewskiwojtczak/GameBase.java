package com.zalewskiwojtczak;

import java.util.Random;

import static java.lang.Math.abs;
/** Klasa odpowiedzialna za implementacje mechaniki gry, a konkretniej ruchów */
public class GameBase {
	/** dwuwymiarowa tablica przechowujaca informacje o ułożeniu planszy */
    public final int[][] board = {{8, 8, 8, 8, 8, 8, 6, 8, 8, 8, 8, 8, 8}, {8, 8, 8, 8, 8, 6, 6, 8, 8, 8, 8, 8, 8}, {8, 8, 8, 8, 8, 6, 6, 6, 8, 8, 8, 8, 8},
            {8, 8, 8, 8, 6, 6, 6, 6, 8, 8, 8, 8, 8}, {5, 5, 5, 5, 0, 0, 0, 0, 0, 4, 4, 4, 4}, {5, 5, 5, 0, 0, 0, 0, 0, 0, 4, 4, 4, 8}, {8, 5, 5, 0, 0, 0, 0, 0, 0, 0, 4, 4, 8},
            {8, 5, 0, 0, 0, 0, 0, 0, 0, 0, 4, 8, 8}, {8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8}, {8, 3, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 8}, {8, 3, 3, 0, 0, 0, 0, 0, 0, 0, 2, 2, 8},
            {3, 3, 3, 0, 0, 0, 0, 0, 0, 2, 2, 2, 8}, {3, 3, 3, 3, 0, 0, 0, 0, 0, 2, 2, 2, 2}, {8, 8, 8, 8, 1, 1, 1, 1, 8, 8, 8, 8, 8}, {8, 8, 8, 8, 8, 1, 1, 1, 8, 8, 8, 8, 8},
            {8, 8, 8, 8, 8, 1, 1, 8, 8, 8, 8, 8, 8}, {8, 8, 8, 8, 8, 8, 1, 8, 8, 8, 8, 8, 8},
    };
    /** Zmienna przechowujaca poczatkowe wartosci poszczegolnych pol na planszy,
     * przydatna, gdy liczba graczy jest mniejsza niz 6
     */
    protected int[][] startingBoard;
    /** Zmienna przechowująca informacje na temat aktualnego gracza */
    protected TrylmaGame.playerHandler current;
    /** tablica dwuwymiarowa przechowująca pola na które dany pion może wykonać ruch*/
    protected int[][] available;
    
    protected TrylmaGame.playerHandler[] players;
    /** Zmienna reprezentujaca informacje o liczbie graczy*/
    protected int NUMOF;
    /** Zmienna przechowująca informacje o liczbe graczy */
    protected int playerCounter = 0;
    /** Zmienna przechowujaca informacje, który zawodnik rozpoczyna rozgrywkę */
    protected int first;

    /** Funkcja odpowiedzialna za wykonanie ruchu
     * @param row rząd, na którym początkowo znajdował się pion, którego chcemy przesunąć
     * @param column kolumna, na której początkowo znajdował się pion, którego chcemy przesunąć
     * @param player gracz, który przesuwa danego piona
     */
    public synchronized void action(int row, int column, TrylmaGame.playerHandler player) {
        if (player != current) {
        	// Zabraniamy graczowi wykonwania ruchu, gdy nie jest jego kolej...
            throw new IllegalStateException("Not your turn!");
        }
        for (TrylmaGame.playerHandler opponent : players) {
            if (opponent == null)
            	// ... lub gdy brakuje przeciwników
                throw new IllegalStateException("You have no opponent yet!");
        }
      //Oznaczenie pionka
        if (!current.marked) {
        	// Zabraniamy graczowi wykonywania ruchu nie swoimi pionami
            if (board[row][column] != current.id)
                throw new IllegalStateException("This is not your pawn!");
            current.prev[0] = row;
            current.prev[1] = column;
            available = getAvailable(current.prev[0], current.prev[1]);
        } else {
        	//Zabraniamy poruszania się na pola już zajęte przez innych graczy
            if (board[row][column] != 0) {
                throw new IllegalStateException("Place is already occupied!");
            }
            //Funkcja wykonująca ruch 
            for (int[] av : available) {
                if (av[0] == row && av[1] == column) {
                    board[current.prev[0]][current.prev[1]] = 0;
                    board[row][column] = current.id;
                    int diffrow = abs(current.prev[0] - row);
                    int diffcol = abs(current.prev[1] - column);
                    if (diffrow == 2 || diffcol == 2) {
                        current.prev[0] = row;
                        current.prev[1] = column;
                        current.jump = true;
                    }
                    if (!current.jump)
                        current = current.nextPlayer();
                    //current = current.opponent;
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
    public synchronized int[][] getAvailable(int row, int column) {
        int[][] result = {{row - 1, column}, {row - 1, column + 1}, {row, column - 1}, {row, column + 1},
                {row + 1, column}, {row + 1, column + 1}};
        if (row % 2 == 0) {
            result[0][1] = column - 1;
            result[1][1] = column;
            result[4][1] = column - 1;
            result[5][1] = column;
        }
        for (int i = 0; i < result.length; i++) {
            if (result[i][0] < 0 || result[i][0] > 16 || result[i][1] < 0 || result[i][1] > 12)
                continue;
            if (board[result[i][0]][result[i][1]] != 0) {
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
                else if (diffrow == -1 && diffcol == 0) {
                    if (row % 2 == 1) {
                        result[i][0] -= 1;
                        result[i][1] -= 1;
                    } else {
                        result[i][0] -= 1;
                        result[i][1] += 1;
                    }
                } else if (diffrow == 1 && diffcol == 0) {
                    if (row % 2 == 1) {
                        result[i][0] += 1;
                        result[i][1] -= 1;
                    } else {
                        result[i][0] += 1;
                        result[i][1] += 1;
                    }
                }
            }
        }
        for (int[] a : result) {
            if (a[0] < 0 || a[0] > 16 || a[1] < 0 || a[1] > 12) {
                a[0] = 0;
                a[1] = 0;
            } else if (board[a[0]][a[1]] != 0) {
                a[0] = 0;
                a[1] = 0;
            }
        }
        return result;
    }
    /** Funkcja sprawdzająca warunki zwycięstwa 
     * @param id id gracza dla którego sprawdzamy warunki zwycięstwa
     */
    public boolean checkWin(int id) {
    	//sprawdzanie odbywa się poprzez sprawdzenie wartości wszystkich pól
        boolean full = false;
        switch (id) {
            case 1:
                if (board[0][6] != 0 && board[1][5] != 0 && board[1][6] != 0
                        && board[2][5] != 0 && board[2][6] != 0 && board[2][7] != 0
                        && board[3][4] != 0 && board[3][5] != 0 && board[3][6] != 0 && board[3][7] != 0)
                    full = true;
                break;
            case 2:
                if (board[7][1] != 0 && board[6][1] != 0 && board[6][2] != 0
                        && board[5][0] != 0 && board[5][1] != 0 && board[5][2] != 0
                        && board[4][0] != 0 && board[4][1] != 0 && board[4][2] != 0 && board[4][3] != 0)
                    full = true;
                break;
            case 3:
                if (board[7][10] != 0 && board[6][10] != 0 && board[6][11] != 0
                        && board[5][9] != 0 && board[5][10] != 0 && board[5][11] != 0
                        && board[4][9] != 0 && board[4][10] != 0 && board[4][11] != 0 && board[4][12] != 0)
                    full = true;
                break;
            case 4:
                if (board[9][1] != 0 && board[10][1] != 0 && board[10][2] != 0
                        && board[11][0] != 0 && board[11][1] != 0 && board[11][2] != 0
                        && board[12][0] != 0 && board[12][1] != 0 && board[12][2] != 0 && board[12][3] != 0)
                    full = true;
                break;
            case 5:
                if (board[9][10] != 0 && board[10][10] != 0 && board[10][11] != 0
                        && board[11][9] != 0 && board[11][10] != 0 && board[11][11] != 0
                        && board[12][9] != 0 && board[12][10] != 0 && board[12][11] != 0 && board[12][12] != 0)
                    full = true;
                break;
            case 6:
                if (board[16][6] != 0 && board[15][5] != 0 && board[15][6] != 0
                        && board[14][5] != 0 && board[14][6] != 0 && board[14][7] != 0
                        && board[13][4] != 0 && board[13][5] != 0 && board[13][6] != 0 && board[13][7] != 0)
                    full = true;
                break;
        }
        for (int row = 0; row < board.length; row++){
            for (int column = 0; column < board[row].length; column++){
                if (startingBoard[row][column] == 0 && board[row][column] == id)
                    return false;
                if (startingBoard[row][column] == id && board[row][column] == id)
                    return false;
            }
        }
        if (full)
            return true;

        return false;
    }
}
