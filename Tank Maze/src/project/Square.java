package project;

public class Square {
	private int xCoord;
	private int yCoord;

	public Square(int a, int b){
		this.xCoord=a;
		this.yCoord=b;
	}

	public int getxCoord() {
		return xCoord;
	}
	
	public int getyCoord() {
		return yCoord;
	}
	
	public String toString(){
		return "" + xCoord + "," + yCoord;
	}
	
	public boolean equals(Square a){
		return this.getxCoord()==a.getxCoord()&&this.getyCoord()==a.getyCoord();
	}
}