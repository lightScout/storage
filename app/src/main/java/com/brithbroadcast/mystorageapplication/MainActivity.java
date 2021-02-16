package com.brithbroadcast.mystorageapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.brithbroadcast.mystorageapplication.ui.login.LoginActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // "@" - stands for Annotation, as in code simplification
    // Binding using ButterKnife
    // Implicit
    @BindView(R.id.observation_edittext)
    public EditText observationEditText;
    @BindView(R.id.previous_obv_textview)
    public TextView previousObservationsText;
    @BindView(R.id.main_layout)
    public ConstraintLayout mainLayout;
    private SharedPreferences sharedPreferences;
    // Encrypted Shared Preferences
    private SharedPreferences encryptedSharedPreferences;
    // Encryption Key
    private MasterKey masterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Retrieving shared preferences from context
        // Context.MODE_PRIVATE used when dealing with shared preferences
        // Shared preferences as a Key Value pair
        sharedPreferences = getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);


        // Encrypted Shared Preferences
        try {
            // Creating Master Key for encryption
            masterKey = new MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            // Defining encrypted shared preferences
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    getPackageName(),
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Tracking the if the app was opened for the first time
        if (sharedPreferences.getBoolean("FIRST_TIME", true)) {
            Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
            // Instantiating the intent
            // intent = to show the login activity
            // if it is the first time the app is running on the device
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // Triggering the intent
            startActivityForResult(intent, 707);
            sharedPreferences.edit().putBoolean("FIRST_TIME", false).apply();
        } else
            Toast.makeText(this, "Welcome back", Toast.LENGTH_LONG).show();
        readFromSharedPreferences();
        sharedPreferences.edit().putBoolean("FIRST_TIME", true).apply();


    }

    // Binding using ButterKnife
    // Direct
    //
    // Saving to shared preferences
    @OnClick(R.id.save_button)
    public void onClick(View view) {

        String observation = observationEditText.getText().toString().trim();
        observationEditText.setText("");
        // Grabbing the date from the system
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        // Formatting the date
        String currentTime = simpleDateFormat.format(new Date());
        // Saving previous value before writing new to add continuum
        String current = sharedPreferences.getString("KEY", "");

        // Writing data to shared preferences
        sharedPreferences.edit().putString("KEY", current + "\n" + currentTime + " - " + observation)
                .apply();

        // Writing data to encrypted shared preferences
        encryptedSharedPreferences.edit().putString("KEY", current + "\n" + currentTime + " - " + observation)
                .apply();

        readFromSharedPreferences();

    }

    // Reading from shared preferences
    private void readFromSharedPreferences() {
        String observations = sharedPreferences.getString("KEY", "No observations");
        previousObservationsText.setText(observations);
    }

}