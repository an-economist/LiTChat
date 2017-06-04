package com.xidzheng.litchat21;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.library.bubbleview.BubbleTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public String serverAddress;
    public BufferedReader bufferedReader = null;
    public PrintWriter printWriter = null;
    public Socket socket;

    public String text;
    public String readingLine;
    public String userName;
    public FloatingActionButton btn_send_message;
    public ListView listView;
    public EditText editText;

    public ArrayList<String> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Get the userName from the mainActivity
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("USERNAME");
        serverAddress = bundle.getString("SERVERIP");

        //Set up connection first
        connect();

        listView = (ListView) findViewById(R.id.list_of_message);
        editText = (EditText) findViewById(R.id.user_message);
        btn_send_message = (FloatingActionButton) findViewById(R.id.fab);

        btn_send_message.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Get the message from the editText and then send it to the server
                text = editText.getText().toString();

                new Thread(){

                    @Override
                    public void run(){
                        printWriter.println(text);
                    }

                }.start();

                editText.setText("");
            }
        });

    }

    //Set up the connection to the server socket
    public void connect() {

        new Thread() {
            @Override
            public void run() {

                try {
                    socket = new Socket(serverAddress, 60001);

                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    printWriter = new PrintWriter(socket.getOutputStream(), true);

                    while(true){

                        try{
                            while(true){

                                readingLine = bufferedReader.readLine();

                                if(readingLine.startsWith("SUBMITNAME")){
                                    printWriter.println(userName);

                                }else if(readingLine.startsWith("MESSAGE")){

                                    runOnUiThread(new Runnable(){

                                        @Override
                                        public void run(){
                                            //Add message to the arrayList;
                                            listItems.add(readingLine.substring(8));

                                            ArrayAdapter<String> adapter;

                                            //Adapter is the bridge between the data source and the UI components
                                            adapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_list_item_1, listItems);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }

                                    });
                                }
                            }
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
