package project;
//子弹运算模型
//功能：能对给出的玩家1、2以及ai所在位置找到一个能在击中玩家，且击中之前不会击中自己的角度
public class AIBullet {
    private int timer;                                            //ai将要进行模拟的时长
    private int angle;                                           //ai角度
    private int angle1;                                          //尝试角度
    private int angle2;                                           //子弹运算角度
    private Point position;
    private Point positionNow;                                     //真实位置
    private static final double bulletSpeed = 1.5;

    private GameEngine engine;

    public AIBullet( Point position, int angle, GameEngine e) {
        this.angle = angle;                                                            //angle是传入的角度，也就是ai本身的角度
        this.angle1 = angle;                                                            //angle1是用来循环的角度，每次循环加一，直至找到所求角度
        this.angle2 = angle;                                                            //angle2是一次模拟子弹运动角度angle1每变化一次，angle2变化好多次
        this.position = position;                      //子弹在模拟运行中的位置         //初始化ai位置
        this.timer = 1000;                                                              //*******设置时间100帧,计算100帧内能否击中*********
        this.engine=e;                                                                 //顺带一提，这个模型运算时内部高度内聚，不要轻易改变某个变量的值
        this.positionNow=position;                         //ai的真实位置                         //如果想改变这个模拟运算的timer（改变帧），除了改这个声明，下面还有一个
    }                                                                                  //在modelMoveBullet()方法，也需要改变
    public void setModel(int angle,Point p)                                             //给外界重置ai的位置与当前角度
    {
        this.angle = angle;
        this.angle1 = angle;
        this.angle2 = angle;
        this.position = p;
        this.positionNow=p;
        this.timer = 1000;
    }
    private int changeAngle(int angle){                                 //角度增加1，并且确保角度在360度内
        this.angle1++;
        if(this.angle1>=360)
        {
            this.angle1-=360;
            return this.angle1;
        }
        else if(this.angle1<0)
        {
            this.angle1+=360;
            return this.angle1;
        }                                                             //如果超了一个圈，变回1
        return this.angle1;
    }
    public int modelMoveBullet()
    {
        this.timer=100;
        this.angle1=this.angle;
        System.out.println("Check1");
        while(this.angle1!=this.angle-1&&(this.angle!=0))                                                             //判断所有方向
        {                                                                       //************接下来是一次子弹运行模拟，当角度为angle1时
        	 System.out.println("Check2");
            while(reduceTimer())                                           //一次方向判断能否碰到
            {
            	int i;
                i=moveBullet();                                              //子弹移动
                if(i==1)
                {
                	if(collisionself()&&this.timer<50)                                      //子弹运行50帧以上后会击中自己，则直接跳出这一次模拟
                    {
                        break;
                    }
                    if(AICollision()&&(this.angle1-this.angle)%3==0)                           //碰到坦克直接返回角度
                    {
                    	 System.out.println("Check41******************");
                        return this.angle1;                                              //找到一个ai可以转向成功且击中敌人的角度
                    }
                }
                else
                	break;
            }                                                                //**************一次子弹运行模拟结束
            this.timer=100;                                                   //时间重置
            this.angle2=changeAngle(this.angle1);                             //角度加一
            this.position=this.positionNow;                                    //发射点重置
            System.out.println("Check3");
        }
        System.out.println("Check42*************");
        return 361;                                                              //当所有角度都不能打中敌人，返回361
    }
    private boolean AICollision(){                                                  //区块检测，当有玩家死亡，不检测此玩家
        if(!GameEngine.player1_dead)
        {
            if (collision(0)){
                return true;
            }
        }
        if(!GameEngine.player2_dead)
        {
            if (collision(1)){
                return true;
            }
        }
        return false;
    }
    private boolean collision(int i){                                      //判断子弹能否打到玩家所在区块
        if(currentXSquare()==engine.Players.get(i).currentXSquare()&&currentYSquare()==engine.Players.get(i).currentYSquare())
        {
            return true;
        }
        return false;
    }
    private boolean collisionself(){                                   //距离
        double distance = Point.distance(this.positionNow,this.position);
        return (distance<(GameEngine.tankWidth/2+GameEngine.bulletWidth/2));
    }
    private int moveBullet(){                                               //一帧的移动
		Point nextPoint = new Point(this.position.getxCoord()
				+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle2))),
				this.position.getyCoord()
				- (bulletSpeed * Math.sin(Math
						.toRadians(90-this.angle2))));
		if(nextPoint.getxCoord()>=Maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)||
				nextPoint.getyCoord()>=Maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth))
		{
			return 0;
		}
		if (wallCrashHorizontal(nextPoint, GameEngine.bulletWidth)){            //垂直墙碰撞检测
			flipBulletH();
			nextPoint = new Point(this.position.getxCoord()
					+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle2))),
					this.position.getyCoord());
		}else if (wallCrashVertical(nextPoint, GameEngine.bulletWidth)){        //同上
			flipBulletV();
			nextPoint = new Point(this.position.getxCoord(),
					this.position.getyCoord()
					- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle2))));
		}else if(cornerCrash(nextPoint,GameEngine.bulletWidth)){
			if(this.currentYSquare()==(int)this.currentYSquare()){
				flipBulletH();
				nextPoint = new Point(this.position.getxCoord()
						+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle2))),
						this.position.getyCoord());
			}else{
				flipBulletV();
				nextPoint = new Point(this.position.getxCoord(),
						this.position.getyCoord()
						- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle2))));
			}
		}
		if(nextPoint.getxCoord()>=Maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth)||
				nextPoint.getyCoord()>=Maze.getGridWidth()*(GameEngine.squareWidth+GameEngine.wallWidth))
		{
			return 0;
		}
		this.position=nextPoint;
		return 1;
	}
	private boolean reduceTimer(){                                                //子弹时间计时
		timer--;
		if (timer==0){
			return false;
		}
		return true;
	}
	private void flipBulletV(){
		this.angle2=(-this.angle2) + 360;
	}
	
	private void flipBulletH(){
		if (this.angle2>180){
			this.angle2 = -this.angle2+540;
		}else{
			this.angle2=-this.angle2+180;
		}
	}	
	public int currentXSquare() {                            //返回当前方块是第几个
		return (int)(this.position.getxCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	public int currentYSquare() {                            //跟上面一样
		return (int)(this.position.getyCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}


	private boolean wallCrashVertical(Point p, int w){                             //水平墙碰撞检测             point中也有一样的方法
		w=w/2;
		if (this.currentYSquare()>Maze.getGridWidth()-1||this.currentXSquare()>Maze.getGridWidth()-1
				||this.currentYSquare()<0||this.currentXSquare()<0){
			return false;
		}
		boolean byLeftWall = Maze.isWallLeft(this.currentXSquare(), this.currentYSquare());
		boolean inLeftWall = (p.getxCoord()-w<=
				(this.currentXSquare())*GameEngine.squareWidth+(this.currentXSquare()+1)*GameEngine.wallWidth);
		boolean byRightWall = Maze.isWallRight(this.currentXSquare(), this.currentYSquare());
		boolean inRightWall = (p.getxCoord()+w>=
				(this.currentXSquare()+1)*GameEngine.squareWidth+(this.currentXSquare()+1)*GameEngine.wallWidth);
		return ((byLeftWall&&inLeftWall)||(byRightWall&&inRightWall));
	}
	private boolean wallCrashHorizontal(Point p, int w){                             //同上
		w=w/2;
		if (this.currentYSquare()>Maze.getGridWidth()-1||this.currentXSquare()>Maze.getGridWidth()-1
				||this.currentYSquare()<0||this.currentXSquare()<0){
			return false;
		}
		boolean byTopWall = Maze.isWallAbove(this.currentXSquare(), this.currentYSquare());
		boolean inTopWall = (p.getyCoord()-w<=
				(this.currentYSquare())*GameEngine.squareWidth+(this.currentYSquare()+1)*GameEngine.wallWidth);
		boolean byBottomWall = Maze.isWallBelow( this.currentXSquare(),  this.currentYSquare());
		boolean inBottomWall = (p.getyCoord()+w>=
				(this.currentYSquare()+1)*GameEngine.squareWidth+(this.currentYSquare()+1)*GameEngine.wallWidth);
		return ((byTopWall&&inTopWall)||(byBottomWall&&inBottomWall));
	}
	private boolean cornerCrash(Point p, int w){                                            //同上
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
		boolean isWallInCorner=Maze.isWallAbove(x,y)||Maze.isWallLeft(x,y);
		if(x>0){
			isWallInCorner=isWallInCorner||Maze.isWallAbove(x-1,y);
		}
		if(y>0){
			isWallInCorner=isWallInCorner||Maze.isWallLeft(x,y-1);
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


