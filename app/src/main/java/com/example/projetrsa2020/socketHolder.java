package com.example.projetrsa2020;

public class socketHolder {

    static SocketConnection sockHold;

    public static SocketConnection getSockHold() {
        return sockHold;
    }

    public static void setSockHold(SocketConnection sockHolds) {
        socketHolder.sockHold = sockHolds;
    }
}
