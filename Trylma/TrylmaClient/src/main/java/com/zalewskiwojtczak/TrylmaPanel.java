package com.zalewskiwojtczak;

import javax.swing.*;
import java.awt.*;

public class TrylmaPanel extends JPanel {
    private int id;
    private final Circle[][] circles = new Circle[17][13];
    private Circle currentCircle;
    private Color currentColor;
    private final int[][] board = { {8,8,8,8,8,8,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,6,8,8,8,8,8},
            {8,8,8,8,6,6,6,6,8,8,8,8,8}, {5,5,5,5,0,0,0,0,0,4,4,4,4}, {5,5,5,0,0,0,0,0,0,4,4,4,8}, {8,5,5,0,0,0,0,0,0,0,4,4,8},
            {8,5,0,0,0,0,0,0,0,0,4,8,8}, {8,8,0,0,0,0,0,0,0,0,0,8,8}, {8,3,0,0,0,0,0,0,0,0,2,8,8}, {8,3,3,0,0,0,0,0,0,0,2,2,8},
            {3,3,3,0,0,0,0,0,0,2,2,2,8}, {3,3,3,3,0,0,0,0,0,2,2,2,2}, {8,8,8,8,1,1,1,1,8,8,8,8,8}, {8,8,8,8,8,1,1,1,8,8,8,8,8},
            {8,8,8,8,8,1,1,8,8,8,8,8,8}, {8,8,8,8,8,8,1,8,8,8,8,8,8},
    };

    public TrylmaPanel(){
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());

        int diameter = 50;
        for (int row = 0; row < circles.length; row++){
            for (int column = 0; column < circles[row].length; column++){
                if (row % 2 == 0)
                    circles[row][column] = new Circle(column* diameter, row* diameter, diameter, board[row][column]);
                else
                    circles[row][column] = new Circle(column* diameter + diameter /2, row* diameter, diameter, board[row][column]);
            }
        }
        repaint();
    }

    public void setId(int id){
        this.id = id;
        for (Circle[] c: circles){
            for (Circle c1: c){
                if (c1.getId() == id)
                    c1.setBright();
            }
        }
    }

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

    public void mark(int row, int column, boolean tick){
        currentCircle = circles[row][column];
        currentColor = currentCircle.getColor();
        if (tick){
            currentCircle.setColor(Color.CYAN);
        }
    }

    public void mark(int row, int column){
        circles[row][column].setColor(Color.MAGENTA);
    }

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

    public void makeMove(int row, int column){
        currentCircle.setColor(Color.LIGHT_GRAY.darker());
        circles[row][column].setColor(currentColor);
    }

    public Circle[][] getCircles(){
        return circles;
    }

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