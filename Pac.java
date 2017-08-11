package pac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

/**
 *
 * @author snord
 */
public class Pac extends Application {

    double movementSpeedX = 6.5; //7.5
    double movementSpeedY = 6.9; //8
    double bulletSpeed = 5;
    double ghostSpeed = 2;
    int width = 700;
    int height = 700;
    int squareSize = 50;
    int offset = 1;
    int frame = 0;
    int playerMovement = 0;
    int gameDiff = 100;
    static int boardSize = 14;
    static char[][] board = new char[boardSize][boardSize];
    static PacMan pacman = new PacMan(0, 0);
    int mouth = 240;
    int rotatePac = 40;
    boolean motion = true;
    Canvas canvas = new Canvas(width * 1.2, height * 1.2);
    double bulletSize = 8;
    boolean dwalk = false;
    boolean wwalk = false;
    boolean awalk = false;
    boolean swalk = false;
    boolean playerShoot = false;
    boolean playerHasDied = false;
    boolean checkPointOne = false;
    static Node[][] nodeBoard = new Node[boardSize][boardSize];
    static Ghosts ghost = new Ghosts(6 * 51, 5 * 1, 6, 5);
    static ArrayList<Ghosts> arrayGhost = new ArrayList<Ghosts>();

    //Helps pacman eat the ghosts!
    static boolean powerUp = false;
    static int powerUpTime = 1;

    static int numberOfGhosts = 4;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        VBox vbox = new VBox();
        vbox.setTranslateX(-60);
        root.setRight(vbox);
        Scene scene = new Scene(root, width * 1.4, height * 1.2);

        canvas.setOnMouseMoved(e
                -> {
            int x = ((int) (e.getX())) / (squareSize + offset);
            int y = ((int) (e.getY())) / (squareSize + offset);
            // System.out.println("X: " + e.getX() + " Y: " + e.getY() + " " + y + " " + x);
        });

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) {
                dwalk = false;
                wwalk = false;
                awalk = true;
                swalk = false;
                rotatePac = 210;
            }
            if (e.getCode() == KeyCode.D) {
                dwalk = true;
                wwalk = false;
                awalk = false;
                swalk = false;
                rotatePac = 40;
            }
            if (e.getCode() == KeyCode.S) {
                dwalk = false;
                wwalk = false;
                awalk = false;
                swalk = true;
                rotatePac = 300;
            }
            if (e.getCode() == KeyCode.W) {
                dwalk = false;
                wwalk = true;
                awalk = false;
                swalk = false;
                rotatePac = 120;
            }

        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A) {
                dwalk = false;
                wwalk = false;
                awalk = false;
                swalk = false;
            }
            if (e.getCode() == KeyCode.D) {
                dwalk = false;
                wwalk = false;
                awalk = false;
                swalk = false;
            }
            if (e.getCode() == KeyCode.W) {
                dwalk = false;
                wwalk = false;
                awalk = false;
                swalk = false;
            }
            if (e.getCode() == KeyCode.S) {
                dwalk = false;
                wwalk = false;
                awalk = false;
                swalk = false;
            }
        });

        scene.setOnMouseClicked(e -> {
            int x = ((int) (e.getX())) / (squareSize + offset);
            int y = ((int) (e.getY())) / (squareSize + offset);
            System.out.println("x: " + y + " y:" + (x - 1));
        });

        new AnimationTimer() {

            @Override
            public void handle(long now) {
                if(pacman.lives >0 && !pacWin()){
                updatePacSpot();
                //the different numbers being muiltiplied to the movementSpeed are applying offsets for collision! 
                if (dwalk && (pacman.x + movementSpeedX < width) && checkIfValidMove(movementSpeedX * 6, 0)) {
                    pacman.movePlayerX(movementSpeedX);

                } else if (wwalk && checkIfValidMove(0, -movementSpeedY * 2.0) && pacman.y - movementSpeedY >= 0) {
                    pacman.movePlayerY(-movementSpeedY);

                } else if (swalk && checkIfValidMove(0, movementSpeedY * 6)) {
                    pacman.movePlayerY(movementSpeedY);

                } else if (awalk && (pacman.x + movementSpeedX >= 0) && checkIfValidMove(-movementSpeedX, 0)) {
                    pacman.movePlayerX(-movementSpeedX);
                }

                moveGhosts();
                if (frame == 200) {
                    //RELEASE THE GHOSTS :O
                    board[5][5] = 'e';
                    board[8][8] = 'e';
                    nodeBoard[8][8] = new Node(8, 8);
                    nodeBoard[5][5] = new Node(5, 5);

                }
                drawCanvas();
                frame++;

                for (int i = 0; i < arrayGhost.size(); i++) {
                    if (arrayGhost.get(i).arrX == 5 && arrayGhost.get(i).arrY == 6 && arrayGhost.get(i).eaten == true) {
                        arrayGhost.get(i).eaten = false;
                    }
                }
                eatGhosts();

                if (powerUpTime % 200 == 0) {
                    powerUp = false;
                }
                if (powerUp == true) {
                    powerUpTime++;
                } else {
                    powerUpTime = 1;
                }
            }
            }
        }.start();

        primaryStage.setScene(scene);
        primaryStage.show();

    }
public boolean pacWin()
{
    for(int i =0; i < 14;i++)
    {
        for(int j =0; j<14;j++)
        {
            if(nodeBoard[i][j] != null)
            {
                if(nodeBoard[i][j].searched == false)
                {
                    return false;
                }
            }
        }
    }
    return true;
}
    public void eatGhosts() {

        if (powerUp) {
            for (int i = 0; i < arrayGhost.size(); i++) {
                if (arrayGhost.get(i).arrX == pacman.arrX && arrayGhost.get(i).arrY == pacman.arrY) {
                    arrayGhost.get(i).eaten = true;
                }
            }
        } else {
            for (int i = 0; i < arrayGhost.size(); i++) {
                if (arrayGhost.get(i).arrX == pacman.arrX && arrayGhost.get(i).arrY == pacman.arrY) {
                    if(arrayGhost.get(i).eaten == false)
                    {
                    System.out.println("YOU HAVE BEEN KILLED!");
                    if (pacman.lives > 0) {
                        pacman.lives--;
                        pacman.arrX = 0;
                        pacman.arrY = 0;
                        pacman.x = 0;
                        pacman.y = 0;

                    }
                }
                }
            }
        }
    }
// this function will be incharge of moving the ghosts around the map so that they can find pacman!

    public void moveGhosts() {
        for (int i = 0; i < numberOfGhosts; i++) {

            int distanceX = arrayGhost.get(i).arrX - pacman.arrX;
            int distanceY = arrayGhost.get(i).arrY - pacman.arrY;
            double totalDis = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            if (arrayGhost.get(i).eaten == false) {
                if (totalDis <= 6) {
                    Node nextSpot = aStar(nodeBoard[pacman.arrX][pacman.arrY], nodeBoard[arrayGhost.get(i).arrX][arrayGhost.get(i).arrY]);

                    if (nextSpot != null) {
                        if (arrayGhost.get(i).arrX - 1 == nextSpot.x && arrayGhost.get(i).arrY == nextSpot.y) {
                            arrayGhost.get(i).moveGhost(0, -ghostSpeed);
                        } else if (arrayGhost.get(i).arrX + 1 == nextSpot.x && arrayGhost.get(i).arrY == nextSpot.y) {
                            arrayGhost.get(i).moveGhost(0, ghostSpeed);

                        } else if (arrayGhost.get(i).arrX == nextSpot.x && arrayGhost.get(i).arrY + 1 == nextSpot.y) {
                            arrayGhost.get(i).moveGhost(ghostSpeed, 0);

                        } else if (arrayGhost.get(i).arrX == nextSpot.x && arrayGhost.get(i).arrY - 1 == nextSpot.y) {
                            arrayGhost.get(i).moveGhost(-ghostSpeed, 0);

                        }
                        arrayGhost.get(i).arrX = ((int) (arrayGhost.get(i).y)) / (squareSize + offset);
                        arrayGhost.get(i).arrY = ((int) (arrayGhost.get(i).x)) / (squareSize + offset);
                    }

                } else {
                    lostThePlayer(arrayGhost.get(i));
                }
            } else {
                Node nextSpot = aStar(nodeBoard[6][5], nodeBoard[arrayGhost.get(i).arrX][arrayGhost.get(i).arrY]);
                if (nextSpot == null) {
                    arrayGhost.get(i).eaten = false;
                }
//                System.out.println(nextSpot.x+" "+ nextSpot.y +" GHOST: "+arrayGhost.get(i).arrX+" "+arrayGhost.get(i).arrY );
                if (nextSpot != null) {
                    if (arrayGhost.get(i).arrX - 1 == nextSpot.x && arrayGhost.get(i).arrY == nextSpot.y) {
                        arrayGhost.get(i).moveGhost(0, -ghostSpeed);
                    } else if (arrayGhost.get(i).arrX + 1 == nextSpot.x && arrayGhost.get(i).arrY == nextSpot.y) {
                        arrayGhost.get(i).moveGhost(0, ghostSpeed);

                    } else if (arrayGhost.get(i).arrX == nextSpot.x && arrayGhost.get(i).arrY + 1 == nextSpot.y) {
                        arrayGhost.get(i).moveGhost(ghostSpeed, 0);

                    } else if (arrayGhost.get(i).arrX == nextSpot.x && arrayGhost.get(i).arrY - 1 == nextSpot.y) {
                        arrayGhost.get(i).moveGhost(-ghostSpeed, 0);

                    }
                    arrayGhost.get(i).arrX = ((int) (arrayGhost.get(i).y)) / (squareSize + offset);
                    arrayGhost.get(i).arrY = ((int) (arrayGhost.get(i).x)) / (squareSize + offset);
                }
                //GHOST HAS BEEN EATEN GO TO THE START!
            }
        }
        // System.out.println("POINTX: " + arrayGhost.get(0).x + " POINTY: " + arrayGhost.get(0).y + " xARG: " + arrayGhost.get(0).arrX + " yARG: " + arrayGhost.get(0).arrY);
    }

    /**
     *
     * @param ghost the current ghost will follow a random path until he sees
     * pacman again!
     */
    public void lostThePlayer(Ghosts ghost) {
        if (ghost.randomX == 5 && ghost.randomY == 6) {
            int x = (int) (Math.random() * 14);
            int y = (int) (Math.random() * 14);
            if (nodeBoard[x][y] != null) {
                ghost.setRandom(x, y);

            }
        } else {
            if (ghost.randomX == ghost.arrX && ghost.randomY == ghost.arrY) {
                ghost.randomX = 5;
                ghost.randomY = 6;
            } else {
                Node nextSpot = aStar(nodeBoard[ghost.randomX][ghost.randomY], nodeBoard[ghost.arrX][ghost.arrY]);
                //System.out.println("NEXT SPOT   X: " + nextSpot.x + " Y: " + nextSpot.y);
                if (nextSpot != null) {
                    if (ghost.arrX - 1 == nextSpot.x && ghost.arrY == nextSpot.y) {
                        ghost.moveGhost(0, -ghostSpeed);
                    } else if (ghost.arrX + 1 == nextSpot.x && ghost.arrY == nextSpot.y) {
                        ghost.moveGhost(0, ghostSpeed);

                    } else if (ghost.arrX == nextSpot.x && ghost.arrY + 1 == nextSpot.y) {
                        ghost.moveGhost(ghostSpeed, 0);

                    } else if (ghost.arrX == nextSpot.x && ghost.arrY - 1 == nextSpot.y) {
                        ghost.moveGhost(-ghostSpeed, 0);

                    }
                    ghost.arrX = ((int) (ghost.y)) / (squareSize + offset);
                    ghost.arrY = ((int) (ghost.x)) / (squareSize + offset);
                }
            }
        }

    }

    public static void buildNodeBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] != 'w') {
                    nodeBoard[i][j] = new Node(i, j);
                } else {
                    nodeBoard[i][j] = null;
                }
            }
        }
    }

    public static void makeGhosts() {

        Ghosts ghost = new Ghosts(272.0, 320.0, 6, 5);
        arrayGhost.add(ghost);
        Ghosts ghost2 = new Ghosts(326.0, 373.0, 7, 6);
        arrayGhost.add(ghost2);
        Ghosts ghost3 = new Ghosts(371.0, 326.0, 6, 7);
        arrayGhost.add(ghost3);
        Ghosts ghost4 = new Ghosts(412.0, 377.0, 7, 8);
        arrayGhost.add(ghost4);

    }

    public boolean checkIfValidMove(double moveX, double moveY) {

        int x = ((int) (pacman.x + moveX)) / (squareSize + offset);
        int y = ((int) (pacman.y + moveY)) / (squareSize + offset);
        //System.out.println("X: " + x + " Y:" + y + " BOARD: " + board[x][y]);
        if (x == boardSize || y == boardSize || x == -1 || y == -1) {
            return false;
        }
        if (board[y][x] == 'w') {
            return false;
        }

        //DETECTS IF YOU ALREADY EXPLORED GROUND AND YOU COLLECT POINTS
        if (((x == 13 && y == 0) || (x == 13 && y == 13) || (x == 0 && y == 13)) && nodeBoard[y][x].searched == false) {
            powerUp = true;
        }
        nodeBoard[y][x].searched = true;
        pacman.points += nodeBoard[y][x].point;
        nodeBoard[y][x].updatePoint();
        return true;
    }

    public void updatePacSpot() {
        //    System.out.println("X: " + ((int) pacman.x) / (squareSize + offset) + " Y: " + ((int) pacman.y) / (squareSize + offset));
        pacman.arraySpots(((int) pacman.x) / (squareSize + offset), ((int) pacman.y) / (squareSize + offset));
    }

    public static Node aStar(Node start, Node end) {

        if (end == null) {
            System.out.println("THE START WAS NULL");
        }
        if (start != null) {

            PriorityQueue<Node> frontier = new PriorityQueue<>();
            frontier.add(start);
            Map<Node, Node> came_from = new HashMap<>();
            Map<Node, Double> cost_so_far = new HashMap<>();

            cost_so_far.put(start, 0.0);
            came_from.put(start, null);
            double new_cost;

            Node current = null;
            LinkedList<Node> neighbors = new LinkedList<>();
            double dist;
            while (!frontier.isEmpty()) {
                current = frontier.poll();
                // if (current.x == end.x && current.y == end.y) {
                if (current == end) {
                    // System.out.println("A* found a path.");
                    break;
                } else {
                    //System.out.println("NO PATH FOUND! "+ frontier.size());
                }
                neighbors.removeAll(neighbors);

                if (current.x + 1 < boardSize) {
                    if (nodeBoard[current.x + 1][current.y] != null) {
                        neighbors.add(nodeBoard[current.x + 1][current.y]);
                    }
                }
                if (current.x - 1 != -1) {
                    if (nodeBoard[current.x - 1][current.y] != null) {
                        neighbors.add(nodeBoard[current.x - 1][current.y]);
                    }
                }
                if (current.y + 1 < boardSize) {
                    if (nodeBoard[current.x][current.y + 1] != null) {
                        neighbors.add(nodeBoard[current.x][current.y + 1]);
                    }
                }
                if (current.y - 1 >= 0) {
                    if (nodeBoard[current.x][current.y - 1] != null) {
                        neighbors.add(nodeBoard[current.x][current.y - 1]);
                    }
                }

                for (Node neighbor : neighbors) {
                    // Cost of moving is 1, no matter the direction, unless going uphill, in which case cost is 2.
                    dist = 1;

                    new_cost = cost_so_far.get(current) + dist;
                    if (!came_from.containsKey(neighbor) || new_cost < cost_so_far.get(neighbor)) {
                        cost_so_far.put(neighbor, new_cost);
                        neighbor.priority = new_cost + heuristic(end, neighbor);
                        frontier.add(neighbor);
                        // System.out.println(current.x+" "+ current.y+" NEIGHBOR:"+neighbor.x+" "+ neighbor.y);
                        came_from.put(neighbor, current); // Check if this is giving a bad node
                        if (Math.abs(neighbor.x - current.x) > 1 || Math.abs(neighbor.y - current.y) > 1) {
                            System.out.println("AAUAHUhughg A* screwed up");
                        }
                    }
                }

            }

            Node steps = nodeBoard[arrayGhost.get(0).arrX][arrayGhost.get(0).arrY];
            while (steps != null) {
                steps = came_from.get(steps);
            }

            return came_from.get(end);
        }
        return end;
    }

    private static double heuristic(Node a, Node b) {
        if (a == null || b == null) {
            return 0;
        } else {
            return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
        }
    }
//THIS METHOD IS INCHARGE OF DRAWING THE WHOLE GUI BOARD!

    public void drawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width + 20, height + 20);

        for (int i = 0, x = 0; i < width; i += squareSize + offset, x++) {
            for (int j = 0, y = 0; j < height; j += squareSize + offset, y++) {
                switch (board[y][x]) {
                    case 'e':
                        gc.setFill(Color.BLACK);
                        break;
                    case 'w':
                        gc.setFill(Color.BLUE);
                        break;
                    case 'b':
                        gc.setFill(Color.BLUE);

                    default:
                        break;
                }

                gc.fillRect(i, j, squareSize, squareSize);
            }
        }

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (nodeBoard[i][j] != null) {
                    if (nodeBoard[i][j].searched == false) {
                        gc.setFill(Color.WHITE);
                        if (((i == 13 && j == 0) || (i == 13 && j == 13) || (i == 0 && j == 13))) {
                            gc.fillOval(j * 51 + 20, i * 51 + 20, 14, 14);

                        } else {
                            gc.fillOval(j * 51 + 20, i * 51 + 20, 7, 7);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < arrayGhost.size(); i++) {

            if (i == 0) {
                if (powerUp) {
                    gc.setFill(Color.BLUE);

                } else {
                    gc.setFill(Color.RED);
                }
                if (arrayGhost.get(i).eaten == false) {
                    gc.fillArc(arrayGhost.get(i).x - 15, arrayGhost.get(i).y - 10, 35, 75, 0, 180, ArcType.ROUND);
                    gc.setFill(Color.GREEN);

                }
                gc.setFill(Color.WHITE);
                gc.fillOval(arrayGhost.get(i).x - 5, arrayGhost.get(i).y, 5, 5);
                gc.fillOval(arrayGhost.get(i).x + 5, arrayGhost.get(i).y, 5, 5);
            } else if (i == 1) {
                if (powerUp) {
                    gc.setFill(Color.BLUE);

                } else {
                    gc.setFill(Color.GREEN);
                }
                if (arrayGhost.get(i).eaten == false) {
                    gc.fillArc(arrayGhost.get(i).x - 15, arrayGhost.get(i).y - 10, 35, 75, 0, 180, ArcType.ROUND);
                }
                gc.setFill(Color.WHITE);
                gc.fillOval(arrayGhost.get(i).x - 5, arrayGhost.get(i).y, 5, 5);
                gc.fillOval(arrayGhost.get(i).x + 5, arrayGhost.get(i).y, 5, 5);
            } else if (i == 2) {

                if (powerUp) {
                    gc.setFill(Color.BLUE);

                } else {
                    gc.setFill(Color.ORANGE);
                }
                if (arrayGhost.get(i).eaten == false) {
                    gc.fillArc(arrayGhost.get(i).x - 15, arrayGhost.get(i).y - 10, 35, 75, 0, 180, ArcType.ROUND);
                }
                gc.setFill(Color.WHITE);
                gc.fillOval(arrayGhost.get(i).x - 5, arrayGhost.get(i).y, 5, 5);
                gc.fillOval(arrayGhost.get(i).x + 5, arrayGhost.get(i).y, 5, 5);
            } else if (i == 3) {
                if (powerUp) {
                    gc.setFill(Color.BLUE);

                } else {
                    gc.setFill(Color.PINK);
                }
                if (arrayGhost.get(i).eaten == false) {
                    gc.fillArc(arrayGhost.get(i).x - 15, arrayGhost.get(i).y - 10, 35, 75, 0, 180, ArcType.ROUND);
                }
                gc.setFill(Color.WHITE);
                gc.fillOval(arrayGhost.get(i).x - 5, arrayGhost.get(i).y, 5, 5);
                gc.fillOval(arrayGhost.get(i).x + 5, arrayGhost.get(i).y, 5, 5);
            }

        }

        //THIS DRAWS PACMAN!
        gc.setFill(Color.YELLOW);

        gc.fillArc(pacman.x, pacman.y, 40, 40, rotatePac, mouth, ArcType.ROUND);

        if (mouth == 240) {
            motion = true;
        } else if (mouth == 360) {
            motion = false;
        }

        if (motion) {
            mouth += 10;
        } else {
            mouth -= 10;
        }

    }

    public static void buildBlankBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = 'e';
            }
        }
        //|-
        board[3][2] = 'w';
        board[1][1] = 'w';
        board[2][1] = 'w';
        board[3][1] = 'w';

        //L
        board[10][2] = 'w';
        board[10][1] = 'w';
        board[11][1] = 'w';
        board[12][1] = 'w';

        ///1
        board[3][11] = 'w';
        board[1][12] = 'w';
        board[2][12] = 'w';
        board[3][12] = 'w';

        //_|
        board[10][11] = 'w';
        board[10][12] = 'w';
        board[11][12] = 'w';
        board[12][12] = 'w';

        board[4][4] = 'w';
        board[5][4] = 'w';
        board[6][4] = 'w';
        board[7][4] = 'w';
        board[5][2] = 'w';
        board[8][4] = 'w';
        board[9][4] = 'w';
        board[8][2] = 'w';
        board[5][1] = 'w';
        board[8][1] = 'w';
        board[6][1] = 'w';
        board[6][2] = 'w';

        board[4][9] = 'w';
        board[5][9] = 'w';
        board[6][9] = 'w';
        board[7][9] = 'w';
        board[8][9] = 'w';
        board[9][9] = 'w';

        board[5][11] = 'w';
        board[5][12] = 'w';
        board[6][11] = 'w';
        board[6][12] = 'w';
        board[8][11] = 'w';
        board[8][12] = 'w';

        board[1][4] = 'w';
        board[1][5] = 'w';
        board[1][6] = 'w';
        board[1][7] = 'w';
        board[1][8] = 'w';
        board[1][9] = 'w';
        board[2][4] = 'w';
        board[2][9] = 'w';
        board[1][3] = 'w';
        board[1][10] = 'w';

        board[3][6] = 'w';
        //board[4][7] = 'w';
        // board[4][6] = 'w';
        board[3][7] = 'w';

        board[12][3] = 'w';
        board[11][4] = 'w';
        board[12][4] = 'w';
        board[12][5] = 'w';
        board[12][6] = 'w';
        board[12][7] = 'w';
        board[12][8] = 'w';
        board[12][9] = 'w';
        board[12][10] = 'w';
        board[11][9] = 'w';

        // board[5][5] = 'w';
        board[5][6] = 'w';
        board[5][7] = 'w';
        board[5][8] = 'w';
        board[8][6] = 'w';
        board[8][7] = 'w';
        board[8][5] = 'w';
        // board[8][8] = 'w';
        board[10][7] = 'w';
        board[10][6] = 'w';

        board[5][5] = 'w';
        board[8][8] = 'w';

    }

    public static void main(String[] args) {
        buildBlankBoard();
        buildNodeBoard();
        makeGhosts();
        //    Node nextSpot = aStar(nodeBoard[pacman.arrX][pacman.arrY], nodeBoard[arrayGhost.get(0).arrX][arrayGhost.get(0).arrY]);
        launch(args);
    }

}
