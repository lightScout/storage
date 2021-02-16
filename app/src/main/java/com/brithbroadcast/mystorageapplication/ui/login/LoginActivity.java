package com.brithbroadcast.mystorageapplication.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.brithbroadcast.mystorageapplication.MainActivity;
import com.brithbroadcast.mystorageapplication.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    // |--> Shared Preferences & Encrypted Shared Preferences <--|

    // Shared Preferences Key
    private final String PREFERENCE_KEY = "LOGIN_PREF";
    @BindView(R.id.username_edittext)
    public EditText usernameEditText;
    @BindView(R.id.password_edittext)
    public EditText passwordEditText;
    // Shared Preferences
    private SharedPreferences sharedPreferences;


    // |--> UI <--|
    // Encrypted Shared Preferences
    private SharedPreferences encryptedSharedPreferences;
    // Encryption Key
    private MasterKey masterKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        // Retrieving shared preferences from context
        sharedPreferences = getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);

        // Encrypted Shared Preferences
        // Mandatory try/catch statement
        try {
            // Creating Master Key for encryption
            masterKey = new MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            // Defining encrypted shared preferences
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    getPackageName(), // File name
                    masterKey, // Master ney
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Key scheme
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Value scheme
            );

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @OnClick(R.id.sign_in_button)
    public void onClick(View view) {
        // Data coming from the user
        String userName = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        // Writing data to shared preferences
        sharedPreferences.edit().putString("USER_NAME", userName)
                .apply();
        sharedPreferences.edit().putString("PASSWORD", password)
                .apply();

        // Writing data to encrypted shared preferences
        encryptedSharedPreferences.edit().putString("USER_NAME", userName)
                .apply();
        encryptedSharedPreferences.edit().putString("PASSWORD", password)
                .apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        setResult(707, intent);
        finish();

    }

}


