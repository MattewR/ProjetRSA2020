package com.example.projetrsa2020;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketConnection extends Application {

    public SocketConnection(String port){
        try {
            ServerSocket socketServer =  new ServerSocket(Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
        }
        socketClient = null;

    }

    public SocketConnection(String Host, String port){
        try {
            Socket socketClient = new Socket(InetAddress.getByName(Host), Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
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
                SocketServerAccept = socketServer.accept();
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
    private Socket SocketServerAccept;
    private Socket socketClient;
    private ServerSocket socketServer;

}
