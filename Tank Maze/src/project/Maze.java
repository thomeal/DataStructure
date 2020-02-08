package  project;

import java.util.Stack;
import java.util.Vector;

public class Maze {
    private static  int gridWidth = 13;
    private static boolean[][][] walls=new boolean[2][gridWidth+1][gridWidth+1];
    //    walls[0]存储所有水平墙面（按左边的墙面存储）
//    walls[1]存储所有垂直墙面（按上边的墙面存储）
    private static int[][] distance=new int[gridWidth][gridWidth];

    public static int getGridWidth() {
        return gridWidth;
    }
    public static void setGridWidth(int gridWidth) {
        Maze.gridWidth = gridWidth;
    }

    public static boolean isWallAbove(int y,int x) {
        return walls[0][x][y];
    }

    public static boolean isWallBelow(int y,int x) {
        return walls[0][x+1][y];
    }

    public static boolean isWallLeft(int y,int x) {
        return walls[1][x][y];
    }

    public static boolean isWallRight(int y,int x) {
        return walls[1][x][y+1];
    }

    public  Maze(){
        generateMaze();
    }

    private void generateMaze(){
        generateEmptyMaze();
        Path();
//        Randomize();
    }

    private static void generateEmptyMaze(){
//        建立一个全是墙壁的迷宫
        for(int i=0;i<gridWidth+1;i++){
            for(int j=0;j<gridWidth+1;j++){
                walls[0][i][j]=true;
                walls[1][i][j]=true;
            }
        }
//        边界处理
        for(int i=0;i<gridWidth+1;i++){
            walls[0][i][gridWidth]=false;
            walls[1][gridWidth][i]=false;
        }
    }

    private static void Path(){
        int i=0;
        Stack<Square> trace=new Stack<Square>();
//        trace用以存储当前路径
        boolean[][] visited=new boolean[gridWidth][gridWidth];
//        visited用以记录所有已经访问过的节点
        trace.push(new Square((int)(Math.random()*gridWidth),(int)(Math.random()*gridWidth)));
//        随机选取一个点作为起点
        visited[trace.peek().getxCoord()][trace.peek().getyCoord()]=true;
        while(i<gridWidth*gridWidth-1) {
            Square step;
            Vector<Square> unvisited = new Vector<Square>(4);
            int x = trace.peek().getxCoord();
            int y = trace.peek().getyCoord();
//            将周围没有被访问过的节点加入向量
            if (x+1<gridWidth&&!visited[x + 1][y]) unvisited.add(new Square(x + 1, y));
            if (x-1>-1&&!visited[x - 1][y]) unvisited.add(new Square(x - 1, y));
            if (y+1<gridWidth&&!visited[x][y + 1]) unvisited.add(new Square(x, y + 1));
            if (y-1>-1&&!visited[x][y - 1]) unvisited.add(new Square(x, y - 1));
//            从向量中随机选取一个节点（用来达成随机选取方向的目的）
            switch (unvisited.size()) {
                case 1:
                    step = unvisited.get(0);
                    break;
                case 2:
                    step = RandomPath_2(unvisited);
                    break;
                case 3:
                    step = RandomPath_3(unvisited);
                    break;
                case 4:
                    step = RandomPath_4(unvisited);
                    break;
                default:
                    trace.pop();
                    continue;
            }
//            拆除墙壁
            if(step.getxCoord()!=x){
                if(step.getxCoord()>x)
                    walls[0][x+1][y]=false;
                else
                    walls[0][x][y]=false;
            }
            else{
                if(step.getyCoord()>y)
                    walls[1][x][y+1]=false;
                else
                    walls[1][x][y]=false;
            }

            i++;
            trace.push(step);
            visited[step.getxCoord()][step.getyCoord()]=true;
        }
    }

    private static Square RandomPath_2(Vector<Square> unvisited){
        int ran=(int)(Math.random()*2);
        switch(ran){
            case 0:
                return unvisited.get(0);
            case 1:
                return unvisited.get(1);
        }
        return null;
    }

    private static Square RandomPath_3(Vector<Square> unvisited){
        int ran=(int)(Math.random()*3);
        switch(ran){
            case 0:
                return unvisited.get(0);
            case 1:
                return unvisited.get(1);
            case 2:
                return unvisited.get(2);
        }
        return null;
    }

    private static Square RandomPath_4(Vector<Square> unvisited){
        int ran=(int)(Math.random()*4);
        switch(ran){
            case 0:
                return unvisited.get(0);
            case 1:
                return unvisited.get(1);
            case 2:
                return unvisited.get(2);
            case 3:
                return unvisited.get(3);
        }
        return null;
    }

    private void Randomize(){
//        将生成的地图进一步处理
        int x,y,lim1=0,lim2=0,lim=gridWidth/3;
        for(x=1;x<gridWidth;x++) {
            for (y = 1; y < gridWidth; y++) {
                int i = (int) (gridWidth * Math.random());
                int j = (int) (gridWidth * Math.random());
                if (i == 0 && isWallBelow(j, i) && !isWallRight(j, i) && !isWallLeft(j, i)) {
                    walls[0][i + 1][j] = false;
                    lim2++;
                }
                else if (i == gridWidth - 1 && isWallAbove(j, i) && !isWallRight(j, i) && !isWallLeft(j, i)) {
                    walls[0][i][j] = false;
                    lim2++;
                }
                else if (j == 0 && !isWallAbove(j, i) && !isWallBelow(j, i) && isWallRight(j, i)) {
                    walls[1][i][j + 1] = false;
                    lim1++;
                }
                else if (j == gridWidth - 1 && !isWallAbove(j, i) && !isWallBelow(j, i) && isWallLeft(j, i)) {
                    walls[1][i][j] = false;
                    lim1++;
                }
                else if (i > 0 && i < gridWidth - 1 && y > 0 && y < gridWidth - 1) {
                     if (isWallLeft(j, i) && isWallRight(j, i) && !(lim1 > 1)) {
                        if (lim1 > 0)
                            walls[0][i][j] = false;
                        else
                            walls[0][i + 1][j] = false;
                        lim1++;
                     }
                     else if (isWallAbove(j, i) && isWallBelow(j, i) && !(lim2 > 1)) {
                        if (lim2 > 0)
                            walls[1][i][j] = false;
                        else
                            walls[1][i][j + 1] = false;
                        lim2++;
                    }
                    if (lim1 > lim && lim2 > lim)
                        break;
                }
                if (lim1 > lim && lim2 > lim)
                    break;
            }
        }
    }
}