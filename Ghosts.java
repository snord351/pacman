/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pac;
import java.util.HashMap;

/**
 *
 * @author snord
 */
public class Ghosts {

    double x;
    double y;
    int arrX;
    int arrY;
    boolean eaten = false;
    int randomX = 5;
    int randomY = 6;

    public Ghosts(double xx, double yy, int newx, int newy) {
        this.x = xx;
        this.y = yy;
        this.arrX = newx;
        this.arrY = newy;
        HashMap<Node, Node> path = new HashMap();
    }

    public void moveGhost(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void setRandom(int x, int y) {
        this.randomX = x;
        this.randomY = y;
    }
}
