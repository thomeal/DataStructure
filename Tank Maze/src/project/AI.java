package project;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class vertex{
    public boolean known;
    public Square pre;
    public Square current;
    public int distance;
    public vertex(boolean known,Square pre,Square current,int distance){
        this.known=known;
        this.pre=pre;
        this.current=current;
        this.distance=distance;
    }
    public vertex(){
        this.known=false;
        this.pre=null;
        this.current=null;
        this.distance=-1;
    }
}
public class AI extends Player {

    public Stack<Square> getTrace() {
        return trace;
    }
    private Stack<Square> trace=new Stack<Square>();
    private vertex[][] BFSList = new vertex[Maze.getGridWidth()][Maze.getGridWidth()];
    private int col;
    private int times;
    int angle;
    private Point p1,p2,p3;
    private AIBullet model;
    public AI(int ID,double x, double y, GameEngine e) {
        super(ID, x, y, e);
    	this.model=new AIBullet(coordinates, direction, engine);                          //子弹运算模型初始化
		this.p1=engine.Players.get(0).getCoordinates();                                   //载入玩家1、2坐标
		this.p2=engine.Players.get(1).getCoordinates();
		this.p3=this.coordinates;
        
    }
//    寻找最短路径
    public void FindPath(Square s1,Square s2){
//        while(currentYSquare()==-1||currentXSquare()==-1)
//            goForward();
        int xs=currentXSquare();
        int ys=currentYSquare();
        int xe,ye;
        int xt,yt;
        Queue<vertex> nextdist = new LinkedList<vertex>();
        for(int i=0;i<Maze.getGridWidth();i++){
            for(int j=0;j<Maze.getGridWidth();j++){
                BFSList[i][j]=new vertex();
            }
        }
        nextdist.add(new vertex(true,new Square(xs,ys),new Square(xs,ys),0));
        BFSList[xs][ys]= nextdist.peek();
        for(int i = 0; i<Maze.getGridWidth()*Maze.getGridWidth(); i++){

            xt=nextdist.peek().current.getxCoord();
            yt=nextdist.peek().current.getyCoord();
            if(xt-1>-1&&!BFSList[xt-1][yt].known&&!Maze.isWallLeft(xt, yt)) {
                BFSList[xt-1][yt]=new vertex(true, nextdist.peek().current, new Square(xt - 1, yt), nextdist.peek().distance + 1);
                nextdist.offer(BFSList[xt-1][yt]);
            }
            if(xt+1<Maze.getGridWidth()&&!BFSList[xt+1][yt].known&&!Maze.isWallRight(xt, yt)) {
                BFSList[xt + 1][yt] = new vertex(true, nextdist.peek().current, new Square(xt + 1, yt), nextdist.peek().distance + 1);
                nextdist.offer(BFSList[xt + 1][yt]);
            }
            if(yt-1>-1&&!BFSList[xt][yt-1].known&&!Maze.isWallAbove(xt,yt)) {
                BFSList[xt][yt-1] = new vertex(true, nextdist.peek().current, new Square(xt, yt-1), nextdist.peek().distance + 1);
                nextdist.offer(BFSList[xt][yt-1]);
            }
            if(yt+1<Maze.getGridWidth()&&!BFSList[xt][yt+1].known&&!Maze.isWallBelow(xt, yt)) {
                BFSList[xt][yt+1] = new vertex(true, nextdist.peek().current, new Square(xt, yt+1), nextdist.peek().distance + 1);
                nextdist.offer(BFSList[xt][yt+1]);
            }

            if(nextdist.peek().current==s1&&!GameEngine.player1_dead)
                break;
            if(nextdist.peek().current==s2&&!GameEngine.player2_dead)
                break;

            nextdist.poll();
        }

        trace.clear();

        if((BFSList[s1.getxCoord()][s1.getyCoord()].distance< BFSList[s2.getxCoord()][s2.getyCoord()].distance||GameEngine.player2_dead)&&!GameEngine.player1_dead){
            xe=s1.getxCoord();
            ye=s1.getyCoord();
        }
        else{
            xe=s2.getxCoord();
            ye=s2.getyCoord();
        }
        while (BFSList[xe][ye].distance!=0){
            trace.push(new Square(xe,ye));
            xe= BFSList[trace.peek().getxCoord()][trace.peek().getyCoord()].pre.getxCoord();
            ye= BFSList[trace.peek().getxCoord()][trace.peek().getyCoord()].pre.getyCoord();
        }
    }

    public void move(){
        if(currentXSquare()==-1||currentYSquare()==-1)
            goForward();
        if(trace.empty())
            return;
        Point nextPoint = new Point(this.coordinates.getxCoord() + (forwardSpeed * Math.cos(Math.toRadians(90-this.direction))), this.coordinates.getyCoord() - (forwardSpeed * Math.sin(Math.toRadians(90-this.direction))));
        if(col>0||cornerCrash(nextPoint,GameEngine.tankWidth)) {
            if(col==0){
                times++;
                col=20;
            }
//            if(Maze.isWallRight(currentXSquare(),currentYSquare())||Maze.isWallAbove(currentXSquare(),currentYSquare()))
//                turnLeft();
//            else
            if(times%4==0) {
                turnRight();
                reverse();
            }
            else if(times%4==1) {
                turnLeft();
                reverse();
            }
            else if(times%4==2) {
                turnRight();
                goForward();
            }
            else{
                turnLeft();
                goForward();
            }
            col--;
        }
        else {
            if(trace.peek().getxCoord()<currentXSquare()){
                    if(getDirection()<=90||getDirection()>270){
                    turnLeft();
                }
                else if(getDirection()>90&&getDirection()<270){
                    turnRight();
                }
                goForward();
            }
            else if(trace.peek().getxCoord()>currentXSquare()){
                if(getDirection()<90||getDirection()>=270){
                    turnRight();
                }
                else if(getDirection()>90&&getDirection()<270){
                    turnLeft();
                }
                goForward();
            }
            else if(trace.peek().getyCoord()>currentYSquare()){
                if(getDirection()<=180&&getDirection()>0){
                    turnRight();
                }
                else if(getDirection()>180){
                    turnLeft();
                }
                goForward();
            }
            else if(trace.peek().getyCoord()<currentYSquare()){
                if(getDirection()<180){
                    turnLeft();
                }
                else if(getDirection()>180){
                    turnRight();
                }
                goForward();
            }
            else{
                trace.pop();
            }
        }
    }
    public void shoot1() {
        Point p1=engine.Players.get(0).getCoordinates();
        Point p2=engine.Players.get(1).getCoordinates();
        if(engine.Players.get(0).getCoordinates().equals(this.p1)&&                           //减少计算量，当玩家不运动并且子弹打不到玩家时，ai不作出反应
                engine.Players.get(1).getCoordinates().equals(this.p2)
                &&this.angle==361&&p3.equals(this.coordinates))
        {
        	 System.out.println("Stay");
        }
        else {
        	 System.out.println("Check");
            int angle=model.modelMoveBullet();                                            //子弹模拟运算，返回一个射击方向
            System.out.println("i"+angle);
            this.angle=angle;
            this.p1=p1;                                                               //玩家移动了，重置玩家坐标
            this.p2=p2;
            this.p3=this.coordinates;
            if(angle!=361)
            {
                if(angle==this.direction)
                {
                    shoot();
                }
                else if(angle>this.direction)                                                     //调整ai方向使之与求出方向一致
                {
                    turnRight();
                }
                else if(angle<this.direction)
                {
                    turnLeft();
                }
            }
            model.setModel(this.direction, this.coordinates);                         //重置ai模拟运算模型
        }
    }
}

