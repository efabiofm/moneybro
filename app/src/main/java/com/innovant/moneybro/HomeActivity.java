package com.innovant.moneybro;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView noData;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        noData = findViewById(R.id.noDataText);
        noData.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBarHome);

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
        final Context context = this;

        listView = findViewById(R.id.transactions_list);
        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("transactions").whereArrayContains("showTo", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            final List<Map<String, Object>> transacciones = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                Map<String, Object> transaccion = doc.getData();
                                String state = transaccion.get("state").toString();
                                if (state.equals("Pendiente") || state.equals("Confirmado")) {
                                    transaccion.put("id", doc.getId());
                                    transacciones.add(transaccion);
                                }
                            }
                            CustomAdapter customAdapter = new CustomAdapter(context, transacciones);
                            listView.setAdapter(customAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    startActivity(createDetailsIntent(transacciones.get(position)));
                                }
                            });

                            if (transacciones.size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
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

    private Intent createDetailsIntent(Map<String, Object> transaction) {
        Intent details = new Intent(HomeActivity.this, TransactionDetailsActivity.class);
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
