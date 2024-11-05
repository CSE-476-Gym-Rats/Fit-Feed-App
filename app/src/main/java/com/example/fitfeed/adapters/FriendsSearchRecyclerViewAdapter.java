package com.example.fitfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.R;

import java.util.List;

/**
 * RecyclerViewAdapter for FriendActivity's user search results.
 */
public class FriendsSearchRecyclerViewAdapter extends RecyclerView.Adapter<FriendsSearchRecyclerViewAdapter.ViewHolder> {
    private List<String> data;
    private LayoutInflater inflater;

    public FriendsSearchRecyclerViewAdapter(Context context, List<String> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public FriendsSearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row_friend_search, parent, false);
        return new FriendsSearchRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsSearchRecyclerViewAdapter.ViewHolder holder, int position) {
        // set text for each user result
        holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.friendSearchTextView);
        }
    }
}
