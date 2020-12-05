package com.example.projetrsa2020;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ThirdActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decripter);   // Layout XML lié à cette Activity


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
        System.out.println(prime_numbers);
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
        System.out.println(liste_binaire.size());
        double bk = 1;

        for (int i = 0; i < liste_binaire.size(); i++){

            //            double C;
            String ak = liste_binaire.get(i);   // Element i de la liste

            double _2k = Math.pow(2, i);
            double b = Math.pow(C, _2k);
            double _C_2k = b % n;

            if (ak == "1") {
                bk = bk * _C_2k % n;
            }
        }
        return bk;

    }







}
