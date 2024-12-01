package com.example.fitfeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitfeed.R;
import com.example.fitfeed.util.APIManager;
import com.example.fitfeed.util.TokenManager;

public class LoginActivity extends AppCompatActivity {

    private String username;
    private String password;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private Button registerButton;

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
        rememberMeCheckBox = findViewById(R.id.rememberMe);
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonSignUp);

        loginButton.setOnClickListener(this::login);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // Check if credentials are saved
        String savedUsername = TokenManager.getUsername();
        String savedPassword = TokenManager.getPassword();

        // "Remember me" auto login
        if (savedUsername != null && savedPassword != null) {
            APIManager.Login(savedUsername, savedPassword, this, success -> {
                switch (success) {
                    case -1: {
                        LoginActivity.this.loginError();
                        break;
                    }
                    case 0: {
                        LoginActivity.this.loginFail();
                        break;
                    }
                    case 1: {
                        LoginActivity.this.loginSuccess();
                        break;
                    }
                }
            });
        }
    }

    public void login(View view) {
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();

        APIManager.Login(username, password, this, success -> {
            switch (success) {
                case -1: {
                    LoginActivity.this.loginError();
                    break;
                }
                case 0: {
                    LoginActivity.this.loginFail();
                    break;
                }
                case 1: {
                    LoginActivity.this.loginSuccess();
                    break;
                }
            }
        });
    }

    private void loginSuccess() {
        // Remember me
        if (rememberMeCheckBox.isChecked()) {
            TokenManager.rememberMe(username, password);
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loginFail() {
        Toast.makeText(this, R.string.incorrect_username_or_password, Toast.LENGTH_SHORT).show();
    }

    private void loginError() {
        Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
    }
}