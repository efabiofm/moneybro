package com.innovant.moneybro;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

public class CustomSuggestionAdapter extends SuggestionsAdapter<User, CustomSuggestionAdapter.SuggestionHolder> {
    private MaterialSearchBar searchBar;
    private TextView userView;

    public CustomSuggestionAdapter(LayoutInflater inflater, MaterialSearchBar searchBar, TextView userView) {
        super(inflater);
        this.searchBar = searchBar;
        this.userView = userView;
    }
    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwoLineListItem listItem = (TwoLineListItem) view;
                String name = listItem.getText1().getText().toString();
                userView.setText(name);
                searchBar.setText("");
                searchBar.clearSuggestions();
            }
        });
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(User suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getName());
        holder.subtitle.setText(suggestion.getEmail());
    }

    @Override
    public int getSingleViewHeight() {
        return 60;
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected TextView subtitle;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}
