package com.example.projetrsa2020;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class SocketConnection extends Application {

    public SocketConnection(String port) {
        try {
            socketServer = new ServerSocket(Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();


        } catch (NumberFormatException e) {
            e.printStackTrace();


        }
        socketClient = null;

    }

    public SocketConnection(String Host, String port) {

        socketClient = new Socket();


        try {
            socketClient.connect(new InetSocketAddress(InetAddress.getByName(Host), Integer.parseInt(port)));
        } catch (IOException e) {
            e.printStackTrace();

        }


        socketServer = null;

    }

    public void sendMessage(String message) {

        DataOutputStream outputStream = null;
        try {
            if (socketServer == null) {
                outputStream = new DataOutputStream(socketClient.getOutputStream());
            } else {
                outputStream = new DataOutputStream(socketServerAccept.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String receiveMessage() {

        DataInputStream inputStream = null;

        try {
            if (socketServerAccept != null) {
                inputStream = new DataInputStream(socketServerAccept.getInputStream());
            } else {
                inputStream = new DataInputStream(socketClient.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String toSend = "";
        while (toSend.equals("")) {
            try {
                toSend = inputStream.readUTF();
                inputStream.close();
                if(!toSend.equals("")) {
                    return toSend;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return "";

    }

    public void accept() {

        if (socketServer != null) {
            try {
                socketServerAccept = socketServer.accept();
                if (socketServerAccept.isConnected()) {
                    isConnectedServer = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void close() {
        try {
            socketClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socketServer.close();
            socketServerAccept.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateClientStatus() {
        if (socketClient != null) {
            isConnectedClient = socketClient.isConnected();
        }
    }


    public boolean getConnectionstatusClient() {
        return isConnectedClient;
    }

    public boolean getConnectionstatusServer() {
        return isConnectedServer;
    }

    private boolean isConnectedClient = false;
    private boolean isConnectedServer = false;
    private Socket socketServerAccept;
    private Socket socketClient;
    private ServerSocket socketServer;

}
