package com.innovant.moneybro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> transacciones;

    public CustomAdapter(Context context, List<Map<String, Object>> transacciones) {
        this.transacciones = transacciones;
        this.context = context;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.transaction_list_row, null, false);
        TextView title = view.findViewById(R.id.tListTitle);
        TextView deadline = view.findViewById(R.id.tListDeadline);
        TextView amount = view.findViewById(R.id.tListAmount);
        TextView state = view.findViewById(R.id.tListState);

        title.setText(transacciones.get(i).get("type").toString());
        deadline.setText("Finaliza: " + Utils.formatDate(transacciones.get(i).get("deadline")));

        int monto = Integer.parseInt(transacciones.get(i).get("amount").toString());
        int interes = Integer.parseInt(transacciones.get(i).get("interest").toString());
        amount.setText("₡" + Utils.calcInterest(monto, interes));
        state.setText(transacciones.get(i).get("state").toString());
        return view;
    }
}
