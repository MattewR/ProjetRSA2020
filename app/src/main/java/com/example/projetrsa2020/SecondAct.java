package com.example.projetrsa2020;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SecondAct extends AppCompatActivity {

    private EditText message_recu;
    private EditText message_a_encrypter;
    private Button button_encrypter_send;
    private Button button_decrypter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        // Pour simplifier, je reprends chaque élément graphique et les lie dans le code
        message_a_encrypter = findViewById(R.id.editText_message_crypter);
        button_encrypter_send = findViewById(R.id.button_encrypter_send);
        message_recu = findViewById(R.id.editTextmessage_recu);
        button_decrypter = findViewById(R.id.buttonDecrypt);






        // Quand on click sur le bouton encrypter et envoyer
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

                int e = 0;
                int n = 0;
                // Code 3 du projet de math pour chiffrer M --> C
                Double message_crypter = exponentiation_mod(message_base10, e, n);
                System.out.println("Message Encrypter: " + message_crypter);



                // Test pour verifier que la conversion de retour fonctionne

                // Reconvertir le message vers la base 36 en String
                String base_36 = Integer.toString(Integer.parseInt(String.valueOf(message_crypter), 10), 36);

                // Decoder la base 36 en String
                byte[] bytes = new BigInteger(base_36, 36).toByteArray();
                int zeroPrefixLength = zeroPrefixLength(bytes);
                String string_decrypter = new String(bytes, zeroPrefixLength, bytes.length-zeroPrefixLength, StandardCharsets.UTF_8);


                // Afficher dans l'application la string decrypter
                message_recu.setText(string_decrypter);


                // ***********************************************************
                // **  Envoyer au serveur message encrypter
                // ***********************************************************

            }
        });







        // Quand on click sur le bouton decrypter
        button_decrypter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {


                // --------------------------- Decrypter le message --------------------------------


                // ******************************************************************
                // ** Il faut mettre le bout de code pour recevoir l'info transmis
                // ******************************************************************

                String message_encrypter = "";
                double d = 0;
                double n = 0;

                // Decrypter le message en base 10 avec un tableau d'exponentiation modulaire
                double message_decrypter = exponentiation_mod(message_encrypter, d,  n);


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



//
//    // Fonctions qui permettent de savoir si 2 nombres sont co-premier entre eux
//
//    private static int PGCD(int a, int b) {
//        int t;
//        while (b!=0) {
//            t = a;
//            a = b;
//            b = t % b;}
//        return a;
//    }
//
//    // Verifier si co-premier
//    private static boolean co_premier(int a, int b) {
//        return PGCD(a,b) == 1;
//    }
//
//    // Retourner la premiere paire de nombre co-premier
//    private static ArrayList<Integer> paires_co_premier(int arr[], int n){
//
//        int trouver = 0;
//        ArrayList<Integer> co_premier = new ArrayList<Integer>();
//
//        for (int i = 0; i < n - 1; i++)
//            for (int j = i + 1; j < n; j++)
//
//                if (co_premier(arr[i], arr[j]) && trouver!= 1) {
//                    trouver = 1;
//                    co_premier.add(i);
//                    co_premier.add(j);}
//
//        return co_premier;
//    }
//







    private int zeroPrefixLength(final byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                return i;
            }
        }
        return bytes.length;
    }


    // Code 3 du projet de Math pour générer tableau d'exponention modulaire
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


}

