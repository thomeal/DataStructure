package project;

//import java.util.Map;

public class Player {
	protected int playerNumber;                              // 玩家号码
	protected int numberOfBulletsFired;                      // 当前子弹数量
	protected int direction;                                 // 坦克方向
	protected Point coordinates;							   //坦克中心点
	protected static  int rotationSpeed = 3;            // 转向速度
	protected static  double forwardSpeed = 1;          // 前进速度
	protected static  double reverseSpeed = -.65;       // 后退速度
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
		if (cornerCrash(nextPoint,GameEngine.tankWidth)){                  //撞角落
			nextPoint = new Point(this.coordinates.getxCoord()             //水平移动
					+ (speed * Math.cos(Math.toRadians(90-this.direction))),
					this.coordinates.getyCoord());
			if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||
					wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint = new Point(this.coordinates.getxCoord(),        //水平移动不行，垂直移动
						this.coordinates.getyCoord()
								- (speed * Math.sin(Math.toRadians(90-this.direction))));
				if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||
						wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
					nextPoint= new Point(this.coordinates.getxCoord(),     //垂直移动不行，水平移动
							this.coordinates.getyCoord());
				}
			}
		}else {
			if (wallCrashVertical(nextPoint,GameEngine.tankWidth)&&wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint= new Point(this.coordinates.getxCoord(),          //如果还会撞墙，那就不移动
						this.coordinates.getyCoord());
			}
			if (wallCrashVertical(nextPoint, GameEngine.tankWidth)){        //碰垂直墙，垂直移动

				nextPoint = new Point(this.coordinates.getxCoord(),
						this.coordinates.getyCoord()
								- (speed * Math.sin(Math.toRadians(90-this.direction))));
			}
			if(wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){        //碰水平墙，水平移动
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
		//碰垂直墙判断
		w=w/2;
		int xWall=0;
		if (this.currentXSquare()!=(int)this.currentXSquare()){
			xWall=1;                                                         //防止卡在方块中间
		}
		//方块左面有墙，物体在墙左边，右边同理
		boolean byLeftWall = engine.maze.isWallLeft((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inLeftWall = (p.getxCoord()-w<=((int)this.currentXSquare())*GameEngine.squareWidth+
				((int)this.currentXSquare()+1)*GameEngine.wallWidth);
		boolean byRightWall = engine.maze.isWallRight((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inRightWall = (p.getxCoord()+w>=
				((int)this.currentXSquare()+1+xWall)*GameEngine.squareWidth+
						((int)this.currentXSquare()+1+xWall)*GameEngine.wallWidth);
		return ((byLeftWall&&inLeftWall)||(byRightWall&&inRightWall));
	}
	protected boolean wallCrashHorizontal(Point p, int w){                      //碰水平墙判断
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

	protected boolean cornerCrash(Point p, int w){                               //碰角落判断
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
		return distance<w;                                                   //算出和四面墙距离，比较
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

	public int currentXSquare() {                            //返回当前方块是第几个
		return (int)(getCoordinates().getxCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	public int currentYSquare() {                            //跟上面一样
		return (int)(getCoordinates().getyCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	//初始化
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


