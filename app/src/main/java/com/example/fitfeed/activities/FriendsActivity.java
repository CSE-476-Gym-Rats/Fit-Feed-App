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
import com.example.fitfeed.util.TokenManager;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private FriendsSearchRecyclerViewAdapter friendsSearchRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TokenManager.init(this);

        // Get the logged-in username from TokenManager
        String loggedInUsername = TokenManager.getUsername();
        if (loggedInUsername == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return; // Exit the activity
        }

        // Initialize the UI
        setContentView(R.layout.activity_friends);

        EditText friendUsernameEditText = findViewById(R.id.editTextFriendUsername); // Friend's username input
        Button addFriendButton = findViewById(R.id.buttonFriendsSearch);

        // Set up the button click listener
        addFriendButton.setOnClickListener(v -> {
            String friendUsername = friendUsernameEditText.getText().toString().trim();

            if (!friendUsername.isEmpty()) {
                // Use the logged-in username automatically
                addFriend(loggedInUsername, friendUsername);
            } else {
                Toast.makeText(this, "Please enter a friend's username.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FriendsActivity.this, "Unable to add friend: User does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}