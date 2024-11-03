package com.example.fitfeed.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.adapters.FriendsSearchRecyclerViewAdapter;
import com.example.fitfeed.R;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private FriendsSearchRecyclerViewAdapter friendsSearchRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friends);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TODO implement actual user searching instead of using placeholder values
        ArrayList<String> postText = new ArrayList<>();
        postText.add("Username1");
        postText.add("Username2");
        postText.add("Username3");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFriendsSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsSearchRecyclerViewAdapter = new FriendsSearchRecyclerViewAdapter(this, postText);
        recyclerView.setAdapter(friendsSearchRecyclerViewAdapter);
        recyclerView.setSaveEnabled(true);
    }
}