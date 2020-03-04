package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransactionDetailsActivity extends AppCompatActivity {
    private String transactionId;
    private TextView state;
    private TextView from;
    private TextView with;
    private TextView amount;
    private TextView deadline;
    private TextView category;
    private MaterialButton confirm;
    private MaterialButton reject;
    private MaterialButton delete;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        String uid = mAuth.getCurrentUser().getUid();
        setTitle(bundle.getString("type"));

        transactionId = bundle.getString("id");

        state = findViewById(R.id.tdState);
        from = findViewById(R.id.tdFromValue);;
        with = findViewById(R.id.tdWithValue);;
        amount = findViewById(R.id.tdAmountValue);;
        deadline = findViewById(R.id.tdDeadlineValue);;
        category = findViewById(R.id.tdCategoryValue);;

        confirm = findViewById(R.id.tdConfirmBtn);
        reject = findViewById(R.id.tdRejectBtn);
        delete = findViewById(R.id.tdCancelBtn);

        state.setText(bundle.getString("state"));
        from.setText(bundle.getString("creatorName"));
        with.setText(bundle.getString("receiverName"));
        amount.setText("₡" + bundle.getString("amount"));
        deadline.setText(bundle.getString("deadline"));
        category.setText(bundle.getString("category"));

        // Hide confirm/delete buttons from creator
        if (uid.equals(bundle.getString("creatorId")) || !bundle.getString("state").equals("Pendiente")) {
            confirm.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        }

        // Only show delete button to creator
        if (uid.equals(bundle.get("receiverId"))) {
            delete.setVisibility(View.GONE);
        }
    }

    public void confirmTransaction(View view) {
        db.collection("transactions")
                .document(transactionId)
                .update("state", "Confirmado")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()) {
                          Toast.makeText(TransactionDetailsActivity.this, "Transacción confirmada", Toast.LENGTH_LONG).show();
                          startActivity(new Intent(TransactionDetailsActivity.this, HomeActivity.class));
                      }
                    }
                });
    }

    public void rejectTransaction(View view) {
        db.collection("transactions")
                .document(transactionId)
                .update("state", "Rechazado")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TransactionDetailsActivity.this, "Transacción rechazada", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(TransactionDetailsActivity.this, HomeActivity.class));
                        }
                    }
                });
    }

    public void cancelTransaction(View view) {
        db.collection("transactions")
                .document(transactionId)
                .update("state", "Cancelado")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TransactionDetailsActivity.this, "Transacción cancelada", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(TransactionDetailsActivity.this, HomeActivity.class));
                        }
                    }
                });
    }
}
