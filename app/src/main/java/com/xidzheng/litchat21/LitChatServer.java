package com.xidzheng.litchat21;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class LitChatServer{

    public final int PORT_NUMBER = 60001;
    //HashSet of Strings to store userNames and a HashSet of PrintWriters so that every client can write at the same time
    public HashSet<String> userNames = new HashSet<>();
    public HashSet<PrintWriter> printWriters = new HashSet<>();
    public ServerSocket serverSocket = null;

    public static void main(String[] args) {

        System.out.println("Server has started up: ");

        LitChatServer newServer = new LitChatServer();
        newServer.createServer();

    }

    //Create the server to listen on a port number;
    public void createServer(){

        try{
            serverSocket = new ServerSocket(PORT_NUMBER);
            while(true){
                Socket clientSocket = new Socket();
                clientSocket = serverSocket.accept();

                System.out.println("Client IP connected at :" + clientSocket.getInetAddress());
                System.out.println("Client Port Number is :" + clientSocket.getPort());
                System.out.println("Server Port Number is :" + clientSocket.getLocalPort());

                Thread handlerThread = new Thread(new Handler(clientSocket));
                handlerThread.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    //Handler class to communicate with the clients
    private class Handler implements Runnable{

        private String name;
        private Socket socket;
        private BufferedReader bufferedReader;
        private PrintWriter printWriter;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try{
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);

                //Trying to read a username
                while(true){
                    printWriter.println("SUBMITNAME");

                    name = bufferedReader.readLine();
                    if(name == null){
                        return;
                    }
                    synchronized (userNames) {
                        if(!userNames.contains(name)){
                            userNames.add(name);
                            break;
                        }
                    }
                }

                printWriters.add(printWriter);
                System.out.println(name + " has connected");

                //Reads the stream and write the name of the user along with the message
                while(true){

                    String input = bufferedReader.readLine();

                    if(input == null){
                        return;
                    }

                    for(PrintWriter writer: printWriters){
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }

            }catch(IOException e){
                System.out.println(e);
            }finally{
                if(name != null){
                    userNames.remove(name);
                    System.out.println(name + " has disconnected");
                }
                if(printWriter != null){
                    printWriters.remove(printWriter);
                }
                try{
                    socket.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

        }

    }
}