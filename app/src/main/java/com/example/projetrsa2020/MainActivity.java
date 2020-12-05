package com.example.projetrsa2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private final int FROM_THIRD_ACTIVITY = 1;

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




}
