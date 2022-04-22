package com.example.agenda;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    EditText login, passWord;
    Button connexion;
    //SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //recuperation des valeurs du layout
        login= (EditText)findViewById(R.id.username);
        passWord=(EditText)findViewById(R.id.password);
        connexion= (Button) findViewById(R.id.submit);
        //les preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            //clique sur le bouton submit
            public void onClick(View v) {
                //si le login et mot de passe ne sont pas vides
                if((!login.getText().toString().equals("")) && (!passWord.getText().toString().equals(""))) {
                     /*
                verifier si c'est la premiére connexion
                 */
                    if (preferences.getAll().isEmpty()) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("login", login.getText().toString()); //ajout preference
                        editor.putString("passWord", passWord.getText().toString()); ///ajout  preference
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, Connexion.class);
                        startActivity(intent);
                    }
                     /*
                sinon on verifie dans les préferences si c'est le login et mot de passe enregistrés
                 */
                    else {
                        if ((preferences.getString("login", null)).equals(login.getText().toString()) && (preferences.getString("passWord", null)).equals(passWord.getText().toString())) {
                            Intent intent = new Intent(MainActivity.this, Connexion.class);
                            startActivity(intent);
                        } else
                            Toast.makeText(MainActivity.this, "Login ou mot de passe inccorect", Toast.LENGTH_SHORT).show();
                    }
                }
                else Toast.makeText(MainActivity.this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();

            }
        });
    }
}