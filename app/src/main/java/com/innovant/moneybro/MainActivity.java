package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    }

    public void login(View view) {
        Button loginBtn = findViewById(R.id.loginBtn);
        EditText emailField = findViewById(R.id.loginEmailField);
        EditText passwordField = findViewById(R.id.loginPassField);
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();
        loginBtn.setEnabled(false);

        // add input validations
        mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
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
