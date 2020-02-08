package project;

public class Bullet{

	private int timer;
	private int owner;
	private int angle;
	private Point position;
	private static final double bulletSpeed = 1.5;

	private GameEngine engine;

	public Bullet(int player, Point position, int angle, GameEngine e) {
		this.owner = player;
		this.angle = angle;
		this.position = position;
		this.timer = 1000;                            //设置时间1000帧
		this.engine=e;
	}

	public Point getPosition() {
		return position;
	}

	public void removeBullet(){
		if (this.owner==0){
			engine.Players.get(0).decreaseNumberOfBulletsFired();
		}else if (this.owner==1){
			engine.Players.get(1).decreaseNumberOfBulletsFired();
		}
		else if (this.owner==-1){
			engine.AIs.get(0).decreaseNumberOfBulletsFired();
		}
		GameEngine.bulletList.remove(this);
	}

	public void moveBullet(){
		Point nextPoint = new Point(this.position.getxCoord()
				+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
				this.position.getyCoord()
						- (bulletSpeed * Math.sin(Math
						.toRadians(90-this.angle))));
		if(nextPoint.getxCoord()==Maze.getGridWidth()*(engine.squareWidth+engine.wallWidth)||
				nextPoint.getyCoord()==Maze.getGridWidth()*(engine.squareWidth+engine.wallWidth)||
				nextPoint.getxCoord()==engine.wallWidth||
				nextPoint.getyCoord()==engine.wallWidth)
		{
			removeBullet();
			return ;
		}
		if (wallCrashHorizontal(nextPoint, GameEngine.bulletWidth)){            //垂直墙碰撞检测
			flipBulletH();
			nextPoint = new Point(this.position.getxCoord()
					+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
					this.position.getyCoord());
		}else if (wallCrashVertical(nextPoint, GameEngine.bulletWidth)){        //同上
			flipBulletV();
			nextPoint = new Point(this.position.getxCoord(),
					this.position.getyCoord()
							- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle))));
		}else if(cornerCrash(nextPoint,GameEngine.bulletWidth)){
			if(this.currentYSquare()==(int)this.currentYSquare()){
				flipBulletH();
				nextPoint = new Point(this.position.getxCoord()
						+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
						this.position.getyCoord());
			}else{
				flipBulletV();
				nextPoint = new Point(this.position.getxCoord(),
						this.position.getyCoord()
								- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle))));
			}
		}
		if(nextPoint.getxCoord()==Maze.getGridWidth()*(engine.squareWidth+engine.wallWidth)||
				nextPoint.getyCoord()==Maze.getGridWidth()*(engine.squareWidth+engine.wallWidth)||
				nextPoint.getxCoord()==engine.wallWidth||
				nextPoint.getyCoord()==engine.wallWidth)
		{
			removeBullet();
			return ;
		}
		this.position=nextPoint;
	}

	public boolean reduceTimer(){                                                //子弹时间计时
		timer--;
		if (timer<0){
			this.removeBullet();
			return true;
		}
		return false;
	}

	protected void flipBulletV(){
		this.angle=(-this.angle) + 360;
	}

	protected void flipBulletH(){
		if (this.angle>180){
			this.angle = -this.angle+540;
		}else{
			this.angle=-this.angle+180;
		}
	}

	public void tankCollision(){                                                  //碰撞检测
		if (!GameEngine.player1_dead&&collision(engine.Players.get(0))){
			this.removeBullet();
			engine.Players.get(0).hit();
		}
		else if (!GameEngine.player2_dead&&collision(engine.Players.get(1))){
			this.removeBullet();
			engine.Players.get(1).hit();
		}
		else{
			for(int i=0;i<GameEngine.AiNums;i++){
				if(!GameEngine.ai_dead[i]&&collision(engine.AIs.get(i))){
					this.removeBullet();
					engine.AIs.get(i).hit();
					return;
				}
			}
		}

	}

	protected boolean collision(Player player){                                   //距离
		double distance = Point.distance(player.getCoordinates(),this.position);
		return (distance<=(GameEngine.tankWidth/2+GameEngine.bulletWidth/2));
	}

	public int currentXSquare() {                            //返回当前方块是第几个
		return (int)(this.position.getxCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	public int currentYSquare() {                            //跟上面一样
		return (int)(this.position.getyCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	protected boolean wallCrashVertical(Point p, int w){                             //水平墙碰撞检测             point中也有一样的方法
		w=w/2;
		int xWall=0;
		if (this.currentXSquare()!=(int)this.currentXSquare()){
			xWall=1;
		}
		boolean byLeftWall = Maze.isWallLeft((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inLeftWall = (p.getxCoord()-w<=
				(this.currentXSquare())*GameEngine.squareWidth+(this.currentXSquare()+1)*GameEngine.wallWidth);
		boolean byRightWall = engine.maze.isWallRight((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inRightWall = (p.getxCoord()+w>=
				((int)this.currentXSquare()+1+xWall)*GameEngine.squareWidth+((int)this.currentXSquare()+1+xWall)*GameEngine.wallWidth);
		return ((byLeftWall&&inLeftWall)||(byRightWall&&inRightWall));
	}
	protected boolean wallCrashHorizontal(Point p, int w){                             //同上
		w=w/2;
		int yWall=0;
		if (this.currentYSquare()!=(int)this.currentYSquare()){
			yWall=1;
		}
		boolean byTopWall = engine.maze.isWallAbove((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inTopWall = (p.getyCoord()-w<=
				(this.currentYSquare())*GameEngine.squareWidth+(this.currentYSquare()+1)*GameEngine.wallWidth);
		boolean byBottomWall = engine.maze.isWallBelow((int) this.currentXSquare(), (int) this.currentYSquare());
		boolean inBottomWall = (p.getyCoord()+w>=
				((int)this.currentYSquare()+1+yWall)*GameEngine.squareWidth+((int)this.currentYSquare()+1+yWall)*GameEngine.wallWidth);
		return ((byTopWall&&inTopWall)|(byBottomWall&&inBottomWall));
	}
	protected boolean cornerCrash(Point p, int w){                                            //同上
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
		Point p1=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p2=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth,y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p3=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		Point p4=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth,y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		double distance1=Point.distance(p1,p);
		double distance2=Point.distance(p2,p);
		double distance3=Point.distance(p3,p);
		double distance4=Point.distance(p4,p);
		double distance=Math.min(distance1, Math.min(distance2,Math.min(distance3,distance4)));
		return distance<w;
	}
}

