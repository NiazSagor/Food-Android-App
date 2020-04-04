package com.angik.duodevloopers.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.imageView5);
        textView = findViewById(R.id.textView7);
        textView2 = findViewById(R.id.textView5);
        textView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_from_down));
        textView2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_from_down));
        imageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_from_down));

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent intent = new Intent(SplashScreenActivity.this, InfoActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
