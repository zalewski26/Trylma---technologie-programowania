package TestSocket;

import java.awt.*;

public class Pawn {
    private int x;
    private int y;
    private int width;
    private int playerId;
    private int height;
    private int row;
    private int column;
    private Color color;

    public Pawn(int x, int y, int width, int height, int playerId,Color color, int row, int column){
        this.x = x;
        this.y = y;
        this.width=width;
        this.playerId=playerId;
        this.height=height;
        this.color = color;
        this.row=row;
        this.column=column;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    boolean contains(Point point)
	{
		/** sprawdzamy czy dany punkt lezy w mniejszej odleglosci od srodka okregu niz promien*/
		int radius = getHeight()/2;
                int centerX = getX() + radius;
                int centerY = getY() + radius;
                int distance = (int)Math.sqrt((Math.pow(point.x - centerX, 2)) + (Math.pow(point.y - centerY, 2)));
                if (distance > radius)
                {
                        return false;
                } 
                return true;
	}
    public int getX()
    {
    	return x;
    }
    public void setX(int x)
    {
    	this.x=x;
    }
    public int getY()
    {
    	return y;
    }
    public void setY(int y)
    {
    	this.y=y;
    }
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * @return the playerId
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public void paint(Graphics2D g2d) 
	{
		g2d.setColor(color);
		System.out.println(this.color);
		g2d.fillOval(getX(), getY(), getWidth(), getHeight());
	}
}
