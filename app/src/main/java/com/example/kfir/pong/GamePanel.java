package com.example.kfir.pong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements Runnable {
    //--------------final declaration-------------------------------
    private final int SECOND = 1000; //1 sec equals 1000 ms.
    //--------------static declaration------------------------------
    public static int WIDTH;
    public static int HEIGHT;
    //--------------variables declaration---------------------------
    private boolean playing, paused;
    private long fps; //track the game frame rate.
    private long current_frame_time;
    private int player_score, rival_score;
    //--------------instances declaration---------------------------
    private Canvas canvas;
    private SurfaceHolder holder;
    private Paint paint;
    private Bitmap bg;
    private Paddle rival, player;
    private Ball ball;
    private Thread game_thread;

    //constructor.
    public GamePanel(Context context) {
        super(context);
        //--------------var initialize------------------------------
        this.player_score = 0;
        this.rival_score = 0;
        //--------------static initialize---------------------------
        //get the screen size.
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WIDTH = metrics.widthPixels;
        HEIGHT = metrics.heightPixels;

        //--------------instances initialize---------------------------
        this.holder = getHolder();
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg), WIDTH, HEIGHT, false);
        rival = new Paddle(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), 52, 250, WIDTH/2, 0, false);
        player = new Paddle(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), 52, 250, 0, HEIGHT - 52, true);
        ball = new Ball(BitmapFactory.decodeResource(getResources(), R.drawable.ball), 50, 50, GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2);
    }

    //=============================================================================================
    //Runnable implement.
    public void run() {
        //if the user playing, save the current time.
        //update the game if it not paused, draw the canvas and save the current time.
        //if the current time is bigger then 1, update the fps.
        while (playing) {
            long start_time = System.currentTimeMillis();
            if (!paused)
                update();
            draw();
            current_frame_time = System.currentTimeMillis() - start_time;
            if (current_frame_time >= 1)
                fps = SECOND / current_frame_time;
        }
    }

    //=============================================================================================
    //decides which angle the ball get while it collision with the paddle.
    public void collision(Paddle paddle)
    {
        //if the ball and the paddle are intersect.
        if (ball.getRect().intersect(paddle.getRect())) {
            int unit = paddle.getWidth() / 6;   //part of the paddle.
            float ball_mid = ball.getDiameter() / 2 + ball.getX();  //the middle of the ball.

            //the max/min angle is +-45, created by the x and y axis speed.
            if (ball_mid >= paddle.getX() && ball_mid < paddle.getX() + unit)
                ball.reverseX(180);
            else if (ball_mid > paddle.getX() + unit && ball_mid < paddle.getX() + (unit * 2))
                ball.reverseX(160);
            else if (ball_mid > paddle.getX() + (unit * 2) && ball_mid < paddle.getX() + (unit * 3))
                ball.reverseX(120);
            else if (ball_mid > paddle.getX() + (unit * 3) && ball_mid < paddle.getX() + (unit * 4))
                ball.reverseX(60);
            else if (ball_mid > paddle.getX() + (unit * 4) && ball_mid < paddle.getX() + (unit * 5))
                ball.reverseX(30);
            else if (ball_mid > paddle.getX() + (unit * 5) && ball_mid <= paddle.getX() + (unit * 6))
                ball.reverseX(0);

            //reflect the ball on y axis.
            ball.reverseY();
        }

    }


    //=============================================================================================
    //update method.
    public void update() {
        ball.update(fps);
        player.update(fps);
        rival.ai(ball);
        rival.update(fps);

        //check for collision with the player's paddle and clear obstacles.
        if (ball.getRect().intersect(player.getRect())) {
            collision(player);
            ball.clearObstacleY(GamePanel.HEIGHT - player.getHeight() - ball.getDiameter() - 2);
        }
        //check for collision with the rival's paddle and clear obstacles.
        else if(ball.getRect().intersect(rival.getRect()))
        {
            collision(rival);
            ball.clearObstacleY(rival.getHeight() + 2);
        }
        else {
            //---------------------
            //check for collision with the walls and clear obstacles.
            if (ball.getRect().left < 0 || ball.getX() + ball.getDiameter() > GamePanel.WIDTH) {
                if (ball.getRect().left < 0) {
                    ball.reverseX(0);
                    ball.clearObstacleX(2);
                } else {
                    ball.reverseX(180);
                    ball.clearObstacleX(GamePanel.WIDTH - 52);
                }
            } else if (ball.getY() < 0 || ball.getY() + ball.getDiameter() > GamePanel.HEIGHT) {
                ball.reverseY();
                if (ball.getY() < 0) {
                    ball.clearObstacleY(2);
                    player_score++;
                }
                else {
                    ball.clearObstacleY(GamePanel.HEIGHT - 52);
                    rival_score++;
                }
            }
        }


    }

    //=============================================================================================
    //drawing method.
    public void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            //draw background.
            canvas.drawBitmap(bg, 0, 0, paint);

            //initialize the paint and the position of the score text.
            paint.setColor(Color.parseColor("#c0cec7"));
            paint.setTextSize(300);
            int xPos = (int)(WIDTH/2 - paint.descent());
            int yPos = (int) ((HEIGHT / 2) - ((paint.descent() + paint.ascent()) / 2));
            int factor = HEIGHT/5;

            //draw the text.
            canvas.drawText(""+player_score,xPos,yPos+factor,paint);
            canvas.drawText(""+rival_score,xPos,yPos-factor,paint);

            //draw paddles.
            rival.draw(fps, canvas);
            player.draw(fps, canvas);

            //draw ball.
            ball.draw(fps, canvas);
            holder.unlockCanvasAndPost(canvas);

        }
    }

    //=============================================================================================
    //what happened while the game is running.
    public void resume() {
        playing = true;
        this.game_thread = new Thread(this);
        game_thread.start();
    }

    //---------------------------------------------------------------------------------------------
    //what happened if the game is paused.
    public void pause() {
        playing = false;
        try {
            game_thread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    //=============================================================================================
    //the screen is split to right and left just in the middle.
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if (motionEvent.getX() > WIDTH / 2)
                    player.setCond(player.RIGHT);
                else
                    player.setCond(player.LEFT);
                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                player.setCond(player.STOP);
                break;
        }

        return true;
    }
}
