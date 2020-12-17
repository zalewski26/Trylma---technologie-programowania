import javax.swing.*;
import java.awt.*;

public class Drawing {

    public static void main(String[] args){
        JFrame frame = new JFrame("Trylma");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 900);
        frame.setVisible(true);
        frame.setResizable(false);
        Panel panel = new Panel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }

    static class Panel extends JPanel{
        private Circle[][] board = new Circle[13][17];
        int diameter = 50;
        private int[][] pattern = {
                {8,8,8,8,8,8,6,8,8,8,8,8,8},
                {8,8,8,8,8,6,6,8,8,8,8,8,8},
                {8,8,8,8,8,6,6,6,8,8,8,8,8},
                {8,8,8,8,6,6,6,6,8,8,8,8,8},
                {5,5,5,5,0,0,0,0,0,4,4,4,4},
                {5,5,5,0,0,0,0,0,0,4,4,4,8},
                {8,5,5,0,0,0,0,0,0,0,4,4,8},
                {8,5,0,0,0,0,0,0,0,0,4,8,8},
                {8,8,0,0,0,0,0,0,0,0,0,8,8},
                {8,3,0,0,0,0,0,0,0,0,2,8,8},
                {8,3,3,0,0,0,0,0,0,0,2,2,8},
                {3,3,3,0,0,0,0,0,0,2,2,2,8},
                {3,3,3,3,0,0,0,0,0,2,2,2,2},
                {8,8,8,8,1,1,1,1,8,8,8,8,8},
                {8,8,8,8,8,1,1,1,8,8,8,8,8},
                {8,8,8,8,8,1,1,8,8,8,8,8,8},
                {8,8,8,8,8,8,1,8,8,8,8,8,8},
        };

        public Panel(){
            setBackground(Color.LIGHT_GRAY);
            setLayout(new BorderLayout());

            for (int i = 0; i < board.length; i++){
                for (int j = 0; j < board[i].length; j++){
                    if (j % 2 == 0)
                        board[i][j] = new Circle(i*diameter, j*diameter, diameter, colorFromNumber(pattern[j][i]));
                    else
                        board[i][j] = new Circle(i*diameter+diameter/2, j*diameter, diameter, colorFromNumber(pattern[j][i]));
                }
            }
            repaint();
        }

        private Color colorFromNumber(int number){
            switch (number){
                case 0:
                    return Color.DARK_GRAY;
                case 1:
                    return Color.GREEN;
                case 2:
                    return Color.YELLOW;
                case 3:
                    return Color.BLACK;
                case 4:
                    return Color.WHITE;
                case 5:
                    return Color.BLUE;
                case 6:
                    return Color.RED;
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < board.length; i++){
                for (int j = 0; j < board[i].length; j++){
                    if (pattern[j][i] != 8)
                        board[i][j].draw(g);
                }
            }

        }
    }

    static class Circle{
        int x;
        int y;
        int diameter;
        Color color;

        public Circle(int x, int y, int diameter, Color color){
            this.x = x;
            this.y = y;
            this.diameter = diameter;
            this.color = color;
        }

        public void setColor(Color color){
            this.color = color;
        }

        public void draw(Graphics g){
            g.setColor(color);
            g.fillOval(x, y, diameter, diameter);
        }
    }
}
