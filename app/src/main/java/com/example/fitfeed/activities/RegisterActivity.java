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

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private Button registerButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        emailEditText = findViewById(R.id.editTextEmail);
        cancelButton = findViewById(R.id.buttonCancel);
        registerButton = findViewById(R.id.buttonCreateAccount);
        cancelButton.setOnClickListener(v -> {
            finish();
        });
        registerButton.setOnClickListener(this::register);
    }

    public void register(View view) {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, R.string.missing_required_field, Toast.LENGTH_SHORT).show();
            return;
        }

        if (firstName.isEmpty()) {
            Toast.makeText(this, R.string.missing_required_field, Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastName.isEmpty()) {
            Toast.makeText(this, R.string.missing_required_field, Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, R.string.missing_required_field, Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, R.string.missing_required_field, Toast.LENGTH_SHORT).show();
            return;
        }

        APIManager.Register(firstName, lastName, username, email, password, this, success -> {
            switch (success) {
                case -1: {
                    RegisterActivity.this.registerError();
                    break;
                }
                case 0: {
                    RegisterActivity.this.registerFail();
                    break;
                }
                case 1: {
                    RegisterActivity.this.registerSuccess();
                    break;
                }
            }
        });
    }

    private void registerSuccess() {
        Toast.makeText(this, R.string.successfully_created_account, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void registerFail() {
        Toast.makeText(this, R.string.username_already_in_use, Toast.LENGTH_SHORT).show();
    }

    private void registerError() {
        Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
    }

}