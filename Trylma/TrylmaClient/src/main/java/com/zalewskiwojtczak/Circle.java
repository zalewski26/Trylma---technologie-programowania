package com.zalewskiwojtczak;

import java.awt.*;

public class Circle {
    private final int id;
    private final int x;
    private final int y;
    private final int diameter;
    private Color color;

    public Circle(int x, int y, int diameter, int id){
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.id = id;
        this.color = getColor(id);
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setColor(int id){
        this.color = getColor(id);
    }

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

    public Color getColor(){
        return color;
    }

    public int getId(){
        return id;
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, diameter, diameter);
    }

    public void setBright(){
        color = color.brighter().brighter();
    }

    public boolean contains(int pointX, int pointY)
    {
        int radius = diameter/2;            // ||x - y|| = sqrt[(x1 - y1)^2 + (x2 - y2)^2 + ...]
        int distance = (int) Math.sqrt((Math.pow(pointX - (x + radius), 2)) + (Math.pow(pointY - (y + radius), 2)));

        return distance <= radius;
    }
}
