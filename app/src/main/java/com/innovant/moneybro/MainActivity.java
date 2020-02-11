package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        // If user is already logged in, go to home
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }

    public void login(View view) {
        final Button loginBtn = findViewById(R.id.loginBtn);
        EditText emailField = findViewById(R.id.loginEmailField);
        EditText passwordField = findViewById(R.id.loginPassField);
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();

        if (TextUtils.isEmpty(emailValue)) {
            emailField.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(passwordValue)) {
            passwordField.setError("Password is required");
            return;
        }

        loginBtn.setEnabled(false);

        mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginBtn.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish(); // Prevents going back to login
                        } else {
                            Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goToSignup(View view) {
        startActivity(new Intent(MainActivity.this, SignupActivity.class));
    }
}
