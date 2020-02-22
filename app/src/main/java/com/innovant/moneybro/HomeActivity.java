package com.innovant.moneybro;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        listView = findViewById(R.id.transactions_list);
        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("transactions").whereEqualTo("ownerId", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> transacciones = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                Map<String, Object> transaccion = doc.getData();
                                transacciones.add(transaccion);
                            }
                            CustomAdapter customAdapter = new CustomAdapter(transacciones);
                            listView.setAdapter(customAdapter);
                        }
                    }
                });

//        ListView listView = findViewById(R.id.transactions_list);
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("android");
//        arrayList.add("ios");
//
//        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
//        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void startTransaction(View view) {
        startActivity(new Intent(HomeActivity.this, TransactionActivity.class));
    }

    class CustomAdapter extends BaseAdapter {
        private List<Map<String, Object>> transacciones;

        public CustomAdapter(List<Map<String, Object>> transacciones) {
            this.transacciones = transacciones;
        }
        @Override
        public int getCount() {
            return transacciones.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.transaction_list_row, null, false);
            TextView title = view.findViewById(R.id.tListTitle);
            TextView type = view.findViewById(R.id.tListType);
            TextView deadline = view.findViewById(R.id.tListDeadline);
            TextView amount = view.findViewById(R.id.tListAmount);

            Timestamp timestamp = (Timestamp) transacciones.get(i).get("deadline");
            Date fecha = timestamp.toDate();
            Locale locale = new Locale("es", "ES");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            title.setText(transacciones.get(i).get("userName").toString());
            type.setText(transacciones.get(i).get("type").toString());
            deadline.setText("Finaliza: " + dateFormat.format(fecha));
            amount.setText("₡" + transacciones.get(i).get("amount").toString());
            return view;
        }
    }
}
