package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void signup(View view) {
        final Button signupBtn = findViewById(R.id.signupBtn);
        EditText nameField = findViewById(R.id.singupNameField);
        EditText phoneField = findViewById(R.id.signupPhoneField);
        EditText passConfirmField = findViewById(R.id.signupPassConfirmField);
        EditText emailField = findViewById(R.id.signupEmailField);
        EditText passwordField = findViewById(R.id.signupPassField);

        final String nameValue = nameField.getText().toString();
        final String phoneValue = phoneField.getText().toString();
        String passConfirmValue = passConfirmField.getText().toString();
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();

        if (TextUtils.isEmpty(nameValue)) {
            nameField.setError("Nombre requerido");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            emailField.setError("Formato de correo inválido");
            return;
        }

        if (TextUtils.isEmpty(phoneValue)) {
            phoneField.setError("Teléfono requerido");
            return;
        }

        if (passwordValue.length() < 6) {
            passwordField.setError("La contraseña debe contener al menos 6 dígitos");
            return;
        }

        if (!isAlphanumeric(passwordValue)) {
            passwordField.setError("La contraseña debe contener al menos una letra y un número");
            return;
        }

        if (!TextUtils.equals(passwordValue, passConfirmValue)) {
            passwordField.setError("Los valores no coinciden");
            passConfirmField.setError("Los valores no coinciden");
            return;
        }

        signupBtn.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            DocumentReference docRef = db.collection("users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", nameValue);
                            user.put("phone", phoneValue);
                            docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    signupBtn.setEnabled(true);
                                    Toast.makeText(SignupActivity.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                    finish(); // Prevents going back to signup
                                }
                            });
                        } else {
                            Toast.makeText(SignupActivity.this, "El registro falló", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goToLogin(View view) {
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
    }

    public boolean isAlphanumeric(String str) {
        String numericPattern = ".*[0-9].*";
        String letterPattern = ".*[A-Za-z].*";
        return str.matches(numericPattern) && str.matches(letterPattern);
    }
}
