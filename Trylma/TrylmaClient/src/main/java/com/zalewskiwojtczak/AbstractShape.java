package com.zalewskiwojtczak;

import java.awt.*;

public abstract class AbstractShape implements Shape {
    /** Identyfikator określający którego gracza jest dany pionek */
    protected int id;
    /** Współrzędne x pionka */
    protected int x;
    /** Współrzędne y pionka */
    protected int y;
    /** Średnia pionka */
    protected int diameter;
    /** Color pionka, który jest graficznym przedstawieniem id */
    protected Color color;

    /** Funkcja ustawiająca kolor
     * @param color kolor pionka
     */
    public void setColor(Color color){
        this.color = color;
    }
    /** Funkcja zmieniajaca kolor pionka względem podanego id
     * @param id numer koloru (kolory są utożsamiane z id)
     */
    public void setColor(int id){
        this.color = getColor(id);
    }
    /** Funkcja przyciemniająca kolor pionka
     * @param id identyfikator gracza
     * @return przyciemniony kolor
     */
    public Color getColor(int id){
        switch (id){
            case 0:
                return Color.LIGHT_GRAY.darker();
            case 1:
                return Color.GREEN.darker();
            case 2:
                return Color.YELLOW.darker();
            case 3:
                return Color.BLACK.darker();
            case 4:
                return Color.WHITE.darker();
            case 5:
                return Color.BLUE.darker();
            case 6:
                return Color.RED.darker();
        }
        return null;
    }
    /** Funkcja zwracająca kolor pionka
     * @return kolor pionka
     */
    public Color getColor(){
        return color;
    }
    /** Funkcja zwracająca identyfikator gracza, do którego należy pionek
     * @return identyfikator gracza
     */
    public int getId(){
        return id;
    }
    /** Funkcja rysująca dany pionek na ekranie
     * @param g grafika do narysowania
     */
    public abstract void draw(Graphics g);
    /** Funkcja rozjąsniająca kolor pionka, używana do
     * podkreślenia, który gracz aktualnie się porusza
     */
    public void setBright(){
        color = color.brighter().brighter();
    }
    /** Funkcja sprawdzająca czy punkt znajduje się w pionku
     * @param pointX współrzędna x sprawdzanego punktu
     * @param pointY współrzędna y sprawdzanego punktu
     */
    public abstract boolean contains(int pointX, int pointY);
}
