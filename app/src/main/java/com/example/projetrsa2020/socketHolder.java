/*
SocketHolder.java
Permet de manipuler les sockets à travers l'application
Date 8 Décembre 2020
Par Julien-Pierre et Mathis
 */
package com.example.projetrsa2020;

/**
 * @author Mathis
 * Permet au socket connection object d'être accèder à travers l'application
 */
public class socketHolder {
    /**
     * L'objet statique que l'on souhaite accèder
     */
    static SocketConnection sockHold;

    /**
     * Permet de récupérer la socketConnection
     * @return retourne la socketConnection
     */
    public static SocketConnection getSockHold() {
        return sockHold;
    }

    /**
     * Permet de changer la socketConnection
     * @param sockHolds Change la socket connection
     */
    public static void setSockHold(SocketConnection sockHolds) {
        socketHolder.sockHold = sockHolds;
    }
}
