package com.example.kfir.pong;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Paddle extends GameObject {
    //--------------final declaration-------------------------------
    private final float speed = 550;    //speed of the bat in pixels.
    //--------------static declaration------------------------------
    public static int RIGHT = 2;
    public static int LEFT = 1;
    public static int STOP = 0;
    //--------------variables declaration---------------------------
    private boolean player;
    private int cond; //left/right movement.
    //--------------instances declaration---------------------------
    private RectF rect;
    private Bitmap img;

    //constructor.
    public Paddle(Bitmap img, int height, int width, int x, int y, boolean player) {
        //-----variables  initialize-----//
        super.setX(x);
        super.setY(y);
        super.setWidth(width);
        super.setHeight(height);
        this.player = player;
        //-----instances initialize-----//
        rect = new RectF();
        this.img = img;
    }

    //=============================================================================================
    //update the rect position.
    private void rectUpdate() {
        rect.left = getX();
        rect.right = getX() + getWidth();
        if (player)//player paddles's rect.
        {
            rect.top = GamePanel.HEIGHT - getHeight();
            rect.bottom = GamePanel.HEIGHT;
        }
        else//rival paddle's rect.
        {
            rect.top = 0;
            rect.bottom = getHeight();
        }

    }

    //---------------------------------------------------------------------------------------------
    public RectF getRect() {
        return rect;
    }

    //=============================================================================================
    public Bitmap getImg() {
        return img;
    }

    //=============================================================================================
    public int getCond() {
        return cond;
    }

    //=============================================================================================
    public void setCond(int cond) {
        this.cond = cond;
    }

    //=============================================================================================
    //move the rival paddle left if the ball is in the right side of him and left if the ball is in the left side of him.
    public void ai(Ball ball) {
        //the current place of the rival paddle.
        float mid = this.getX() + (this.getWidth()/2);

        if (ball.getX() > mid)
        {
            if(this.getX()+this.getWidth() < GamePanel.WIDTH)
                setCond(RIGHT);
        }
        if (ball.getX() < mid) {
            if(this.getX() > 0)
                setCond(LEFT);
        }
    }

    //=============================================================================================
    //update and decide to which side the paddle will move.
    public void update(long fps) {

        if (getX() < 0) {
            setX(0);
            setCond(STOP);
        }
        else if (getX() + getWidth() > GamePanel.WIDTH) {
            setX(GamePanel.WIDTH - getWidth());
            setCond(STOP);
        }

        if (getCond() == RIGHT)
            setX(getX() + speed / fps);
        if (getCond() == LEFT)
            setX(getX() - speed / fps);

        rectUpdate();

    }

    //=============================================================================================
    public void draw(long fps, Canvas canvas) {
        canvas.drawBitmap(getImg(), getX(), getY(), null);
    }
}
