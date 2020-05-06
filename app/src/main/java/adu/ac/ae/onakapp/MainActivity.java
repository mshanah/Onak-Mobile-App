package adu.ac.ae.onakapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import adu.ac.ae.onakapp.common.OnakActivity;

public class MainActivity extends OnakActivity {

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CardView ann=findViewById(R.id.cv0);
        ann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,AnnouncementActivity.class);
                startActivity(i);
            }
        });
        ann=findViewById(R.id.cv1);
        ann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},100);
                    return;
                }
                MainActivity.this.in=new Intent(MainActivity.this,ReportAccidentActivity.class);
                secureActivity();
            }
        });
    }
    public void logout(View v){
        signOut();
    }
}

