package com.innovant.moneybro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

public class CustomSuggestionAdapter extends SuggestionsAdapter<User, CustomSuggestionAdapter.SuggestionHolder> {
    public CustomSuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }
    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(User suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getEmail());
    }

    @Override
    public int getSingleViewHeight() {
        return 60;
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder{
        protected TextView title;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
        }
    }
}
