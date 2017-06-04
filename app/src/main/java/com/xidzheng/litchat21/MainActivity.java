package com.xidzheng.litchat21;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public String userName;
    public String serverAddress;
    /*
    Note: Do not Block the UI Thread, i.e Run Socket Programming on Separate Thread
    Note: Do not access the Android UI toolkit from outside the UI Thread
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Called when the user presses the Login button and changes view to ChatActivity
    public void login(View view){

        userName = ((EditText) findViewById(R.id.edit_text_user)).getText().toString();
        serverAddress = ((EditText) findViewById(R.id.edit_text_server)).getText().toString();

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("USERNAME", userName);
        intent.putExtra("SERVERIP", serverAddress);
        MainActivity.this.startActivity(intent);

    }

    public void quit(View view){

        Button btn1 = (Button) findViewById(R.id.button2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

    }

}
