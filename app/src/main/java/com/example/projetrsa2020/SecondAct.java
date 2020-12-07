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

                String message = message_a_encrypter.getText().toString();
                System.out.println("Message: " + message);


                // Convertir le message en base 36
                byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                String message_base36 = new BigInteger(1, bytes).toString(36);
                System.out.println("Message Base 36: " + message_base36);


                // Convertir la base 36 en base 10
                String message_base10 = Integer.toString(Integer.parseInt(message_base36, 36), 10);
                System.out.println("Message Base 10: " + message_base10);


                // Aller chercher les valeurs generer des cles
                Intent intent = getIntent();
                getIntent().getSerializableExtra("SocketConnection");
                String e_et_n = intent.getStringExtra("e_et_n");
                Boolean isClient = intent.getBooleanExtra("isclient", true);
                Boolean connectez = intent.getBooleanExtra("isConnecter", true);

                String[] parts = e_et_n.split("_");
                String e = parts[0];
                String n = parts[1];


                // Code 3 du projet de math pour chiffrer M --> C
                Double message_crypter = exponentiation_mod(message_base10, Double.parseDouble(e), Double.parseDouble(n));
                System.out.println("Message Encrypter: " + message_crypter);



                // ***********************************************************
                //              Envoyer au serveur message encrypter
                // ***********************************************************

                if (isClient) {
                    send(message_crypter.toString());
                    try {
                        serverThread.join();}
                    catch (InterruptedException ex) {
                        ex.printStackTrace();}
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


                // Aller chercher les valeurs generer des cles
                Intent intent = getIntent();
                getIntent().getSerializableExtra("SocketConnection");
                Boolean isClient = intent.getBooleanExtra("isclient", true);
                Boolean connectez = intent.getBooleanExtra("isConnecter", true);
                String e_et_n = intent.getStringExtra("e_et_n");
                String d = intent.getStringExtra("d");
                String[] parts = e_et_n.split("_");
                String n = parts[1];

                // ******************************************************************
                //                   Recevoir le message encrypte
                // ******************************************************************

                if (!isClient) {
                    receive();
                    try {
                        serverThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();}
                }



                // --------------------------- Decrypter le message --------------------------------

                // Decrypter le message en base 10 avec un tableau d'exponentiation modulaire
                double message_decrypter = exponentiation_mod(message, Double.parseDouble(d),  Double.parseDouble(n));


                // Reconvertir le message vers la base 36 en String
                String base_36 = Integer.toString(Integer.parseInt(String.valueOf(message_decrypter), 10), 36);


                // Decoder la base 36 en String
                byte[] bytes = new BigInteger(base_36, 36).toByteArray();
                int zeroPrefixLength = zeroPrefixLength(bytes);
                String string_decrypter = new String(bytes, zeroPrefixLength, bytes.length-zeroPrefixLength, StandardCharsets.UTF_8);


                // Afficher dans l'application la string decrypter
                message_recu.setText(string_decrypter);

            }
        });

    }


    /**
     *
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
     * Méthode pour générer un tableau d'exponention modulaire qui encrypte et decrypte un message
     * @param C message a encrypter ou decrypter
     * @param d exposant (soit e pour chiffré ou d pour déchiffrer)
     * @param n produit de p et q (le modulo dans l'équation)
     * @return le message qui a ete encrypter ou decrypter
     */
    public double exponentiation_mod(String C, double d, double n){

        // Convertir exposant en binaire
        String binaire = Integer.toBinaryString((int) d);

        List<String> liste_binaire = new ArrayList<String>(Arrays.asList(binaire.split("")));
        System.out.println(liste_binaire);

        // Inverser l'ordre de la liste binaire
        Collections.reverse(liste_binaire);
        System.out.println(liste_binaire);
        System.out.println(liste_binaire.size());
        double bk = 1;

        for (int i = 0; i < liste_binaire.size(); i++) {

            //            double C;
            String ak = liste_binaire.get(i);   // Element i de la liste

            double _2k = Math.pow(2, i);
            double b = Math.pow(Integer.parseInt(C), _2k);
            double _C_2k = b % n;

            if (ak == "1") {
                bk = bk * _C_2k % n;
            }
        }
        return bk;
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
                    throw e;
                } catch (NullPointerException e) {
                    throw e;
                }
            }
        };
        serverThread = new Thread(serverTask);
        serverThread.start();
    }


    /**
     * Permet l'envoie du message peu importe si c'est le client ou le serveur qui l'envoie.
     * @param message Le message souhaitant être envoyé.
     */
    public void send(final String message) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {

                try {
                    ptAcces.sendMessage(message);

                } catch (NumberFormatException e) {
                    throw e;
                } catch (NullPointerException e) {
                    throw e;
                }
            }
        };
        serverThread = new Thread(serverTask);
        serverThread.start();

    }

}

