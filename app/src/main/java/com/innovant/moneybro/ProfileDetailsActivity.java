package com.innovant.moneybro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileDetailsActivity extends AppCompatActivity {
    private TextInputEditText perfilNombre;
    private TextInputEditText perfilTelefono;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        setTitle("Editar Perfil");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        perfilNombre = findViewById(R.id.profileNameInput);
        perfilTelefono = findViewById(R.id.profilePhoneInput);
        perfilNombre.setText(bundle.getString("name"));
        perfilTelefono.setText(bundle.getString("phone"));
    }

    public void cancelarPerfil(View v) {
        finish();
    }

    public void guardarPerfil(View v) {
        String newName = perfilNombre.getText().toString();
        String newPhone = perfilTelefono.getText().toString();
        db.collection("users").document(mAuth.getUid()).update("name", newName, "phone", newPhone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileDetailsActivity.this, "Perfil actualizado", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ProfileDetailsActivity.this, ProfileActivity.class));
                    }
                });
    }
}
