package com.example.emma_jil.w2017_mylabapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "ChatWindow";
    // class variables
    ListView chatListView;
    EditText chatMsgText;
    ImageButton sendButton;
    ChatAdapter messageAdapter;
    // ArrayList for messages
    ArrayList<String> messages;
    SQLiteDatabase db;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        Log.i(ACTIVITY_NAME, "In onCreate()");

        // initialize variables
        chatListView = (ListView)findViewById(R.id.chatListView);
        chatMsgText = (EditText)findViewById(R.id.editText);
        sendButton = (ImageButton)findViewById(R.id.sendButton);
        messages = new ArrayList<String>();
        messageAdapter = new ChatAdapter(this);
        chatListView.setAdapter(messageAdapter);

        // Lab5: step 5_creating temp database object
        ChatDatabaseHelper chatDBhelper = new ChatDatabaseHelper(this);
        db = chatDBhelper.getWritableDatabase();
        // gets all existing msgs in db into array
        cursor = messageAdapter.getExistingMessages();
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            String m = messageAdapter.getMsgFromCursor(cursor);
            messages.add(m); // add existing msgs to array
            messageAdapter.notifyDataSetChanged(); // this should add existing msg to screen
            Log.i(ACTIVITY_NAME, "SQL MESSAGE: " + m);
        }
        // get cursor information
        messageAdapter.getCursorInfo(cursor);

        // get sendButton to get EditText and post it in ListView
        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // onClick take EditText and put in ArrayList
                String msg = chatMsgText.getEditableText().toString();
                messages.add(msg);
                messageAdapter.notifyDataSetChanged();
                messageAdapter.addMessageToDatabase(msg); // Lab5: step6
                chatMsgText.setText("");
            }
        });
    }

    protected void onDestroy(){
        Log.i(ACTIVITY_NAME, "In onDestroy()");
        super.onDestroy();
        cursor.close();
        db.close();
    }

    private class ChatAdapter extends ArrayAdapter<String> {
        static final String ACTIVITY_NAME_2 = "ChatAdapter";


        public ChatAdapter(Context ctx){
            super(ctx, 0);
        }

        public int getCount(){
            return messages.size();
        }

        public String getItem(int position){
            return messages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            // return position in Step 9
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if(position%2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }

        public Cursor getExistingMessages(){
            Log.i(ACTIVITY_NAME_2, "in getExistingMessages()");
            return db.rawQuery("SELECT * FROM " +
                    ChatDatabaseHelper.TABLE_NAME + ";", null);
        }

        public String getMsgFromCursor(Cursor c){
            Log.i(ACTIVITY_NAME_2, "in getMsgFromCursor()");
            String msg = c.getString(c.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE));
            return msg;
        }

        public void getCursorInfo(Cursor c){
            // get column count
            Log.i(ACTIVITY_NAME_2, "Cursor's column count: " +
                    c.getColumnCount());
            c.moveToFirst();
            // get column names
            for(int i = 0; i < c.getColumnCount(); i++){
                Log.i(ACTIVITY_NAME_2, "Column " + (i+1) + " name: " +
                        c.getColumnName(i));
            }
        }

        public void addMessageToDatabase(String msg){
            Log.i(ACTIVITY_NAME_2, "in addMessagesToDatabase; Message being added: "
                    + msg);
            // adding using ContentValues
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChatDatabaseHelper.KEY_MESSAGE, msg);
            db.insert(ChatDatabaseHelper.TABLE_NAME, null, contentValues);
        }
    }
}
