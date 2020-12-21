package com.zalewskiwojtczak;

import java.awt.*;
/** Klasa reprezentująca pojedynczny pionek na planszy (nazwa pochodzi od kształtu pionka)
 */
public class Circle extends AbstractShape {

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

    /** Funkcja rysująca dany okrągły pionek na ekranie
     * @param g grafika do narysowania
     */
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, diameter, diameter);
    }

    /** Funkcja sprawdzająca czy punkt znajduje się w okręgu
     * @param pointX współrzędna x sprawdzanego punktu
     * @param pointY współrzędna y sprawdzanego punktu
     */
    public boolean contains(int pointX, int pointY)
    {
        // ||x - y|| = sqrt[(x1 - y1)^2 + (x2 - y2)^2 + ...]
        int radius = diameter/2;
        int distance = (int) Math.sqrt((Math.pow(pointX - (x + radius), 2)) + (Math.pow(pointY - (y + radius), 2)));

        return distance <= radius;
    }
}