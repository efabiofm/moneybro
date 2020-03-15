package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class HistoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Map<String, Object>> history;
    private ListView listView;
    private Context context = this;
    private MaterialDatePicker inicioPicker;
    private MaterialDatePicker finPicker;
    private EditText inicioInput;
    private EditText finInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Historial");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.history_list);
        inicioInput = findViewById(R.id.inicioInput);
        finInput = findViewById(R.id.finInput);

        inicioPicker = buildPicker(inicioInput);
        finPicker = buildPicker(finInput);

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

    private MaterialDatePicker buildPicker(final EditText input) {
        MaterialDatePicker picker;
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());
        picker = builder.build();
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Long dateLong = Long.parseLong(selection.toString());
                String dateString = formatter.format(new Date(dateLong));
                input.setText(dateString);
            }
        });
        return picker;
    }

    public void setFechaInicio(View v) {
        inicioPicker.show(getSupportFragmentManager(), inicioPicker.toString());
    }

    public void setFechaFin(View v) {
        finPicker.show(getSupportFragmentManager(), finPicker.toString());
    }

    public void filtrar(View v) {
        if (history.size() > 0 && !inicioInput.getText().toString().isEmpty() && !finInput.getText().toString().isEmpty()) {
            List<Map<String, Object>> listaFiltrada = new ArrayList<>();
            Long inicio = (Long) inicioPicker.getSelection();
            Long fin = (Long) finPicker.getSelection();
            Date fechaInicio = new Date(inicio);
            Date fechaFin = new Date(fin);
            for (Map<String, Object> t : history) {
                Timestamp deadline = (Timestamp) t.get("deadline");
                Date fechaDeadline = deadline.toDate();
                if (fechaInicio.before(fechaDeadline) && fechaFin.after(fechaDeadline)) {
                    listaFiltrada.add(t);
                }
            }
            CustomAdapter ca = new CustomAdapter(context, listaFiltrada);
            listView.setAdapter(ca);
        }
    }
}
