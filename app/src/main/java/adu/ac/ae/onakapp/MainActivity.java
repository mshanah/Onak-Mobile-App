package adu.ac.ae.onakapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import adu.ac.ae.onakapp.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i=new Intent(MainActivity.this,
            LoginActivity.class);
        //Intent is used to switch from one activity to another.
        startActivity(i);
    }
}
