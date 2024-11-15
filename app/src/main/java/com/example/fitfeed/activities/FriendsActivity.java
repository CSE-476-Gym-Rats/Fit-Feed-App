package com.example.fitfeed.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.adapters.FriendsSearchRecyclerViewAdapter;
import com.example.fitfeed.R;
import com.example.fitfeed.util.APIManager;

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

        // Initialize RecyclerView with placeholder data
        ArrayList<String> postText = new ArrayList<>();
        postText.add("Username1");
        postText.add("Username2");
        postText.add("Username3");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFriendsSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsSearchRecyclerViewAdapter = new FriendsSearchRecyclerViewAdapter(this, postText);
        recyclerView.setAdapter(friendsSearchRecyclerViewAdapter);
        recyclerView.setSaveEnabled(true);

        // Initialize EditText fields and button
        EditText usernameEditText = findViewById(R.id.editTextText);  // EditText for your own username
        EditText friendUsernameEditText = findViewById(R.id.editTextFriendUsername);  // EditText for friend's username
        Button addFriendButton = findViewById(R.id.buttonFriendsSearch);

        // Set button click listener to add a friend
        addFriendButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String friendUsername = friendUsernameEditText.getText().toString().trim();

            if (!username.isEmpty() && !friendUsername.isEmpty()) {
                // Call the API to add a friend
                addFriend(username, friendUsername);
            } else {

            }
        });
    }

    // Method to handle adding a friend via the API (no token used)
    private void addFriend(String username, String friendUsername) {
        // Call the APIManager's AddFriend method
        APIManager.AddFriend(username, friendUsername, new APIManager.AddFriendCallback() {
            @Override
            public void onAddFriendResult(int statusCode) {
                // Handle success or failure of the add friend request
                if (statusCode == 1) {
                    Toast.makeText(FriendsActivity.this, "Friend added successfully!", Toast.LENGTH_SHORT).show();
                } else if (statusCode == -1) {
                    Toast.makeText(FriendsActivity.this, "Failed to add friend. Check your connection.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FriendsActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
