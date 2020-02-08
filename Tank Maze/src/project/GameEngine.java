package project;

import java.util.ArrayList;
import java.util.Vector;

public class GameEngine {
	// GameEngine��������ʼ����Ϸ�������Թ������ɣ�̹�˳�ʼλ�õ�ѡ���ȣ�
	public static int player1_score;
	public static int player2_score;
	public static int ai_score;
	public int PlayerNums=2;
	public static int AiNums=2;
	public static boolean player1_dead = false;
	public static boolean player2_dead = false;
	public static boolean all_AI_dead=false;
	public static boolean[] ai_dead = new boolean[AiNums];
	public static int tankWidth = 26;
	public static int bulletWidth = 6;
	public static int squareWidth = 50;
	public static int wallWidth = 5;
	public Maze maze;
	public Vector<Player> Players=new Vector<Player>(PlayerNums);
	public Vector<AI> AIs=new Vector<AI>(AiNums);
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>((2+AiNums)*5);
	public GameEngine() {
		// ��ʼ������
		player1_score=0;
		player2_score = 0;
		ai_score=0;
		maze = new Maze();
		// ���ѡȡ̹�˵ĳ�ʼλ��
		int x = (int) (Math.random() * maze.getGridWidth());
		int y = (int) (Math.random() * maze.getGridWidth());
		int x1 = (int) (Math.random() * maze.getGridWidth());
		int y1 = (int) (Math.random() * maze.getGridWidth());
		int xa=(int) (Math.random() * maze.getGridWidth());
		int ya=(int) (Math.random() * maze.getGridWidth());
		while (x1 == x && y1==y) {//ʹ��ͬ��̹�˿����ڲ�ͬ��λ������
			x1 = (int) (Math.random() * maze.getGridWidth());
			y1 = (int) (Math.random() * maze.getGridWidth());
		}
//		while((xa==x&&ya==y)||(xa==x1&&ya==y1)){
//			xa=(int) (Math.random() * maze.getGridWidth());
//			ya=(int) (Math.random() * maze.getGridWidth());
//		}
//		��Tank������Square������
		x=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x;
		y=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y;
		x1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x1;
		y1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y1;
//		xa=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*xa;
//		ya=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*ya;
		this.Players.add (new Player(0, x, y,this));
		this.Players.add (new Player(1, x1, y1,this));
		for(int i=1;i<=AiNums;i++)
			this.AIs.add(new AI(0-i,GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*(int)(Math.random() * maze.getGridWidth()),GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*(int)(Math.random() * maze.getGridWidth()),this));
	}

	public void roundOver() {
		if (GameEngine.player1_dead&&GameEngine.all_AI_dead)
			GameEngine.player2_score++;
		else if (GameEngine.player2_dead&&GameEngine.all_AI_dead)
			GameEngine.player1_score++;
		else
			GameEngine.ai_score++;
		for(int i=0;i<AiNums;i++)
			GameEngine.ai_dead[i]=false;
		GameEngine.player1_dead=false;
		GameEngine.player2_dead=false;
		Players.clear();
		AIs.clear();
		int x = (int) (Math.random() * maze.getGridWidth());
		int y = (int) (Math.random() * maze.getGridWidth());
		int x1 = (int) (Math.random() * maze.getGridWidth());
		int y1 = (int) (Math.random() * maze.getGridWidth());
		int xa=(int) (Math.random() * maze.getGridWidth());
		int ya=(int) (Math.random() * maze.getGridWidth());
		while (x1 == x && y1==y) {//ʹ��ͬ��̹�˿����ڲ�ͬ��λ������
			x1 = (int) (Math.random() * maze.getGridWidth());
			y1 = (int) (Math.random() * maze.getGridWidth());
		}
//		while((xa==x&&ya==y)||(xa==x1&&ya==y1)){
//			xa=(int) (Math.random() * maze.getGridWidth());
//			ya=(int) (Math.random() * maze.getGridWidth());
//		}
//		��Tank������Square������
		x=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x;
		y=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y;
		x1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x1;
		y1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y1;
//		xa=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*xa;
//		ya=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*ya;
		this.Players.add (new Player(0, x, y,this));
		this.Players.add (new Player(1, x1, y1,this));
		for(int i=1;i<=AiNums;i++)
			this.AIs.add(new AI(0-i,GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*(int)(Math.random() * maze.getGridWidth()),GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*(int)(Math.random() * maze.getGridWidth()),this));
		GameEngine.bulletList = new ArrayList<Bullet>((2+AiNums)*5);
		maze = new Maze();
	}
}




