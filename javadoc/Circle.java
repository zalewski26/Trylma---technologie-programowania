package com.zalewskiwojtczak;

import java.awt.*;
/** Klasa reprezentująca pojedynczny pionek na planszy (nazwa pochodzi od kształtu pionka)
 */
public class Circle {
	/** Identyfikator określający którego gracza jest dany pionek */
    private final int id;
    /** Współrzędne x pionka */
    private final int x;
    /** Współrzędne y pionka */
    private final int y;
    /** Średnia pionka */
    private final int diameter;
    /** Color pionka, który jest graficznym przedstawieniem id */
    private Color color;
    /** Konstruktor klasy Circle
     * @param x współrzędna x pionka
     * @param y współrzędna y pionka
     * @param diameter średnia pionka
     * @param id identyfikator określający do którego gracza należy pionek
     */
    public Circle(int x, int y, int diameter, int id){
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.id = id;
        this.color = getColor(id);
    }
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
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, diameter, diameter);
    }
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
    public boolean contains(int pointX, int pointY)
    {
        int radius = diameter/2;
        // ||x - y|| = sqrt[(x1 - y1)^2 + (x2 - y2)^2 + ...]
        int distance = (int) Math.sqrt((Math.pow(pointX - (x + radius), 2)) + (Math.pow(pointY - (y + radius), 2)));

        return distance <= radius;
    }
}
