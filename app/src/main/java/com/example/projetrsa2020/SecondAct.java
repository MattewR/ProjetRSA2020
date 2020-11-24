package com.example.projetrsa2020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SecondAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

}

class SocketConnection{

    public SocketConnection(int port){
        try {
            ServerSocket socketServer =  new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketClient = null;

    }
    public SocketConnection(String Host, int port){
        try {
            Socket socketClient = new Socket(Host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketServer = null;

    }

    public void sendMessage(String message){
        if(socketServer == null) {
            DataOutputStream outputStream = null;
            try {
                outputStream = new DataOutputStream(socketClient.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String receiveMessage(){


            DataInputStream inputStream = null;
            try {
                inputStream = new DataInputStream(socketClient.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
    }

    public void accept(){

        if(socketServer != null){
            try {
                socketClient = socketServer.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void close(){
        try {
            socketClient.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        try {
            socketServer.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private Socket socketClient;
    private ServerSocket socketServer;


}