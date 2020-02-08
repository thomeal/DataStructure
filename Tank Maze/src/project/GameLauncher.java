package project;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
//��ͼ�õ�����ϵ��ԭ�������Ͻ�,��������������Ϊ��λ������ϵ��
//
public class GameLauncher extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Runnable��ʵ�������߳�ִ�У�ʵ�����붨��һ���޲����ķ�������Ϊrun ��

	private GameEngine engine = new GameEngine();       //������Ϸ����
	private int time =60;
	public static final int xDimension=55*Maze.getGridWidth()+GameEngine.squareWidth*4;
	public static final int yDimension=55*Maze.getGridWidth();             //������Ļ��С   finalȷ���޷��ı�   387
	private int mark=0;          
	private boolean running=false;                      //�����ж�
	private Thread thread;                              //����һ���̶߳���
	private boolean[] instructionsArray = new boolean[12]; //�����1��2���ƶ��Լ��������

	private synchronized void start(){                    // ���̿�ʼ����������
		running=true;
		thread=new Thread(this);                           //Ӧ���Ƕ��߳�ͬ��
		thread.start();                                    //���̿�ʼ
	}

	private synchronized void stop(){
		running=false;
		try {                                                   //���ֻ��Ϊ�˱��������ʹ���߳�ȫ������������ע�͵�
			thread.join();
			//thread.join�ĺ����ǵ�ǰ�߳���Ҫ�ȴ�thread�߳���ֹ֮��Ŵ�thread.join���ء�
			// ����˵�������߳�û��ִ����֮ǰ����һֱ������join��������
		} catch (InterruptedException e) {
			e.printStackTrace();                                //�������д�ӡ�쳣��Ϣ�ڳ����г����λ�ü�ԭ��
		}
		System.exit(1);                                        //�����˳�
	}
	@Override                                                  //��ϵͳ���run����                             ���Լӣ�
	public void run(){                                         //��дrun����
		addKeyListener(new KeyboardInput(this));               //�Ѽ����ϵ�¼�����
		//init();                                                //�ͼ��̽�������ϵ
		long lastTime = System.nanoTime();                     //�����������е�Java������ĸ߷ֱ���ʱ��Դ�ĵ�ǰֵ��������Ϊ��λ
		final double amountOfTicks=60.0;                       //����1s�ڲ�����������
		double ns = 1000000000/amountOfTicks;
		double delta = 0;                                      //����
//		engine.AIs.get(0).FindPath(new Square(engine.Players.get(0).currentXSquare(),engine.Players.get(0).currentYSquare()), new Square(engine.Players.get(1).currentXSquare(),engine.Players.get(0).currentYSquare()));
		while(running){
			long now = System.nanoTime();                       //ͬȡ��ʱ��
			delta+=(now-lastTime)/ns;
			lastTime=now;
			if(delta>=1){
				tick();											//һ���ƶ���һ���ƶ���һ��ʱ����٣�һ����ײ���
				AIrun();
				render();                                       //��һ��ͼ
				delta--;                                        //������һ
				time++;
			}
		}
		stop();                                                 //�������
	}
	private void tick(){
		//�ƶ���ң��ж�����ÿ��ֵ ���
		if(!GameEngine.player1_dead) {
			if (instructionsArray[0]) {
				engine.Players.get(0).goForward();
			} else if (instructionsArray[2]) {
				engine.Players.get(0).reverse();
			}
			if (instructionsArray[1]) {
				engine.Players.get(0).turnLeft();
			} else if (instructionsArray[3]) {
				engine.Players.get(0).turnRight();
			}
			if (instructionsArray[4]){
				engine.Players.get(0).shoot();
				instructionsArray[4]=false;
			}
			if(instructionsArray[10]){
				engine.Players.get(0).immortal();
				instructionsArray[10]=false;
			}
		}
		if(!GameEngine.player2_dead) {
			if (instructionsArray[5]) {
				engine.Players.get(1).goForward();
			} else if (instructionsArray[7]) {
				engine.Players.get(1).reverse();
			}
			if (instructionsArray[6]) {
				engine.Players.get(1).turnLeft();
			} else if (instructionsArray[8]) {
				engine.Players.get(1).turnRight();
			}
			if (instructionsArray[9]){
				engine.Players.get(1).shoot();
				instructionsArray[9]=false;
			}
			if(instructionsArray[11]){
				engine.Players.get(1).immortal();
				instructionsArray[11]=false;
			}
		}
		//���ÿ����ӳ�
		for (int i = 0 ;i<GameEngine.bulletList.size();i++){
			GameEngine.bulletList.get(i).moveBullet();            //ÿ���ӵ��ƶ�
			boolean removed = GameEngine.bulletList.get(i).reduceTimer();  //����ʱ��
			if (!removed){                                        //�Ƿ��ӵ��ƶ�ʱ�����
				GameEngine.bulletList.get(i).tankCollision();     //��ײ���
			}
		}
		if(time%60==0){
			engine.Players.get(0).cd--;
			engine.Players.get(1).cd--;
		}
		if(engine.Players.get(0).immortal){
			if(time%60==0)
				engine.Players.get(0).lastingtime--;
			if(engine.Players.get(0).lastingtime<0)
				engine.Players.get(0).immortal=false;
		}
		if(engine.Players.get(1).immortal){
			if(time%60==0)
				engine.Players.get(1).lastingtime--;
			if(engine.Players.get(1).lastingtime<0)
				engine.Players.get(1).immortal=false;
		}
	}
	private void AIrun(){
		for(int i=0;i<GameEngine.AiNums;i++) {
			if (!GameEngine.ai_dead[i]) {
				if (engine.AIs.get(i).getTrace().size() > 5) {
					if (time % 60 == 0) {
						engine.AIs.get(i).FindPath(new Square(engine.Players.get(0).currentXSquare(), engine.Players.get(0).currentYSquare()), new Square(engine.Players.get(1).currentXSquare(), engine.Players.get(1).currentYSquare()));
					}
					engine.AIs.get(i).move();
			}
				else {
					if (engine.AIs.get(i).angle == 361 || mark == 1 || engine.AIs.get(i).numberOfBulletsFired == 5) {
						if (time % 60 == 0) {
							engine.AIs.get(i).FindPath(new Square(engine.Players.get(0).currentXSquare(), engine.Players.get(0).currentYSquare()), new Square(engine.Players.get(1).currentXSquare(), engine.Players.get(1).currentYSquare()));
						}
						if (time % 60 == 0 && engine.AIs.get(i).numberOfBulletsFired > 0) {
							mark = 0;
						}
						if (engine.AIs.get(i).numberOfBulletsFired == 5) {
							mark = 1;
						}
						engine.AIs.get(i).move();
					}
					int temp = engine.AIs.get(i).numberOfBulletsFired;
					if (mark == 0) {
						engine.AIs.get(i).shoot1();
					}
					if (engine.AIs.get(i).numberOfBulletsFired > temp) {
						mark = 1;
					}
					if (GameEngine.player1_dead || GameEngine.player2_dead) {
						mark = 0;
					}
				}
				if (GameEngine.ai_dead[i]) mark = 0;
			}
		}
	}
	private void render(){                                         //��ͼ����
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null){
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();                         //GraphicsΪ����
		//������{
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(5.0f));                       //�ߴ�ϸ
		//������:
		g.drawRect(2,3,xDimension,yDimension);
		g.setColor(Color.WHITE);
		g.fillRect(4,6,xDimension,yDimension);
		//g.drawImage(background, 0, 0, this);
		g.setColor(Color.BLACK);
		//��ǽ:
		for (int x = 0; x<Maze.getGridWidth();x++){
			for (int y = 0; y<Maze.getGridWidth(); y++){
				int x1=(GameEngine.squareWidth+GameEngine.wallWidth)*x+2;
				int y1=(GameEngine.squareWidth)*(y+1)+GameEngine.wallWidth*(y+1)+2;
				int x11=(GameEngine.squareWidth+GameEngine.wallWidth)*x+GameEngine.squareWidth+GameEngine.wallWidth+2;
				int y11=(GameEngine.squareWidth)*(y+1)+GameEngine.wallWidth*(y+1)+2;
				if (engine.maze.isWallBelow(x, y)){
					g2.drawLine(x1,y1,x11,y11);                                                     //��ǽ
				}
//				else {
//					g.setColor(Color.WHITE);
//					g2.drawLine(x1,y1,x11,y11);
//				}
			g.setColor(Color.BLACK);
				x1=(GameEngine.squareWidth+GameEngine.wallWidth)*(x+1)+2;
				y1=(GameEngine.squareWidth+GameEngine.wallWidth)*y+2;
				x11=(GameEngine.squareWidth+GameEngine.wallWidth)*(x+1)+2;
				y11=(GameEngine.squareWidth+GameEngine.wallWidth)*y+GameEngine.squareWidth+GameEngine.wallWidth+2;
				if (engine.maze.isWallRight(x, y)){
					g2.drawLine(x1,y1,x11,y11);
				}
			}
		}
		//��̹��
		if(!GameEngine.player1_dead) {
			if(engine.Players.get(0).immortal)
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.BLUE);
			g2.setStroke(new BasicStroke(1.0f));
			int x1 = (int) (engine.Players.get(0).getCoordinates().getxCoord() - GameEngine.tankWidth / 2);//��Ҫ��������д����  ���1
			int y1 = (int) (engine.Players.get(0).getCoordinates().getyCoord() - GameEngine.tankWidth / 2);
			int x11 = (int) (engine.Players.get(0).getCoordinates().getxCoord() - GameEngine.tankWidth / 2 + GameEngine.tankWidth / 2);
			int y11 = (int) (engine.Players.get(0).getCoordinates().getyCoord() - GameEngine.tankWidth / 2 + GameEngine.tankWidth / 2);
			x11 = (int) (x11 + (GameEngine.tankWidth / 2) * Math.sin(Math.toRadians(180 - engine.Players.get(0).getDirection())));
			y11 = (int) (y11 + (GameEngine.tankWidth / 2) * Math.cos(Math.toRadians(180 - engine.Players.get(0).getDirection())));

			g2.drawOval(x1, y1, GameEngine.tankWidth, GameEngine.tankWidth);
			g.drawLine(x1 + GameEngine.tankWidth / 2, y1 + GameEngine.tankWidth / 2, x11, y11);
		}

		if(!GameEngine.player2_dead) {
			if(engine.Players.get(1).immortal)
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.RED);
			g2.setStroke(new BasicStroke(1.0f));                                                           //  ���2
			int x2 = (int) (engine.Players.get(1).getCoordinates().getxCoord() - GameEngine.tankWidth / 2);
			int y2 = (int) (engine.Players.get(1).getCoordinates().getyCoord() - GameEngine.tankWidth / 2);
			int x22 = (int) (engine.Players.get(1).getCoordinates().getxCoord() - GameEngine.tankWidth / 2 + GameEngine.tankWidth / 2);
			int y22 = (int) (engine.Players.get(1).getCoordinates().getyCoord() - GameEngine.tankWidth / 2 + GameEngine.tankWidth / 2);
			x22 = (int) (x22 + (GameEngine.tankWidth / 2) * Math.sin(Math.toRadians(180 - engine.Players.get(1).getDirection())));
			y22 = (int) (y22 + (GameEngine.tankWidth / 2) * Math.cos(Math.toRadians(180 - engine.Players.get(1).getDirection())));

			g2.drawOval(x2, y2, GameEngine.tankWidth, GameEngine.tankWidth);
			g.drawLine(x2 + GameEngine.tankWidth / 2, y2 + GameEngine.tankWidth / 2, x22, y22);
		}

		for(int i=0;i<GameEngine.AiNums;i++){
			if(!GameEngine.ai_dead[i]) {
				g.setColor(Color.GREEN);
				g2.setStroke(new BasicStroke(1.0f));                                                           //  AI
				int x3 = (int) (engine.AIs.get(i).getCoordinates().getxCoord() - GameEngine.tankWidth / 2);
				int y3 = (int) (engine.AIs.get(i).getCoordinates().getyCoord() - GameEngine.tankWidth / 2);
				int x33 = (int) (engine.AIs.get(i).getCoordinates().getxCoord() - GameEngine.tankWidth / 2 + GameEngine.tankWidth / 2);
				int y33 = (int) (engine.AIs.get(i).getCoordinates().getyCoord() - GameEngine.tankWidth / 2 + GameEngine.tankWidth / 2);
				x33 = (int) (x33 + (GameEngine.tankWidth / 2) * Math.sin(Math.toRadians(180 - engine.AIs.get(i).getDirection())));
				y33 = (int) (y33 + (GameEngine.tankWidth / 2) * Math.cos(Math.toRadians(180 - engine.AIs.get(i).getDirection())));

				g2.drawOval(x3, y3, GameEngine.tankWidth, GameEngine.tankWidth);
				g.drawLine(x3 + GameEngine.tankWidth / 2, y3 + GameEngine.tankWidth / 2, x33, y33);
			}
		}


		g2.setStroke(new BasicStroke(3.0f));
		g.setColor(Color.BLACK);                                                                               //�ӵ�
		for (int i = 0 ;i<GameEngine.bulletList.size();i++){
			int x0=(int)(GameEngine.bulletList.get(i).getPosition().getxCoord());
			int y0=(int)(GameEngine.bulletList.get(i).getPosition().getyCoord());
			g2.drawOval(x0,y0,GameEngine.bulletWidth/2,GameEngine.bulletWidth/2);
		}
		
		g.drawString("С��ս��",engine.maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)+5,20);	
		g.drawString(""+GameEngine.player1_score,engine.maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)+60,20);	
		g.drawString("С��ս��",engine.maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)+5,GameEngine.squareWidth+20);	
		g.drawString(""+GameEngine.player2_score,engine.maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)+60,GameEngine.squareWidth+20);	
		g.drawString("aiս��",engine.maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)+5,GameEngine.squareWidth*2+20);	
		g.drawString(""+GameEngine.ai_score,engine.maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)+60,GameEngine.squareWidth*2+20);
		g.dispose();                                                    //���ʻ���
		g2.dispose();                                                   //���ʻ���
		bs.show();                                                      //ͼƬչʾ
	}
	public void keyPressed(KeyEvent e){
		switch (e.getKeyCode())
		//getKeyCode():������ÿһ����ť���ж�Ӧ��(Code),��������֪�û�����ʲô�������ص�ǰ��ť����ֵ
		{
			case KeyEvent.VK_W:
				instructionsArray[0]=true;
				break;
			case KeyEvent.VK_A:
				instructionsArray[1]=true;
				break;
			case KeyEvent.VK_S:
				instructionsArray[2]=true;
				break;
			case KeyEvent.VK_D:
				instructionsArray[3]=true;
				break;
			case KeyEvent.VK_Q:
				instructionsArray[4]=true;
				break;
			case KeyEvent.VK_UP:
				instructionsArray[5]=true;
				break;
			case KeyEvent.VK_LEFT:
				instructionsArray[6]=true;
				break;
			case KeyEvent.VK_DOWN:
				instructionsArray[7]=true;
				break;
			case KeyEvent.VK_RIGHT:
				instructionsArray[8]=true;
				break;
			case KeyEvent.VK_ENTER:
				instructionsArray[9]=true;
				break;
			case KeyEvent.VK_E:
				instructionsArray[10]=true;
				break;
			case KeyEvent.VK_SLASH:
				instructionsArray[11]=true;
				break;
		}
	}
	public void keyReleased(KeyEvent e){
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_W:
				instructionsArray[0]=false;
				break;
			case KeyEvent.VK_A:
				instructionsArray[1]=false;
				break;
			case KeyEvent.VK_S:
				instructionsArray[2]=false;
				break;
			case KeyEvent.VK_D:
				instructionsArray[3]=false;
				break;
			case KeyEvent.VK_Q:
				instructionsArray[4]=false;
				break;
			case KeyEvent.VK_UP:
				instructionsArray[5]=false;
				break;
			case KeyEvent.VK_LEFT:
				instructionsArray[6]=false;
				break;
			case KeyEvent.VK_DOWN:
				instructionsArray[7]=false;
				break;
			case KeyEvent.VK_RIGHT:
				instructionsArray[8]=false;
				break;
			case KeyEvent.VK_ENTER:
				instructionsArray[9]=false;
				break;
			case KeyEvent.VK_E:
				instructionsArray[10]=false;
				break;
			case KeyEvent.VK_SLASH:
				instructionsArray[11]=false;
				break;
		}
	}

	public static void main(String args[]){
		GameLauncher game = new GameLauncher();                                              //����gameʵ������ϷԤ����
		//���ô��ڴ�С:
		game.setSize(new Dimension(game.xDimension,game.yDimension));
		JFrame frame = new JFrame("Tank Maze");                                           //��������
		frame.add(game);                                                                     //game��ӵ�����frame��
		frame.pack();                                                                        //��ʾ����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                                //�쳣�������رմ���
		frame.setResizable(true);                                                           //���ڴ�С���ɱ�
		frame.setLocationRelativeTo(null);                                                   //������������Ļ������
		frame.setVisible(true);                                                              //��Ϊ�ɼ�
		game.start();                                                                        //��Ϸ��ʼ
	}
}
