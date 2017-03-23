package com.example.emma_jil.w2017_mylabapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "StartActivity";
    public final static int REQUEST_CODE = 5;
    Button ImAButton;
    Button chatButton;
    Button weatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.i(ACTIVITY_NAME, "In onCreate()");

        ImAButton = (Button)findViewById(R.id.button);
        chatButton = (Button)findViewById(R.id.startChatButton);

        ImAButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // button leads to ListItemsActivity
                Intent i = new Intent(StartActivity.this, ListItemsActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // leads to ChatWindow
                Intent i = new Intent(StartActivity.this, ChatWindow.class);
                startActivity(i);
            }
        });

        weatherButton = (Button)findViewById(R.id.weatherForecastButton);
        weatherButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent weatherIntent = new Intent(StartActivity.this, WeatherForecast.class);
                startActivity(weatherIntent);
            }
        });
    }

    protected void onResume(){
        Log.i(ACTIVITY_NAME, "In onResume()");
        super.onResume();
    }

    protected void onStart(){
        Log.i(ACTIVITY_NAME, "In onStart()");
        super.onStart();
    }

    protected void onPause(){
        Log.i(ACTIVITY_NAME, "In onPause()");
        super.onPause();
    }

    protected void onStop(){
        Log.i(ACTIVITY_NAME, "In onStop()");
        super.onStop();
    }

    protected void onDestroy(){
        Log.i(ACTIVITY_NAME, "In onDestroy()");
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE){
            Log.i(ACTIVITY_NAME, "Returned to StartActivity.onActivityResult");
            if(resultCode == RESULT_OK){
                String messagePassed = data.getStringExtra("Response");
                // add Toast to display information
                Context c = getApplicationContext();
                int duration = Toast.LENGTH_LONG;

                Toast t = Toast.makeText(c, messagePassed, duration);
                t.show();
            }
        }
    }
}
