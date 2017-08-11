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
public class PacMan 
{
    double x;
    double y;
    int arrX;
    int arrY;
    int oldX;
    int oldY;
    int points =0;
    int lives =3;
    public PacMan (double currentx , double currentY)
    {
        this.x = currentx;
        this.y = currentY;
        this.arrX = (int)currentx;
        this.arrY = (int)currentY;
    }
    
    public void arraySpots(int y , int x)
    {
        oldX =  arrX;
        oldY = arrY;
        arrX = x;
        arrY = y;
    }
    public void movePlayerX (double speed)
    {
        x +=speed;
    }
    public void movePlayerY (double speed)
    {
        y += speed;
    }
    
}
