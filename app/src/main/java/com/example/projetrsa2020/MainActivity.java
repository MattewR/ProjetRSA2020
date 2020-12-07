package com.example.projetrsa2020;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final int FROM_SECOND_ACTIVITY = 1;
    public SocketConnection ptAcces;
    private boolean isClient = true;
    private Button bouton_generer_codes;
    private Button bouton_communication_RSA;
    private Button boutonConnecter;
    private boolean connectez = false;
    private boolean codesGenerez = false;
    private double publicKey;
    private double privateKey;
    private String message;
    private Thread serverThread;
    private double n;

    /**
     * Créer le serveur et commence tout de suite à accepter la prochaine connection
     * À cause des restrtictions Android, tout est fait dans un nouveau thread.
     * @param port Le port du serveur
     */
    public void start(final String port) {

        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {

                try {
                    ptAcces = new SocketConnection(port);
                    ptAcces.accept();
                    connectez = true;
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
     * Créer le socket pour le client et connecte celui-ci au serveur.
     * Tout est fait sur un nouveau thread.
     * @param port Port de destination
     * @param ip Ip de destination
     */
    public void start(final String port, final String ip) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                ptAcces = new SocketConnection(ip, port);
                ptAcces.updateClientStatus();
                connectez = true;
            }
        };
        serverThread = new Thread(serverTask);
        serverThread.start();
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

    /**
     * Fonction principal permettant bien des choses:
     * Les OnClick pour les boutons
     * Change le titre de l'action bar
     * Change la couleur des boutons
     * @param savedInstanceState Instance sauvé
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Messagerie Recevoir-Sans-Agent");

        // Relier chaque element graphique dans le code
        bouton_generer_codes = findViewById(R.id.button_generer_codes);
        bouton_communication_RSA = findViewById(R.id.button_communication);


        // Quand on clisk sur le bouton Generer les codes RSA
        bouton_generer_codes.setOnClickListener(new View.OnClickListener() {
            /**
             * Génère les clé RSA et envoie la clé publique au client
             * @param view Vue sur laquelle le bouton est
             */
            @Override
            public void onClick(View view) {

                //   --------------------- Generer les cles RSA a envoyer --------------------------

                // ArrayList de nombre premiers
                ArrayList<Double> prime_numbers = prime();

                // Generer aleatoirement n (n = p x q)
                int min = 0;
                int max = prime_numbers.size();
                double index_p = Math.random() * (max - min + 1) + min;
                double index_q = Math.random() * (max - min + 1) + min;
                double p = prime_numbers.get((int) index_p);
                double q = prime_numbers.get((int) index_q);
                double n = p * q;


                // Generer e qui est copremier avec (𝑝 − 1)(𝑞 − 1)
                double coprime = (p - 1) * (q - 1);
                double e = coprime - 1;   // Pcq par definition 2 entiers qui se suivent sont co-premier


                // Calculer d --> Inverse modulaire e mod (p-1)(q-1)
                double d = euclide_etendue(e, coprime);
                System.out.println(d);


                // -------------------------------------------------------
                //                    Envoyer e + n
                // -------------------------------------------------------
                if (!isClient && connectez) {
                    send(String.valueOf(e) + "_" + String.valueOf(n));
                    try {
                        serverThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    bouton_generer_codes.setBackgroundColor(getResources().getColor(R.color.vertPermis));
                    bouton_communication_RSA.setBackgroundColor(getResources().getColor(R.color.jauneAnanas));
                    codesGenerez = true;
                } else if (connectez) {
                    receive();
                    try {
                        serverThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    String eEtN = message;
                    codesGenerez = true;
                    bouton_generer_codes.setBackgroundColor(getResources().getColor(R.color.vertPermis));
                    bouton_communication_RSA.setBackgroundColor(getResources().getColor(R.color.jauneAnanas));
                } else {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Veuillez-vous connectez avant", Toast.LENGTH_SHORT).show();
                    codesGenerez = false;
                }

            }
        });


        // Quand on click sur le bouton Communication RSA
        bouton_communication_RSA.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * Permet de changer d'activité si seulement l'utilisateur est connecté et à les codes.
             */
            public void onClick(View view) {

                //Part la nouvelle activité si et seulement si les codes ont été générer pour le serveur et si le client ou le serveur sont connectez
                //Enlevez le true quand l'app va marcher
                if (connectez && codesGenerez) {
                    // Créer un Intent disant d'où on part (l'activité de retour) et quelle activité on veut créer
                    Intent intent = new Intent(MainActivity.this, SecondAct.class);

                    // Ajoute les extras à transmettre
                    //intent.putExtra(NOM_JOUEUR, editText.getText().toString());

                    // Créer l'activité avec un code de retour
                    startActivityForResult(intent, FROM_SECOND_ACTIVITY);
                } else {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Assurez-vous d'être connecter et de générez les clés avant d'accéder a l'application principal", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Quand on click sur Se Connecter
        boutonConnecter = findViewById(R.id.butConnecter);
        boutonConnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * Initialise la connection avec Start.
             */
            public void onClick(View view) {

                try {
                    String ip = (((EditText) findViewById(R.id.IPInput)).getText()).toString();
                    String port = ((EditText) findViewById(R.id.PortInput)).getText().toString();
                    if (isClient) {
                        start(port, ip);
                        serverThread.join();
                        ecrireToMem(ip, port,getApplicationContext());
                        if (connectez == false) {
                            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Impossibilité de se connecter", Toast.LENGTH_SHORT).show();
                        } else {
                            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Connection réussie", Toast.LENGTH_SHORT).show();
                            connectez = true;

                        }
                    } else {
                        start(port);
                        serverThread.join();
                        if (connectez) {
                            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Connection réussie", Toast.LENGTH_SHORT).show();
                            boutonConnecter.setBackgroundColor(getResources().getColor(R.color.vertPermis));
                            bouton_generer_codes.setBackgroundColor(getResources().getColor(R.color.jauneAnanas));
                        }
                        else{
                            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Erreur lors de la connection (Connexion à pris trop de temps)", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (NumberFormatException e) {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Veuillez entrez des données valides", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Erreur lors de la connection (Assurez-que le serveur est en marche)", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Erreur lors de la connection (Assurez-que le serveur est en marche)", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }
        });
        //Change la couleur des boutons
        boutonConnecter.setBackgroundColor(getResources().getColor(R.color.jauneAnanas));
        bouton_generer_codes.setBackgroundColor(getResources().getColor(R.color.rougeAlarme));
        bouton_communication_RSA.setBackgroundColor(getResources().getColor(R.color.rougeAlarme));
    }

    /**
     * Génère le menu
     * @param menu Menu à rajouté à l'action bar
     * @return Retourne si ça marché ou pas (Bool)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumain, menu);
        return true;
    }

    /**
     * Indique au programme quoi faire
     * @param item Quel item à été clické
     * @return Retourne si ça marché ou pas
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Quel item a été sélect
        EditText premierEditText = (EditText) findViewById(R.id.IPInput);
        EditText deuxiemeEditText = (EditText) findViewById(R.id.PortInput);

        switch (item.getItemId()) {
            case R.id.ipMenu:
                String[] nouvPar = lireMem(getApplicationContext());
                if (nouvPar.length != 2) {
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), "Veuillez entrez des données valides", Toast.LENGTH_SHORT).show();
                } else {
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

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // D'où est-ce que je reviens?
        switch (requestCode) {
            case FROM_SECOND_ACTIVITY:
                // Est-ce que tout c'est bien terminé?
                if (resultCode == RESULT_OK) {
                    // Je vais chercher les Extras disponibles dans l'Intent container "data".
//                    String messageRetour = data.getStringExtra(SecondAct.RETURN_TEXT);
//                    returnText.setText(messageRetour);

                }
                break;
        }
    }

    /**
     * Permet d'écrire dans un fichier txt l'ip et le port. Ceux-ci pourront ensuite être récuppérer par liremem.
     * @param ip l'ip à écrire
     * @param port le port à écrire
     * @param contexte le contexte
     * @return
     */
    private boolean ecrireToMem(String ip, String port, Context contexte) {

        try {
            OutputStreamWriter fichierAEcrire = new OutputStreamWriter(contexte.openFileOutput("stockageIpPort.txt", contexte.MODE_PRIVATE));
            try {
                fichierAEcrire.write((ip.concat("_" + port)));

            } catch (NullPointerException e) {
                new Toast(getApplicationContext()).makeText(getApplicationContext(), "Veuillez entrez des données valides", Toast.LENGTH_SHORT).show();
            } finally {
                fichierAEcrire.close();
            }
        }

         catch (FileNotFoundException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Impossibilité d'écrire", Toast.LENGTH_SHORT).show();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Impossibilité d'écrire", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    /**
     * Lit le fichier stockageIpPort.txt qui contient l'ip et le port et le load dans les edit Text correspondant.
     * @param contexte
     * @return
     */
    private String[] lireMem(Context contexte) {
        String aLire = "";
        try {
            InputStream fichierALire = contexte.openFileInput("stockageIpPort.txt");



            if ( fichierALire != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fichierALire);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String information = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (information = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(information);
                }

                fichierALire.close();
                aLire = stringBuilder.toString();
            }
            return aLire.split("_");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Impossibilité de lire (fichier pas trouvé)", Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
            new Toast(getApplicationContext()).makeText(getApplicationContext(), "Impossibilité de lire", Toast.LENGTH_SHORT).show();

        }

        return new String[0];


    }


    /**Fonction pour generer un ArrayList de nombres premiers
    ***/
    private ArrayList<Double> prime() {

        ArrayList<Double> prime_number = new ArrayList<Double>();
        int max = 150;

        // loop through the numbers one by one
        for (int i = 1; i < max; i++) {
            boolean isPrimeNumber = true;

            // Check to see if the number is prime
            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    isPrimeNumber = false;
                    break; // exit the inner for loop
                }
            }

            // Ajouter les nombres premiers a l'ArrayList
            if (isPrimeNumber) {
                prime_number.add((double) i);
            }
        }
        return prime_number;
    }


    // Code 1 du projet de math pour calculer l'inverse modulaire
    public Double euclide_etendue(double a, double d) {
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
            r_prime = r - q * r_prime;
            x_prime = x - q * x_prime;
            y_prime = y - q * y_prime;
        }

        while (x < 0) {
            x += d;
            System.out.println(x);
        }

        return y;
    }


}
