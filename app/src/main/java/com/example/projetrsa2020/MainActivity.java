package com.example.projetrsa2020;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {


    private final int FROM_THIRD_ACTIVITY = 1;
    public static SocketConnection ptAcces;
    private boolean isClient = true;
    private Button bouton_decrypter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Relier chaque element graphique dans le code
        bouton_decrypter = findViewById(R.id.button2);


        // Quand on click sur le bouton decrypter
        bouton_decrypter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Créer un Intent disant d'où on part (l'activité de retour) et quelle activité on veut créer
                Intent intent = new Intent(MainActivity.this, ThirdActivity.class);

                // Ajoute les extras à transmettre
                //intent.putExtra(NOM_JOUEUR, editText.getText().toString());

                // Créer l'activité avec un code de retour
                startActivityForResult(intent, FROM_THIRD_ACTIVITY);
            }
        });

        //Quand on click sur Se Connecter
        Button bouton_co =  findViewById(R.id.butConnecter);
        bouton_co.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String ip = (((EditText) findViewById(R.id.IPInput)).getText()).toString();
                    String port = ((EditText) findViewById(R.id.IPInput)).getText().toString();
                    if ( isClient ) {
                        ptAcces = new SocketConnection(ip, port);
                    }
                    else{
                        ptAcces = new SocketConnection(port);
                    }
                    ecrireToMem(ip,port);
                }
                catch (NumberFormatException e){
                    new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
                }


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Quel item a été sélect
        EditText premierEditText = (EditText) findViewById(R.id.IPInput);
        EditText deuxiemeEditText = (EditText) findViewById(R.id.PortInput);

        switch (item.getItemId()) {
            case R.id.ipMenu:
                String[] nouvPar = lireMem();
                if(nouvPar.length != 2){
                    new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
                }else{
                    premierEditText.setText(nouvPar[0]);
                    deuxiemeEditText.setText(nouvPar[1]);
                }
                return true;
            case R.id.devClient:
                isClient = true;
                premierEditText.setFocusable(true);
                return true;
            case R.id.devServeur:
                isClient = false;
                premierEditText.setFocusable(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // D'où est-ce que je reviens?
        switch (requestCode) {
            case FROM_THIRD_ACTIVITY:
                // Est-ce que tout c'est bien terminé?
                if (resultCode == RESULT_OK) {
                    // Je vais chercher les Extras disponibles dans l'Intent container "data".
//                    String messageRetour = data.getStringExtra(SecondAct.RETURN_TEXT);
//                    returnText.setText(messageRetour);

                }
                break;
        }
    }

    private boolean ecrireToMem(String ip, String port){
        FileOutputStream fichierAEcrire;
        try
        {
            fichierAEcrire = openFileOutput("stockageIpPort.txt", MODE_PRIVATE);
            try {
                fichierAEcrire.write((ip.concat("_" + port)).getBytes());
            }
            catch (NullPointerException e){
                new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
            }
            fichierAEcrire.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Impossibilité d'écrire",Toast.LENGTH_SHORT).show();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Impossibilité d'écrire",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private String[] lireMem(){

        try
        {
            FileInputStream fichierAEcrire =  new FileInputStream("stockageIpPort.txt");
            ObjectInputStream ois = new ObjectInputStream(fichierAEcrire);
            try {
                return  String.valueOf(ois.readByte()).split("_");
            }
            catch (NullPointerException e){
                new Toast(getApplicationContext()).makeText(getApplicationContext(),"Veuillez entrez des données valides",Toast.LENGTH_SHORT).show();
            }
            fichierAEcrire.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Impossibilité d'écrire",Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(),"Impossibilité d'écrire",Toast.LENGTH_SHORT).show();

        }

        return new String[0];



    }



}
