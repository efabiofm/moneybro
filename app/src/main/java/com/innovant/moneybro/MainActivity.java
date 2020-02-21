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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button loginBtn;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        emailField = findViewById(R.id.loginEmailField);
        passwordField = findViewById(R.id.loginPassField);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        // If user is already logged in, go home
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }

    public void login(View view) {
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();

        if (isFormValid()) {
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
    }

    public Boolean isFormValid() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            return false;
        }
        return true;
    }

    public void goToSignup(View view) {
        startActivity(new Intent(MainActivity.this, SignupActivity.class));
    }
}
