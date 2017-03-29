package com.example.emma_jil.w2017_mylabapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
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
    FrameLayout frameLayout;
    // ArrayList for messages
    ArrayList<String> messages;
    SQLiteDatabase db;
    Cursor cursor;
    ChatDatabaseHelper chatDBhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        Log.i(ACTIVITY_NAME, "In onCreate()");

        // initialize variables
        chatListView = (ListView)findViewById(R.id.chatListView);
        chatMsgText = (EditText)findViewById(R.id.editText);
        sendButton = (ImageButton)findViewById(R.id.sendButton);
        frameLayout = (FrameLayout)findViewById(R.id.chatWindowFrameLayout);
        //messages = new ArrayList<String>();
        //messageAdapter = new ChatAdapter(this);
        //chatListView.setAdapter(messageAdapter);


        // Lab5: step 5_creating temp database object
        chatDBhelper = new ChatDatabaseHelper(this);
        db = chatDBhelper.getWritableDatabase();
        // gets all existing msgs in db into array
        getAllMessages();
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

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // set Bundle info
                Bundle bundle = new Bundle();
                bundle.putString("message", messageAdapter.getItem(position));
                bundle.putString("id", String.valueOf(position));
                Log.i(ACTIVITY_NAME, "in onClick; position: " + position);
                // Launch fragment
                Intent delMsg = new Intent(ChatWindow.this, MessageDetails.class);
                delMsg.putExtras(bundle);
                startActivityForResult(delMsg, position);
            }
        });
    }

    // for testing purposes
    protected void printArrayList(ArrayList a){
        int s = a.size();
        for(int i=0;i<s;i++){
            Log.i("Msg ", "["+i+"] "+ a.get(i));
        }
    }

    protected void getAllMessages(){
        messages = new ArrayList<String>();
        messageAdapter = new ChatAdapter(this);
        chatListView.setAdapter(messageAdapter);
        cursor = messageAdapter.getExistingMessages();
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            String m = messageAdapter.getMsgFromCursor(cursor);
            int i = cursor.getPosition();
            messages.add(m); // add existing msgs to array
            messageAdapter.notifyDataSetChanged(); // this should add existing msg to screen
            Log.i(ACTIVITY_NAME, "SQL MESSAGE "+i+": " + m);
        }
    }

    protected void onResume(){
        Log.i(ACTIVITY_NAME, "In onResume()");
        super.onResume();
    }

    protected void onDestroy(){
        Log.i(ACTIVITY_NAME, "In onDestroy()");
        super.onDestroy();
        cursor.close();
        db.close();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(ACTIVITY_NAME, "Returned to " + ACTIVITY_NAME + ".onActivityResult");
        if(resultCode == RESULT_OK){
            String messagePassed = data.getStringExtra("Response");
            // go to deleteMessageFromDatabase()
            try{
                int p = Integer.parseInt(messagePassed);
                messageAdapter.deleteMessageFromDatabase(p);
            }catch(Exception e){
                Log.i(ACTIVITY_NAME, "in onActivityResult error: " + e);
            }
            // refresh ListView
            getAllMessages();
        }
    }

    private class ChatAdapter extends ArrayAdapter<String> {
        static final String ACTIVITY_NAME_2 = "ChatAdapter";


        private ChatAdapter(Context ctx){
            super(ctx, 0);
        }

        public int getCount(){
            //Log.i(ACTIVITY_NAME_2, "in getCount");
            return messages.size();
        }

        public String getItem(int position){
            //Log.i(ACTIVITY_NAME_2, "in getItem");
            return messages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            // return position in Step 9
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;

            if(position%2 == 0) result = inflater.inflate(R.layout.chat_row_incoming, null);
            else result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }

        private Cursor getExistingMessages(){
            Log.i(ACTIVITY_NAME_2, "in getExistingMessages()");
            return db.rawQuery("SELECT * FROM " +
                    ChatDatabaseHelper.TABLE_NAME + ";", null);
        }

        private String getMsgFromCursor(Cursor c){
            //Log.i(ACTIVITY_NAME_2, "in getMsgFromCursor()");
            return c.getString(c.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE));
        }

        private void getCursorInfo(Cursor c){
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

        private void addMessageToDatabase(String msg){
            Log.i(ACTIVITY_NAME_2, "in addMessagesToDatabase; Message being added: "
                    + msg);
            // adding using ContentValues
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChatDatabaseHelper.KEY_MESSAGE, msg);
            db.insert(ChatDatabaseHelper.TABLE_NAME, null, contentValues);
        }

        private void deleteMessageFromDatabase(int position){
            Log.i(ACTIVITY_NAME_2, "in deleteMessageFromDatabase. Removing: "
            + getItem(position));

            try{
                String updateSQLdb = "UPDATE " + ChatDatabaseHelper.TABLE_NAME
                        + " SET " + ChatDatabaseHelper.KEY_MESSAGE
                        + " = null WHERE " + ChatDatabaseHelper.KEY_ID
                        + "=" + (position+2);
                db.execSQL(updateSQLdb);
            }catch(Exception e){
                Log.i(ACTIVITY_NAME_2, "in deleteMessageFromDatabase error: " + e);
            }
        }
    }
}
