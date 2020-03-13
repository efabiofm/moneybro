package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Map<String, Object>> history;
    private ListView listView;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Historial");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.history_list);

        db.collection("transactions").whereArrayContains("showTo", mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            history = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                Map<String, Object> transaccion = doc.getData();
                                transaccion.put("id", doc.getId());
                                history.add(transaccion);
                            }

                            CustomAdapter customAdapter = new CustomAdapter(context, history);
                            listView.setAdapter(customAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    startActivity(createDetailsIntent(history.get(position)));
                                }
                            });
                        }
                    }
                });
    }

    private Intent createDetailsIntent(Map<String, Object> transaction) {
        Intent details = new Intent(HistoryActivity.this, TransactionDetailsActivity.class);
        int monto = Integer.parseInt(transaction.get("amount").toString());
        int interes = Integer.parseInt(transaction.get("interest").toString());
        details.putExtra("id", transaction.get("id").toString());
        details.putExtra("creatorId", transaction.get("creatorId").toString());
        details.putExtra("receiverId", transaction.get("receiverId").toString());
        details.putExtra("type", transaction.get("type").toString());
        details.putExtra("creatorName", transaction.get("creatorName").toString());
        details.putExtra("receiverName", transaction.get("receiverName").toString());
        details.putExtra("state", transaction.get("state").toString());
        details.putExtra("deadline", Utils.formatDate(transaction.get("deadline")));
        details.putExtra("amount", "" + Utils.calcInterest(monto, interes));
        details.putExtra("category", transaction.get("category").toString());
        return details;
    }
}
