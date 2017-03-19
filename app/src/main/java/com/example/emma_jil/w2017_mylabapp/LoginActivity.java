package com.example.emma_jil.w2017_mylabapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // Variables
    protected static final String ACTIVITY_NAME = "LoginActivity";

    EditText username, password;
    Button loginButton;

    public static final String PREFERENCES = "myPrefs";
    public static final String NAME = "loginName";
    public static final String PASSWORD = "loginPW";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // initializing variables
        username = (EditText)findViewById(R.id.LoginName);
        password = (EditText)findViewById(R.id.LoginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        sharedPreferences = getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // pretty sure this isn't supposed to go here.
                String name = username.getText().toString();
                String pw = password.getText().toString();

                SharedPreferences.Editor e = sharedPreferences.edit();

                e.putString(NAME, name);
                e.putString(PASSWORD, pw);
                e.commit();

                // Login button leads to StartActivity
                Intent i = new Intent(LoginActivity.this, StartActivity.class);
                startActivity(i);
            }
        });
    }

    protected void onResume(){
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onResume();
    }

    protected void onStart(){
        Log.i(ACTIVITY_NAME, "In onStart()");
        super.onStart();

        //Modify to read the value of the stored email address in SharedPreferences
        // and sets the initial text of the login field. See example.

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

}
