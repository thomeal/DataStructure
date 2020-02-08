package project;

//import java.util.Map;

public class Player {
	protected int playerNumber;                              // ��Һ���
	protected int numberOfBulletsFired;                      // ��ǰ�ӵ�����
	protected int direction;                                 // ̹�˷���
	protected Point coordinates;							   //̹�����ĵ�
	protected static  int rotationSpeed = 3;            // ת���ٶ�
	protected static  double forwardSpeed = 1;          // ǰ���ٶ�
	protected static  double reverseSpeed = -.65;       // �����ٶ�
	protected boolean immortal=false;
	protected static int dead=0;
	protected GameEngine engine;
	public int lastingtime=2;
	public int cd =0;
	public void turnRight() {
		this.direction += rotationSpeed;
		if (this.direction>359){
			this.direction-=360;
		}
	}
	public void turnLeft() {
		this.direction -= rotationSpeed;
		if (this.direction<0){
			this.direction+=360;
		}
	}
	private void move(double speed) {
		Point nextPoint = new Point(this.coordinates.getxCoord()
				+ (speed * Math.cos(Math.toRadians(90-this.direction))),
				this.coordinates.getyCoord()
						- (speed * Math.sin(Math
						.toRadians(90-this.direction))));
		if (cornerCrash(nextPoint,GameEngine.tankWidth)){                  //ײ����
			nextPoint = new Point(this.coordinates.getxCoord()             //ˮƽ�ƶ�
					+ (speed * Math.cos(Math.toRadians(90-this.direction))),
					this.coordinates.getyCoord());
			if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||
					wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint = new Point(this.coordinates.getxCoord(),        //ˮƽ�ƶ����У���ֱ�ƶ�
						this.coordinates.getyCoord()
								- (speed * Math.sin(Math.toRadians(90-this.direction))));
				if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||
						wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
					nextPoint= new Point(this.coordinates.getxCoord(),     //��ֱ�ƶ����У�ˮƽ�ƶ�
							this.coordinates.getyCoord());
				}
			}
		}else {
			if (wallCrashVertical(nextPoint,GameEngine.tankWidth)&&wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint= new Point(this.coordinates.getxCoord(),          //�������ײǽ���ǾͲ��ƶ�
						this.coordinates.getyCoord());
			}
			if (wallCrashVertical(nextPoint, GameEngine.tankWidth)){        //����ֱǽ����ֱ�ƶ�

				nextPoint = new Point(this.coordinates.getxCoord(),
						this.coordinates.getyCoord()
								- (speed * Math.sin(Math.toRadians(90-this.direction))));
			}
			if(wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){        //��ˮƽǽ��ˮƽ�ƶ�
				nextPoint = new Point(this.coordinates.getxCoord()
						+ (speed * Math.cos(Math.toRadians(90-this.direction))),
						this.coordinates.getyCoord());
			}
		}
		this.coordinates=nextPoint;
	}
	public void goForward(){
		move(forwardSpeed);
	}

	public void reverse() {
		move(reverseSpeed);
	}


	protected boolean wallCrashVertical(Point p, int w){
		//����ֱǽ�ж�
		w=w/2;
		int xWall=0;
		if (this.currentXSquare()!=(int)this.currentXSquare()){
			xWall=1;                                                         //��ֹ���ڷ����м�
		}
		//����������ǽ��������ǽ��ߣ��ұ�ͬ��
		boolean byLeftWall = engine.maze.isWallLeft((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inLeftWall = (p.getxCoord()-w<=((int)this.currentXSquare())*GameEngine.squareWidth+
				((int)this.currentXSquare()+1)*GameEngine.wallWidth);
		boolean byRightWall = engine.maze.isWallRight((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inRightWall = (p.getxCoord()+w>=
				((int)this.currentXSquare()+1+xWall)*GameEngine.squareWidth+
						((int)this.currentXSquare()+1+xWall)*GameEngine.wallWidth);
		return ((byLeftWall&&inLeftWall)||(byRightWall&&inRightWall));
	}
	protected boolean wallCrashHorizontal(Point p, int w){                      //��ˮƽǽ�ж�
		w=w/2;
		int yWall=0;
		if (this.currentYSquare()!=(int)this.currentYSquare()){
			yWall=1;
		}
		boolean byTopWall = engine.maze.isWallAbove((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inTopWall = (p.getyCoord()-w<=
				((int)this.currentYSquare())*GameEngine.squareWidth+
						((int)this.currentYSquare()+1)*GameEngine.wallWidth);
		boolean byBottomWall = engine.maze.isWallBelow((int) this.currentXSquare(), (int) this.currentYSquare());
		boolean inBottomWall = (p.getyCoord()+w>=
				((int)this.currentYSquare()+1+yWall)*GameEngine.squareWidth+
						((int)this.currentYSquare()+1+yWall)*GameEngine.wallWidth);
		return ((byTopWall&&inTopWall)|(byBottomWall&&inBottomWall));
	}

	protected boolean cornerCrash(Point p, int w){                               //�������ж�
		w=w/2;
		int x=0;
		int y=0;
		int xCounter=(int)p.getxCoord();
		while (xCounter>GameEngine.wallWidth+GameEngine.squareWidth/2){
			xCounter=xCounter-GameEngine.wallWidth-GameEngine.squareWidth;
			x++;
		}
		int yCounter=(int)p.getyCoord();
		while (yCounter>GameEngine.wallWidth+GameEngine.squareWidth/2){
			yCounter=yCounter-GameEngine.wallWidth-GameEngine.squareWidth;
			y++;
		}
		boolean isWallInCorner=engine.maze.isWallAbove(x,y)||engine.maze.isWallLeft(x,y);
		if(x>0){
			isWallInCorner=isWallInCorner||engine.maze.isWallAbove(x-1,y);
		}
		if(y>0){
			isWallInCorner=isWallInCorner||engine.maze.isWallLeft(x,y-1);
		}
		if(!isWallInCorner){
			return false;
		}
		Point p1=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),
				y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p2=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+
				GameEngine.wallWidth,y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p3=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),
				y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		Point p4=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth,
				y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		double distance1=Point.distance(p1,p);
		double distance2=Point.distance(p2,p);
		double distance3=Point.distance(p3,p);
		double distance4=Point.distance(p4,p);
		double distance=Math.min(distance1, Math.min(distance2,Math.min(distance3,distance4)));
		return distance<w;                                                   //���������ǽ���룬�Ƚ�
	}

	public void shoot() {
		if (this.numberOfBulletsFired < 5) {
			Point bulletStart = new Point(this.coordinates.getxCoord()
					+ ((GameEngine.bulletWidth/2+GameEngine.tankWidth/2) * Math.cos(Math.toRadians(90-this.direction))),
					this.coordinates.getyCoord()
							- ((GameEngine.bulletWidth/2+GameEngine.tankWidth/2) *
							Math.sin(Math.toRadians(90-this.direction))));
			if (wallCrashVertical(bulletStart, GameEngine.bulletWidth)||
					wallCrashHorizontal(bulletStart,GameEngine.bulletWidth)||
					cornerCrash(bulletStart,GameEngine.bulletWidth)){
				this.hit();
			} else {
				GameEngine.bulletList.add(new Bullet(playerNumber, bulletStart, direction,engine));
				this.numberOfBulletsFired += 1;
			}
		}
	}

	public void hit() {
		if (this.playerNumber == 0&&!this.immortal) {
			GameEngine.player1_dead=true;
			dead++;
		}
		else if(this.playerNumber==1&&!this.immortal) {
			GameEngine.player2_dead=true;
			dead++;
		}
		else if(this.playerNumber<0&&!this.immortal){
			GameEngine.ai_dead[0-playerNumber-1]=true;
			dead++;
		}
		if(dead==1+GameEngine.AiNums||(GameEngine.player1_dead&&GameEngine.player2_dead)) {
			if(dead==1+GameEngine.AiNums)
				GameEngine.all_AI_dead=true;
			dead=0;
			engine.roundOver();
		}
	}

	public void immortal(){
		if(cd<=0) {
			cd = 9;
			lastingtime=2;
			immortal = true;
		}
	}

	public int currentXSquare() {                            //���ص�ǰ�����ǵڼ���
		return (int)(getCoordinates().getxCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	public int currentYSquare() {                            //������һ��
		return (int)(getCoordinates().getyCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	//��ʼ��
	public Player(int playerNo, double x, double y,GameEngine e) {
		this.playerNumber = playerNo;
		this.numberOfBulletsFired = 0;
		this.direction = (int) (Math.random() * 360);
		this.coordinates = new Point(x, y);
		this.engine=e;
	}


	public void decreaseNumberOfBulletsFired() {
		if (this.numberOfBulletsFired>0){
			this.numberOfBulletsFired -= 1;
		}
	}

	public Point getCoordinates(){
		return this.coordinates;
	}

	public int getDirection(){
		return this.direction;
	}
}


