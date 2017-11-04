package com.example.kfir.pong;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends Activity {

    public GamePanel panel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        panel = new GamePanel(this);
        setContentView(panel);
    }

    protected void onResume() {
        super.onResume();
        panel.resume();
    }

    protected void onPause() {
        super.onPause();
        panel.pause();
    }
}
