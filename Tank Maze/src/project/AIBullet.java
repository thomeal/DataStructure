package project;
//�ӵ�����ģ��
//���ܣ��ܶԸ��������1��2�Լ�ai����λ���ҵ�һ�����ڻ�����ң��һ���֮ǰ��������Լ��ĽǶ�
public class AIBullet {
    private int timer;                                            //ai��Ҫ����ģ���ʱ��
    private int angle;                                           //ai�Ƕ�
    private int angle1;                                          //���ԽǶ�
    private int angle2;                                           //�ӵ�����Ƕ�
    private Point position;
    private Point positionNow;                                     //��ʵλ��
    private static final double bulletSpeed = 1.5;

    private GameEngine engine;

    public AIBullet( Point position, int angle, GameEngine e) {
        this.angle = angle;                                                            //angle�Ǵ���ĽǶȣ�Ҳ����ai����ĽǶ�
        this.angle1 = angle;                                                            //angle1������ѭ���ĽǶȣ�ÿ��ѭ����һ��ֱ���ҵ�����Ƕ�
        this.angle2 = angle;                                                            //angle2��һ��ģ���ӵ��˶��Ƕ�angle1ÿ�仯һ�Σ�angle2�仯�ö��
        this.position = position;                      //�ӵ���ģ�������е�λ��         //��ʼ��aiλ��
        this.timer = 1000;                                                              //*******����ʱ��100֡,����100֡���ܷ����*********
        this.engine=e;                                                                 //˳��һ�ᣬ���ģ������ʱ�ڲ��߶��ھۣ���Ҫ���׸ı�ĳ��������ֵ
        this.positionNow=position;                         //ai����ʵλ��                         //�����ı����ģ�������timer���ı�֡�������˸�������������滹��һ��
    }                                                                                  //��modelMoveBullet()������Ҳ��Ҫ�ı�
    public void setModel(int angle,Point p)                                             //���������ai��λ���뵱ǰ�Ƕ�
    {
        this.angle = angle;
        this.angle1 = angle;
        this.angle2 = angle;
        this.position = p;
        this.positionNow=p;
        this.timer = 1000;
    }
    private int changeAngle(int angle){                                 //�Ƕ�����1������ȷ���Ƕ���360����
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
        }                                                             //�������һ��Ȧ�����1
        return this.angle1;
    }
    public int modelMoveBullet()
    {
        this.timer=100;
        this.angle1=this.angle;
        System.out.println("Check1");
        while(this.angle1!=this.angle-1&&(this.angle!=0))                                                             //�ж����з���
        {                                                                       //************��������һ���ӵ�����ģ�⣬���Ƕ�Ϊangle1ʱ
        	 System.out.println("Check2");
            while(reduceTimer())                                           //һ�η����ж��ܷ�����
            {
            	int i;
                i=moveBullet();                                              //�ӵ��ƶ�
                if(i==1)
                {
                	if(collisionself()&&this.timer<50)                                      //�ӵ�����50֡���Ϻ������Լ�����ֱ��������һ��ģ��
                    {
                        break;
                    }
                    if(AICollision()&&(this.angle1-this.angle)%3==0)                           //����̹��ֱ�ӷ��ؽǶ�
                    {
                    	 System.out.println("Check41******************");
                        return this.angle1;                                              //�ҵ�һ��ai����ת��ɹ��һ��е��˵ĽǶ�
                    }
                }
                else
                	break;
            }                                                                //**************һ���ӵ�����ģ�����
            this.timer=100;                                                   //ʱ������
            this.angle2=changeAngle(this.angle1);                             //�Ƕȼ�һ
            this.position=this.positionNow;                                    //���������
            System.out.println("Check3");
        }
        System.out.println("Check42*************");
        return 361;                                                              //�����нǶȶ����ܴ��е��ˣ�����361
    }
    private boolean AICollision(){                                                  //�����⣬����������������������
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
    private boolean collision(int i){                                      //�ж��ӵ��ܷ�������������
        if(currentXSquare()==engine.Players.get(i).currentXSquare()&&currentYSquare()==engine.Players.get(i).currentYSquare())
        {
            return true;
        }
        return false;
    }
    private boolean collisionself(){                                   //����
        double distance = Point.distance(this.positionNow,this.position);
        return (distance<(GameEngine.tankWidth/2+GameEngine.bulletWidth/2));
    }
    private int moveBullet(){                                               //һ֡���ƶ�
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
		if (wallCrashHorizontal(nextPoint, GameEngine.bulletWidth)){            //��ֱǽ��ײ���
			flipBulletH();
			nextPoint = new Point(this.position.getxCoord()
					+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle2))),
					this.position.getyCoord());
		}else if (wallCrashVertical(nextPoint, GameEngine.bulletWidth)){        //ͬ��
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
	private boolean reduceTimer(){                                                //�ӵ�ʱ���ʱ
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
	public int currentXSquare() {                            //���ص�ǰ�����ǵڼ���
		return (int)(this.position.getxCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}

	public int currentYSquare() {                            //������һ��
		return (int)(this.position.getyCoord()/(GameEngine.wallWidth+GameEngine.squareWidth));
	}


	private boolean wallCrashVertical(Point p, int w){                             //ˮƽǽ��ײ���             point��Ҳ��һ���ķ���
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
	private boolean wallCrashHorizontal(Point p, int w){                             //ͬ��
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
	private boolean cornerCrash(Point p, int w){                                            //ͬ��
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


