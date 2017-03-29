package com.example.emma_jil.w2017_mylabapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MessageDetails extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "MessageDetails";
    Button msgDeleteButton;
    TextView msg;
    TextView msgID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        // initialize variables
        msg = (TextView)findViewById(R.id.msgDetailsMessage);
        msgID = (TextView)findViewById(R.id.msgDetailsId);
        msgDeleteButton = (Button)findViewById(R.id.deleteMsgButton);

        if(savedInstanceState == null){
            Fragment newFragment = new MessageFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.msgDetailsFragment, new MessageFragment()).commit();
        }

        // get info from Bundle
        final Bundle getBundle = this.getIntent().getExtras();
        msg.setText(getBundle.getString("message"));
        msgID.setText("ID: " + getBundle.getString("id"));

        // Delete Message button
        msgDeleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // MEssage to delete
                Intent msg2delete = new Intent();
                msg2delete.putExtra("Response", getBundle.getString("id"));
                setResult(RESULT_OK, msg2delete);
                finish();
            }
        });

    }

    public static class MessageFragment extends Fragment {

        protected static final String ACTIVITY_NAME_2 = "MessageFragment";

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
            Log.i(ACTIVITY_NAME_2, "in onCreate");
            return inflater.inflate(R.layout.message_details_fragment,
                    container, false);
        }
    }
}
