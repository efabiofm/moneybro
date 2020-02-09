package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
    }

    public void signup(View view) {
        // disable button
        EditText emailField = findViewById(R.id.signupEmailField);
        EditText passwordField = findViewById(R.id.signupPassField);
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();
        // add input validations
        mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // add extra data to user
                            Toast.makeText(SignupActivity.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                            // go to login
                        } else {
                            Toast.makeText(SignupActivity.this, "El registro falló", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goToLogin(View view) {
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
    }
}
