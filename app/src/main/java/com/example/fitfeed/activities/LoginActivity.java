package com.example.fitfeed.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitfeed.R;

public class LoginActivity extends AppCompatActivity {

    // TODO Not used currently since login is not implemented
    private String username;
    private String password;
    private boolean rememberMe;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        rememberMeCheckBox = findViewById(R.id.checkBox);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(this::goToHome);
    }

    public void goToHome(View view) {
        EditText username = findViewById(R.id.editTextUsername);
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.fitfeed", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username.getText().toString());
        editor.apply();
        skipLogin(); // TODO only skip login if remember me checked
    }

    private void skipLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}