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

        switch (id){
            case 0:
                this.color = Color.LIGHT_GRAY;
                break;
            case 1:
                this.color = Color.GREEN;
                break;
            case 2:
                this.color = Color.YELLOW;
                break;
            case 3:
                this.color = Color.BLACK;
                break;
            case 4:
                this.color = Color.WHITE;
                break;
            case 5:
                this.color = Color.BLUE;
                break;
            case 6:
                this.color = Color.RED;
                break;
        }
    }

    public void setColor(Color color){
        this.color = color;
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

    public boolean contains(int pointX, int pointY)
    {
        int radius = diameter/2;            // ||x - y|| = sqrt[(x1 - y1)^2 + (x2 - y2)^2 + ...]
        int distance = (int) Math.sqrt((Math.pow(pointX - (x + radius), 2)) + (Math.pow(pointY - (y + radius), 2)));

        return distance <= radius;
    }
}
