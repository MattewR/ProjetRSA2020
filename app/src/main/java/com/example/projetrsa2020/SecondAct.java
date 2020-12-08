package com.example.projetrsa2020;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecondAct extends AppCompatActivity {


    private EditText message_recu;
    private EditText message_a_encrypter;
    private Button button_encrypter_send;
    private Button button_decrypter;
    private  Button button_afficher_cle;
    private Thread serverThread;
    private boolean codesGenerez = false;
    public SocketConnection ptAcces;
    private String message;


    /**
     * Méthode qui permet de gérer plusieurs choses
     * onClick pour lorsqu'il ya utilisation d'un bouton
     * @param savedInstanceState Instance sauvé
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        // Pour simplifier, je reprends chaque élément graphique et les lie dans le code
        message_a_encrypter = findViewById(R.id.editText_message_crypter);
        button_encrypter_send = findViewById(R.id.button_encrypter_send);
        message_recu = findViewById(R.id.editTextmessage_recu);
        button_decrypter = findViewById(R.id.buttonDecrypt);
        button_afficher_cle = findViewById(R.id.button_affiche_cle);



        // Aller chercher les valeurs generer des cles
        Intent intent = getIntent();

        final String e_et_n = intent.getStringExtra("e_et_n");
        final Boolean isClient = intent.getBooleanExtra("isclient", true);
        final Boolean connectez = intent.getBooleanExtra("isConnecter", true);
        final String d = intent.getStringExtra("d");
        final String[] parts = e_et_n.split("_");
        final String n = parts[1];

        if (isClient) {
            button_decrypter.setEnabled(false);
        }
        else {
            // Si c'est le serveur --> empecher de clicker sur encrypter et envoyer
            button_encrypter_send.setEnabled(false);
        }
        /**
         * Bouton Encrypter et Envoyer dans l'activité 2 pour l'utilisation du client:
         * - Convertit le message écrit en base 36 puis en base 10 pour effectuer les calculs modulaires
         * - Encrypte le message avec les clé recu
         * - Envoie le message encrypté a serveur
         */
        button_encrypter_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {


                // ---------------------- Encrypter le message et l'envoyer ------------------------




                ptAcces = socketHolder.getSockHold();

                if (isClient) {

                    // Empecher le client de clicker sur le bouton decrypter


                    String message = message_a_encrypter.getText().toString();
                    if (message.length() != 0) {

                        System.out.println("Message: " + message);

                        // Provient de la classe base Encoder pour convertir un String en base 10
                        String message_base10 = BaseEncoder.baseConversion(message, 36, 10);
                        System.out.println("Message Base 10: " + message_base10);


                        String[] parts = e_et_n.split("_");
                        String e = parts[0];
                        String n = parts[1];


                        // Chiffrer le message
                        Integer message_crypter = exponentiation_modulaire((int)Double.parseDouble(message_base10), (int)Double.parseDouble(e), (int)Double.parseDouble(n));
                        System.out.println("Message Encrypter: " + message_crypter);


                        // ***********************************************************
                        //              Envoyer au serveur message encrypter
                        // ***********************************************************

                        if (isClient) {
                            send(message_crypter.toString());
                        }
                    }
                }


                // Empecher d'avoir un message vide
                else {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Le message doit contenir du texte", Toast.LENGTH_SHORT).show();
                }

            }
        });







        // Quand on click sur le bouton decrypter
        /**
         *  Bouton décrypter dans l'activité du pour l'utilisation du serveur:
         *  - Recoit le message encrypter par le client
         *  - Décrypte le message
         *  - Reconvertit le message en base 36 puis en string
         *  - Affiche le message décrypter
         */
        button_decrypter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                ptAcces =  socketHolder.getSockHold();


                // ******************************************************************
                //                   Recevoir le message encrypte
                // ******************************************************************

                if (!isClient) {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Toast recu!", Toast.LENGTH_SHORT).show();
                    receive();

                    try {
                        serverThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();}
                }
                new Toast(getApplicationContext()).makeText(getApplicationContext(), "Message recu!", Toast.LENGTH_SHORT).show();



                // --------------------------- Decrypter le message --------------------------------

                // Decrypter le message en base 10 avec un tableau d'exponentiation modulaire
                int message_decrypter = exponentiation_modulaire((int)Double.parseDouble(message), (int)Double.parseDouble(d),  (int)Double.parseDouble(n));


                // Reconvertir le message vers la base 36 en String
                // Provient de la classe base Encoder
                String string_decrypter = BaseEncoder.baseConversion(String.valueOf(message_decrypter), 36, 10);
                System.out.println("Message Base 10: " + string_decrypter);

                // Afficher dans l'application la string decrypter
                message_recu.setText(string_decrypter);

            }
        });


        /**
         * Si on click sur le bouton pour afficher les clé ont fait apparaitre un Toast
         * de la valeur des clées
         */
        button_afficher_cle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] parts = e_et_n.split("_");
                String e = parts[0];
                String n = parts[1];

                new Toast(getApplicationContext()).makeText(getApplicationContext(), "e: "+e + "n: "+n, Toast.LENGTH_SHORT).show();





            }
        });



    }





    /**
     * Fonction pour la conversion d'une base 36 en string
     * @param bytes
     * @return
     */
    private int zeroPrefixLength(final byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                return i;
            }
        }
        return bytes.length;
    }


    /**
     * Méthode pour calculer l'exponention modulaire neccessaire pour encrypter et decrypter un message
     * Le pseudo-code de l'algorithme à été présenté dans le cours de Math Discrète
     * @param C message a encrypter ou decrypter
     * @param exp exposant (soit e pour chiffré ou d pour déchiffrer)
     * @param n produit de p et q (le modulo dans l'équation)
     * @return le message qui a ete encrypter ou decrypter
     */
    public int  exponentiation_modulaire(int C, int exp, int n) {
        long x=1;
        long y=C;

        // Tant que la valeur de l'exposant est sup a 0
        while (exp > 0){

            if (exp%2 == 1){
                x = (x*y) % n;}

            y = (y*y)%n; // Mettre la base au carre
            exp /= 2;
        }

        return (int) x % n;
    }



    /**
     * Permet de recevoir le prochain message par soit le serveur ou le client.
     * Tout est fait sur un nouveau thread.
     */
    public void receive() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {

                try {
                    message = ptAcces.receiveMessage();

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread = new Thread(serverTask);
        serverThread.start();
    }


    /**
     * Permet l'envoie du message peu importe si c'est le client ou le serveur qui l'envoie.
     * @param messager Le message souhaitant être envoyé.
     */
    public void send(final String messager) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {

                try {
                    ptAcces.sendMessage(messager);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread = new Thread(serverTask);
        serverThread.start();

    }

}

