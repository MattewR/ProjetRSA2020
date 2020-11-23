package com.example.projetrsa2020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }





    // Code 2 du projet de Math pour générer p et q (nombres premiers)

    public ArrayList<Double> factorisation(int n){

        ArrayList<Double> prime_numbers = new ArrayList<Double>();
        double p = Math.floor(Math.sqrt(n));
        double q;
        double test = 0;

        while  (p != 0){
            test ++;
            if (p % 2 != 0 || p == 2){
                if (n % p == 0){
                    q = (n/p);

                    prime_numbers.add(p);
                    prime_numbers.add(q);
                }
            }
        }

        return prime_numbers;
    }




    // Code 3 du projet de Math pour générer tableau d'exponention modulaire

    public double exponentiation_mod(double C, double d, double n){

        // Convertir exposant en binaire
        String binaire = Integer.toBinaryString((int) d);

        List<String> liste_binaire = new ArrayList<String>(Arrays.asList(binaire.split("")));
        System.out.println(liste_binaire);

        // Inverser l'ordre de la liste binaire
        Collections.reverse(liste_binaire);
        System.out.println(liste_binaire);

        for (int i = 0; i < liste_binaire.size(); i++){


            String ak = liste_binaire.get(i);   // Element i de la liste


        }

        return 0;

    }



}
