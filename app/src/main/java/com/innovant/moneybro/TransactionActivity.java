package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {
    private TextInputEditText deadlineInput;
    private TextInputEditText moneyInput;
    private TextInputEditText interestInput;

    private MaterialCheckBox emailCheckbox;
    private MaterialCheckBox pushCheckbox;
    private MaterialCheckBox smsCheckbox;

    private Spinner transactionSpinner;
    private Spinner remindersSpinner;
    private Spinner categoriesSpinner;

    private MaterialDatePicker.Builder builder;
    private MaterialDatePicker picker;
    private CalendarConstraints.Builder constraintsBuilder;
    private Button botonCrear;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        deadlineInput = findViewById(R.id.deadlineInputText);
        moneyInput = findViewById(R.id.moneyInputText);
        interestInput = findViewById(R.id.interestInputText);

        transactionSpinner = findViewById(R.id.transactions_spinner);
        remindersSpinner = findViewById(R.id.reminders_spinner);
        categoriesSpinner = findViewById(R.id.categories_spinner);

        setSpinnerAdapter(transactionSpinner, R.array.transaction_types);
        setSpinnerAdapter(remindersSpinner, R.array.reminders_frequency);
        setSpinnerAdapter(categoriesSpinner, R.array.transaction_category);

        emailCheckbox = findViewById(R.id.emailCheckbox);
        pushCheckbox = findViewById(R.id.pushCheckbox);
        smsCheckbox = findViewById(R.id.smsCheckbox);

        builder = MaterialDatePicker.Builder.datePicker();
        constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());
        picker = builder.build();
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Long dateLong = Long.parseLong(selection.toString());
                String dateString = formatter.format(new Date(dateLong));
                deadlineInput.setText(dateString);
            }
        });

        botonCrear = findViewById(R.id.transactionSubmitBtn);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    private void setSpinnerAdapter(Spinner spinner, int array) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, array, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void openDatePicker(View view) {
        picker.show(getSupportFragmentManager(), picker.toString());
    }

    public void crearTransaccion(View view) {
        String tipoTransaccion = transactionSpinner.getSelectedItem().toString();
        int monto = Integer.parseInt(moneyInput.getText().toString());
        int interes = Integer.parseInt(interestInput.getText().toString());
        Long fecha = Long.parseLong(picker.getSelection().toString());
        boolean valorEmailCheckbox = Boolean.parseBoolean(emailCheckbox.getText().toString());
        boolean valorPushCheckbox = Boolean.parseBoolean(pushCheckbox.getText().toString());
        boolean valorSmsCheckbox = Boolean.parseBoolean(smsCheckbox.getText().toString());
        String frecuenciaRecordatorios = remindersSpinner.getSelectedItem().toString();
        String categoria = categoriesSpinner.getSelectedItem().toString();

        Map<String, Object> transaccion = new HashMap<>();
        transaccion.put("type", tipoTransaccion);
        transaccion.put("amount", monto);
        transaccion.put("interest", interes);
        transaccion.put("deadline", new Timestamp(new Date(fecha)));
        transaccion.put("emailNotifications", valorEmailCheckbox);
        transaccion.put("pushNotifications", valorPushCheckbox);
        transaccion.put("smsNotifications", valorSmsCheckbox);
        transaccion.put("remindersFrequency", frecuenciaRecordatorios);
        transaccion.put("category", categoria);

        botonCrear.setEnabled(false);

        db.collection("transactions")
                .add(transaccion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(TransactionActivity.this, "Transacción creada con éxito", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(TransactionActivity.this, HomeActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TransactionActivity.this, "Ocurrió un error inesperado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        botonCrear.setEnabled(true);
                    }
                });

    }
}
