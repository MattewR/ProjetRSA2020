package com.example.projetrsa2020;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SecondAct extends AppCompatActivity {

    private EditText message_recu;
    private EditText message_encrypter;
    private Button button_encrypter_send;
    private Button button_decrypter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        // Pour simplifier, je reprends chaque élément graphique et les lie dans le code
        message_encrypter = findViewById(R.id.editText_message_crypter);
        button_encrypter_send = findViewById(R.id.button_encrypter_send);
        message_recu = findViewById(R.id.editTextmessage_recu);
        button_decrypter = findViewById(R.id.buttonDecrypt);






        // Quand on click sur le bouton encrypter et envoyer
        button_encrypter_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                //   --------------------- Generer les cles RSA a envoyer --------------------------

                // Generer aleatoirement e
                // ** MODIFIER ** --> Il faut choisir un e qui est copremier avec n

                int min_e = 100;
                int max_e = 150;
                double e = Math.random() * (max_e-min_e+1) + min_e;


                // Generer aleatoirement n (n = p x q)
                int min_n = 50;
                int max_n = 300;
                double p = Math.random() * (max_n-min_n+1) + min_n;
                double q = Math.random() * (max_n-min_n+1) + min_n;
                double n = p*q;



                // ---------------------------- Crypter le message ---------------------------------

                String message = message_encrypter.getText().toString();
                System.out.println("Message: " + message);

                // Convertir le message en base 36
                byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                String message_base36 = new BigInteger(1, bytes).toString(36);
                System.out.println("Message Base 36: " + message_base36);

                // Convertir la base 36 en base 10
                String message_base10 = Integer.toString(Integer.parseInt(message_base36, 36), 10);
                System.out.println("Message Base 10: " + message_base10);

                // Code 3 du projet de math pour chiffrer M --> C
                Double message_crypter = exponentiation_mod(message_base10, e, n);
                System.out.println("Message Encrypter: " + message_crypter);


                // Calculer d qui est l'inverse modulaire avec l'algorithme d'Euclide etendue
                double d = euclide_etendue(e, n);
                System.out.println("d: " + d);


                // ***********************************************************
                // **   Il faudrait donc envoyer: - e
                //                                - n
                //                                - message_crypter
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

    private int zeroPrefixLength(final byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                return i;
            }
        }
        return bytes.length;
    }


    // --------------------------- Fonctions pour calcul modulaire ---------------------------------

    // Code 1 du projet de math pour calculer l'inverse modulaire
    public Double euclide_etendue(double a, double d){
        double r = a;
        double x = 1;
        double y = 0;
        double r_prime = d;
        double x_prime = 0;
        double y_prime = 1;

        while (r_prime != 0) {

            double q = Math.floor(r_prime);

            r = r_prime;
            x = x_prime;
            y = y_prime;
            r_prime = r-q*r_prime;
            x_prime = x-q*x_prime;
            y_prime = y-q*y_prime;
        }

        while (x < 0) {
            x += d;
            System.out.println(x);
        }

        return  y;
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

