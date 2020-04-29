package adu.ac.ae.onakapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import adu.ac.ae.onakapp.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        RotateAnimation anim = new RotateAnimation(0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(5000);
        ImageView img = (ImageView)findViewById(R.id.alogo);
        Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out);
        img.startAnimation(aniSlide);
// Start animating the image
        final ImageView splash = (ImageView) findViewById(R.id.logo);
        splash.startAnimation(anim);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashActivity.this,
                    MainActivity.class);
                //Intent is used to switch from one activity to another.
                startActivity(i);
                //invoke the SecondActivity.
                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
