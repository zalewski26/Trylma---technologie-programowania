package com.zalewskiwojtczak;

import java.awt.*;
/** Klasa reprezentująca kształt na planszy
 */
public interface Shape {
    public void setColor(Color color);
    public void setColor(int id);
    public Color getColor(int id);
    public Color getColor();
    public int getId();
    public void draw(Graphics g);
    public void setBright();
    public boolean contains(int pointX, int pointY);
}
