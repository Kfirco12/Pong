package com.example.kfir.pong;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Ball extends GameObject{

    //--------------final declaration-------------------------------
    private int x_velocity = 700;
    //--------------static declaration------------------------------
    //--------------variables declaration---------------------------
    private int speed_x, speed_y;
    private int diameter;
    int count = 0;
    //--------------instances declaration---------------------------
    private RectF rect;
    private Bitmap img;
    //constructor.
    public Ball(Bitmap img, int height, int width,int x, int y)
    {
        super.setX(x);
        super.setY(y);
        super.setHeight(height);
        super.setWidth(width);

        //starting velocity.
        this.speed_y = 800;
        this.speed_x = -400;
        this.diameter = img.getWidth();

        this.img = img;
        rect = new RectF();

    }

    //=============================================================================================
    public int getSpeed_x() {
        return speed_x;
    }

    //---------------------------------------------------------------------------------------------
    public void setSpeed_x(int speed_x) {
        this.speed_x = speed_x;
    }

    //=============================================================================================
    public int getSpeed_y() {
        return speed_y;
    }

    ////---------------------------------------------------------------------------------------------
    public void setSpeed_y(int speed_y) {
        this.speed_y = speed_y;
    }

    //=============================================================================================
    public int getDiameter() {
        return diameter;
    }

    //=============================================================================================
    public Bitmap getImg() {
        return img;
    }

    //=============================================================================================
    public RectF getRect() {
        return rect;
    }

    //=============================================================================================
    public void reverseX(double angle)
    {
        double radians = Math.toRadians(angle);
        setSpeed_x((int)(x_velocity*Math.cos(radians)));
    }

    //---------------------------------------------------------------------------------------------
    public void reverseY()
    {
        setSpeed_y(-1*getSpeed_y());
    }

    //=============================================================================================
    //if the ball stuck.
    public void clearObstacleY(float y){
        setY(y);
    }

    //---------------------------------------------------------------------------------------------
    public void clearObstacleX(float x){
        setX(x);
    }
    //=============================================================================================
    //update the paddle's rect in every moment.
    private void rectUpdate()
    {
       rect.left = getX();
        rect.right = getX()+ diameter;
        rect.top = getY();
        rect.bottom = getY()+diameter;
    }

    //=============================================================================================
    //update the side that the ball needs to move in every moment.
    public void update(long fps)
    {

        if(fps == 0)
            return;
        setX(getX()+(getSpeed_x()/fps));
        setY(getY() + (getSpeed_y()/fps));

        rectUpdate();

    }

    //=============================================================================================
    public void draw(long fps, Canvas canvas)
    {
        canvas.drawBitmap(getImg(), getX(), getY(), null);
    }
}
