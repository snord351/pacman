/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pac;
/**
 *
 * @author snord
 */
public class Node implements Comparable<Node> {

    int x;
    int y;
    double priority;
    boolean searched = false;
    int point = 100;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.priority = 1;
    }

    public void updatePoint()
    {
        this.point = 0;
    }

    
    Node(int x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override // Just to be sure we're overriding the right method.
    public int compareTo(Node pn) {
        return (int) (this.priority - pn.priority);
    }
}
