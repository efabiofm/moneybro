package com.innovant.moneybro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ajithvgiri.searchdialog.OnSearchItemSelected;
import com.ajithvgiri.searchdialog.SearchListItem;
import com.ajithvgiri.searchdialog.SearchableDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ScrollView formScroll;

    private TextView userSelectedText;
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
    private QuerySnapshot usuarios;
    private SearchableDialog buscadorUsuarios;
    private int userSelectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        setTitle("Nueva transacción");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        formScroll = findViewById(R.id.transaction_scrollview);

        deadlineInput = findViewById(R.id.deadlineInputText);
        moneyInput = findViewById(R.id.moneyInputText);
        interestInput = findViewById(R.id.interestInputText);
        userSelectedText = findViewById(R.id.userSelected);

        transactionSpinner = findViewById(R.id.transactions_spinner);
        remindersSpinner = findViewById(R.id.reminders_spinner);
        categoriesSpinner = findViewById(R.id.categories_spinner);

        setSpinnerAdapter(transactionSpinner, R.array.transaction_types);
        setSpinnerAdapter(remindersSpinner, R.array.reminders_frequency);
        setSpinnerAdapter(categoriesSpinner, R.array.transaction_category);

        emailCheckbox = findViewById(R.id.emailCheckbox);
        pushCheckbox = findViewById(R.id.pushCheckbox);
        smsCheckbox = findViewById(R.id.smsCheckbox);

        botonCrear = findViewById(R.id.transactionSubmitBtn);
        interestInput.setText("0");

        db = FirebaseFirestore.getInstance();
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        usuarios = task.getResult();
                        initSearchBar();
                    }
                });
        initDatePicker();
    }

    private void initDatePicker() {
        builder = MaterialDatePicker.Builder.datePicker();
        constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());
        picker = builder.build();
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                deadlineInput.setError(null);
            }
        });
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Long dateLong = Long.parseLong(selection.toString());
                String dateString = formatter.format(new Date(dateLong));
                deadlineInput.setText(dateString);
            }
        });
    }

    private void initSearchBar() {
        List<SearchListItem> searchListItems = new ArrayList<>();
        for(int i=0; i < usuarios.size(); i++) {
            String name = usuarios.getDocuments().get(i).get("name").toString();
            searchListItems.add(new SearchListItem(i, name));
        }
        buscadorUsuarios = new SearchableDialog(this, (ArrayList<SearchListItem>) searchListItems, "Usuarios");
        buscadorUsuarios.setOnItemSelected(new OnSearchItemSelected() {
            @Override
            public void onClick(int i, SearchListItem searchListItem) {
                userSelectedText.setText(searchListItem.getTitle());
                userSelectedText.setTextColor(Color.BLACK);
                userSelectedIndex = i;
                buscadorUsuarios.dismiss();
            }
        });
        userSelectedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscadorUsuarios.show();
            }
        });
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
        Map<String, Object> transaccion = getObjetoTransaccion();
        if (transaccion != null) {
            botonCrear.setEnabled(false);
            guardarTransaccionFirebase(transaccion);
        }
    }

    private Map<String, Object> getObjetoTransaccion() {
        Object tipoTransaccionItem = transactionSpinner.getSelectedItem();
        String montoText = moneyInput.getText().toString();
        Object categoriaItem = categoriesSpinner.getSelectedItem();
        Object frecuenciaItem = remindersSpinner.getSelectedItem();

        if (!isFormValid(tipoTransaccionItem, montoText, categoriaItem, frecuenciaItem)) {
            return null;
        }

        String tipoTransaccion = tipoTransaccionItem.toString();
        int monto = Integer.parseInt(montoText);
        Long fecha = Long.parseLong(picker.getSelection().toString());
        String categoria = categoriaItem.toString();
        String interesText = interestInput.getText().toString();
        int interes = interesText.isEmpty() ? 0 : Integer.parseInt(interesText);
        boolean valorEmailCheckbox = Boolean.parseBoolean(emailCheckbox.getText().toString());
        boolean valorPushCheckbox = Boolean.parseBoolean(pushCheckbox.getText().toString());
        boolean valorSmsCheckbox = Boolean.parseBoolean(smsCheckbox.getText().toString());
        String frecuenciaRecordatorios = remindersSpinner.getSelectedItem().toString();
        String creatorId = mAuth.getCurrentUser().getUid();
        String receiverId = usuarios.getDocuments().get(userSelectedIndex).getId();

        Map<String, Object> transaccion = new HashMap<>();
        transaccion.put("creatorId", creatorId);
        transaccion.put("type", tipoTransaccion);
        transaccion.put("amount", monto);
        transaccion.put("interest", interes);
        transaccion.put("deadline", new Timestamp(new Date(fecha)));
        transaccion.put("emailNotifications", valorEmailCheckbox);
        transaccion.put("pushNotifications", valorPushCheckbox);
        transaccion.put("smsNotifications", valorSmsCheckbox);
        transaccion.put("remindersFrequency", frecuenciaRecordatorios);
        transaccion.put("category", categoria);
        transaccion.put("receiverName", userSelectedText.getText().toString());
        transaccion.put("state", "Pendiente");
        transaccion.put("receiverId", receiverId);
        transaccion.put("showTo", new ArrayList<>(Arrays.asList(creatorId, receiverId)));

        for (QueryDocumentSnapshot doc : usuarios) {
            if (doc.getId().equals(creatorId)) {
                String creatorName = doc.getData().get("name").toString();
                transaccion.put("creatorName", creatorName);
            }
        }
        return transaccion;
    }

    private void guardarTransaccionFirebase(Map<String, Object> transaccion) {
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

    public boolean isFormValid(Object tipoTransaccion, String monto, Object categoria, Object frecuencia) {
        Long hoy = new Date().getTime();
        if (userSelectedIndex == -1) {
            userSelectedText.setTextColor(Color.RED);
            formScroll.fullScroll(ScrollView.FOCUS_UP);
            return false;
        }
        if (tipoTransaccion == null) {
            ((TextView) transactionSpinner.getSelectedView()).setError("Tipo requerido");
            formScroll.fullScroll(ScrollView.FOCUS_UP);
            return false;
        }
        if (monto.isEmpty() || Integer.parseInt(monto) <= 0) {
            moneyInput.setError("Monto inválido");
            return false;
        }
        if (deadlineInput.getText().toString().isEmpty() || Long.parseLong(picker.getSelection().toString()) < hoy) {
            deadlineInput.setError("Fecha inválida");
            return false;
        }
        if (categoria == null) {
            ((TextView) categoriesSpinner.getSelectedView()).setError("Categoría requerida");
            return false;
        }
        if (frecuencia == null) {
            ((TextView) remindersSpinner.getSelectedView()).setError("Frecuencia requerida");
            return false;
        }
        return true;
    }
}
