package com.zalewskiwojtczak;

import javax.swing.*;
import java.awt.*;
/** Klasa odpowiedzialna za przedstawianie planszy */
public class TrylmaPanel extends JPanel {
    /** Zmienna przechowująca identyfikator gracza, do którego należy dana plansza */
    private int id;
    /** Tablica dwuwymiarowa przechowująca ułożenie planszy */
    private final Circle[][] circles = new Circle[17][13];
    /** Zmienna przechowująca pionek, na którym w danej chwili są wykonywane operacje */
    private Circle currentCircle;
    /** Zmienna przechowująća kolor pionka, na którym w danej chwilli są wykonywane operacje */
    private Color currentColor;
    /** tablica dwuwymiarowa intów, przedstawiająca stan początkowy planszy */
    private final int[][] board = { {8,8,8,8,8,8,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,6,8,8,8,8,8},
            {8,8,8,8,6,6,6,6,8,8,8,8,8}, {5,5,5,5,0,0,0,0,0,4,4,4,4}, {5,5,5,0,0,0,0,0,0,4,4,4,8}, {8,5,5,0,0,0,0,0,0,0,4,4,8},
            {8,5,0,0,0,0,0,0,0,0,4,8,8}, {8,8,0,0,0,0,0,0,0,0,0,8,8}, {8,3,0,0,0,0,0,0,0,0,2,8,8}, {8,3,3,0,0,0,0,0,0,0,2,2,8},
            {3,3,3,0,0,0,0,0,0,2,2,2,8}, {3,3,3,3,0,0,0,0,0,2,2,2,2}, {8,8,8,8,1,1,1,1,8,8,8,8,8}, {8,8,8,8,8,1,1,1,8,8,8,8,8},
            {8,8,8,8,8,1,1,8,8,8,8,8,8}, {8,8,8,8,8,8,1,8,8,8,8,8,8},
    };
    /** Konstruktor klasy TrylmaPanel */
    public TrylmaPanel(){
        //Początkowe ustawienie parametrów okna
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());

        int diameter = 50;
        /*pętle wypełniająca tablice circles nowymi pionkami na podstawie informacji z zmiennej board */
        for (int row = 0; row < circles.length; row++){
            for (int column = 0; column < circles[row].length; column++){
                //w zależności od aktualnego wiersza, dla wierszy nieparzystych dokonujemy lekkiego wcięcia
                // w celu lepszego graficznego przedstawienia planszy
                if (row % 2 == 0)
                    circles[row][column] = new Circle(column* diameter, row* diameter, diameter, board[row][column]);
                else
                    circles[row][column] = new Circle(column* diameter + diameter /2, row* diameter, diameter, board[row][column]);
            }
        }
        repaint();
    }
    /** Funkcja oznaczająca piony danego gracza poprzez zwiększenie jasności koloru
     /dodano ze względów estetycznych, dla dodatkowego odróżnienia pionów gracza od przeciwnika */
    public void setId(int id){
        this.id = id;
        for (Circle[] c: circles){
            for (Circle c1: c){
                if (c1.getId() == id)
                    c1.setBright();
            }
        }
    }
    /** Funkcja majaca za zadanie przyciemnienie pól graczy, którzy nie biorą
     * udziału w aktualnej rozgrywce
     * @param number liczba graczy
     */
    public void setPlayers(int number){
        if (number == 6)
            return;
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                switch (number){
                    case 2:
                        if (board[row][column] == 2 || board[row][column] == 3 || board[row][column] == 4
                                || board[row][column] == 5)
                            circles[row][column].setColor(Color.LIGHT_GRAY.darker());
                        break;
                    case 3:
                        if (board[row][column] == 1 || board[row][column] == 4 || board[row][column] == 5)
                            circles[row][column].setColor(Color.LIGHT_GRAY.darker());
                        break;
                    case 4:
                        if (board[row][column] == 1 || board[row][column] == 6)
                            circles[row][column].setColor(Color.LIGHT_GRAY.darker());
                        break;
                }
            }
        }
    }
    /** Zmiana koloru pionka klikniętego przez gracza, w celu graficznego podkreslenia
     * którym pionkiem aktualnie wykonuje ruch
     * @param row wiersz, w ktorym znajduje sie dany pionek
     * @param column kolumna, w ktorej znajduje sie dany pionek
     * @param tick zmienna boolowska tworzaca mechanizm podswetlania pionka tylko dla gracza
     * ktory aktualnie wykonuje ruch
     */
    public void mark(int row, int column, boolean tick){
        currentCircle = circles[row][column];
        currentColor = currentCircle.getColor();
        if (tick){
            currentCircle.setColor(Color.CYAN);
        }
    }
    /** Oznaczenie pól na które może skoczyć oznaczony wcześniej pionek
     * @param row wiersz, w którym znajduje się dany pionek
     * @param column kolumna, w której znajduje się dany pionek
     */
    public void mark(int row, int column){
        circles[row][column].setColor(Color.MAGENTA);
    }
    /** Jeśli gracz zmienił decyzje i kliknął inny pionek ta funkcja
     usuwa wszystkie wcześniej stworzone oznaczenia */
    public void unmark(){
        for (Circle[] c: circles){
            for (Circle c1: c){
                if (c1.getColor() != null && c1.getColor().equals(Color.MAGENTA))
                    c1.setColor(Color.LIGHT_GRAY.darker());
                else if (c1.getColor() != null && c1.getColor().equals(Color.CYAN)){
                    c1.setColor(id);
                    c1.setBright();
                }
            }
        }
    }
    /** Funkcja odpowiedzialna za graficzne przedstawienie ruchu na planszy */
    public void makeMove(int row, int column){
        currentCircle.setColor(Color.LIGHT_GRAY.darker());
        circles[row][column].setColor(currentColor);
    }
    /** Funkcja zwracająca tablicę pionków - rozłożenie ich na planszy */
    public Circle[][] getCircles(){
        return circles;
    }
    /** Funkcja rysująca planszę
     * @param g obiekt typu Graphics, aktualny pionek do narysowania
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < circles.length; row++){
            for (int column = 0; column < circles[row].length; column++){
                if (board[row][column] != 8){
                    circles[row][column].draw(g);
                }

            }
        }
    }
}