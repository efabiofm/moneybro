package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFunctions mFunctions;
    private TextView perfilNombre;
    private TextView perfilEmail;
    private TextView perfilPrestamos;
    private TextView perfilDeudas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Perfil");

        perfilNombre = findViewById(R.id.perfilNombre);
        perfilEmail = findViewById(R.id.perfilEmail);
        perfilPrestamos = findViewById(R.id.perfilPrestamos);
        perfilDeudas = findViewById(R.id.perfilDeudas);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String nombre = task.getResult().getData().get("name").toString();
                            String email = task.getResult().getData().get("email").toString();
                            perfilNombre.setText(nombre);
                            perfilEmail.setText(email);
                        }
                    }
                });

        db.collection("transactions").whereArrayContains("showTo", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int totalPrestamos = 0;
                            int totalDeudas = 0;
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Map<String, Object> t = documentSnapshot.getData();
                                if (t.get("type").equals("Deuda")) {
                                    totalDeudas += Integer.parseInt(t.get("amount").toString());
                                } else if (t.get("type").equals("Préstamo")) {
                                    totalPrestamos += Integer.parseInt(t.get("amount").toString());
                                }
                            }
                            perfilDeudas.setText("₡" + totalDeudas);
                            perfilPrestamos.setText("₡" + totalPrestamos);
                        }
                    }
                });
//        Map<String, Object> params = new HashMap<>();
//        params.put("userId", uid);
//        mFunctions
//                .getHttpsCallable("getTotalUserTransactions")
//                .call(params)
//                .continueWith(new Continuation<HttpsCallableResult, Object>() {
//                    @Override
//                    public Object then(@NonNull Task<HttpsCallableResult> task) {
//                        if (task.isSuccessful()) {
//                            Object data = task.getResult().getData();
//                            Log.d("Result", data.toString());
//                        }
//                        return null;
//                    }
//                });
    }
}
