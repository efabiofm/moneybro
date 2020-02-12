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
    private Button signupBtn;
    private EditText nameField ;
    private EditText phoneField;
    private EditText passConfirmField;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signupBtn = findViewById(R.id.signupBtn);
        nameField = findViewById(R.id.singupNameField);
        phoneField = findViewById(R.id.signupPhoneField);
        passConfirmField = findViewById(R.id.signupPassConfirmField);
        emailField = findViewById(R.id.signupEmailField);
        passwordField = findViewById(R.id.signupPassField);
    }

    public void signup(View view) {
        final String nameValue = nameField.getText().toString();
        final String phoneValue = phoneField.getText().toString();
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();

        if (isFormValid()) {
            signupBtn.setEnabled(false);

            mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", nameValue);
                                user.put("phone", phoneValue);
                                createUser(user);
                            } else {
                                Toast.makeText(SignupActivity.this, "El registro falló", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void createUser(Map<String, Object> user) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    signupBtn.setEnabled(true);
                    Toast.makeText(SignupActivity.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                    finish(); // Prevents going back to signup
                } else {
                    Toast.makeText(SignupActivity.this, "El registro falló", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Boolean isFormValid() {
        String name = nameField.getText().toString();
        String phone = phoneField.getText().toString();
        String confirmPassword = passConfirmField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (TextUtils.isEmpty(name)) {
            nameField.setError("Nombre requerido");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Formato de correo inválido");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneField.setError("Teléfono requerido");
            return false;
        }

        if (password.length() < 6) {
            passwordField.setError("La contraseña debe contener al menos 6 dígitos");
            return false;
        }

        if (!isAlphanumeric(password)) {
            passwordField.setError("La contraseña debe contener al menos una letra y un número");
            return false;
        }

        if (!TextUtils.equals(password, confirmPassword)) {
            passwordField.setError("Los valores no coinciden");
            passConfirmField.setError("Los valores no coinciden");
            return false;
        }
        return true;
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
