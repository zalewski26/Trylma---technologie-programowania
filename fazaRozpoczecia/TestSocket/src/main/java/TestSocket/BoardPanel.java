package TestSocket;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {
	private int rows=17;
	private int columns=13;
	private int WINDOW_HEIGHT=850;
	private int WINDOW_WIDTH=650;
	private List<Pawn> pawns= new ArrayList<Pawn>();
    private Pawn currentPawn;
    private Color currentColor;
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

    public BoardPanel(){
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        for(int i=0;i<this.rows;i++)
    	{
    		for(int j=0;j<this.columns;j++)
    		{
    			if(i%2==1)
    			{
    				addPawn(new Pawn(25+j*WINDOW_WIDTH/columns,i*WINDOW_HEIGHT/rows,WINDOW_HEIGHT/rows,WINDOW_WIDTH/columns,pattern[i][j],colorFromNumber(pattern[i][j]),i,j));
    			}
    			else
    			{
    				addPawn(new Pawn(j*WINDOW_WIDTH/columns,i*WINDOW_HEIGHT/rows,WINDOW_HEIGHT/rows,WINDOW_WIDTH/columns,pattern[i][j],colorFromNumber(pattern[i][j]),i,j));
    			}
    		}
    	}
        repaint();
    }
    public void addPawn(Pawn pawn) 
	{
      		pawns.add(pawn);
      		repaint();
   	}
    /*public void mark(int i, int j){
        currentCircle = board[i][j];
        currentColor = currentCircle.getColor();
        currentCircle.setColor(Color.CYAN);
        repaint();

    }

    public void makeMove(int i, int j){
        currentCircle.setColor(Color.DARK_GRAY);
        board[i][j].setColor(currentColor);
        repaint();
    }

    public void oppMove(int prevI, int prevJ, int i, int j, int oppId){
        board[prevI][prevJ].setColor(Color.DARK_GRAY);
        board[i][j].setColor(colorFromNumber(oppId));
        repaint();
    }*/

    private Color colorFromNumber(int number){
        switch (number){
            case 0:
                return Color.WHITE;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.BLACK;
            case 4:
                return Color.DARK_GRAY;
            case 5:
                return Color.BLUE;
            case 6:
                return Color.RED;
        }
        return null;
    }

    /*public Circle[][] getBoard(){
        return board;
    }

    public void pick(int i, int j){
        board[i][j].setColor(Color.CYAN);
        repaint();
    }*/

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(Pawn pawn:pawns)
		{
        	if(pawn.getPlayerId()==8)
        	{
        		
        	}
        	else
        	{
        		Graphics2D g2d = (Graphics2D) g.create();
    			pawn.paint(g2d);
    			g2d.dispose();	
        	}
		}

    }
}
