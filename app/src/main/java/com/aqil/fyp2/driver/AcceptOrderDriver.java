package com.aqil.fyp2.driver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aqil.fyp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AcceptOrderDriver extends AppCompatActivity {


        // Assuming you have UI elements to display pickup location and destination
      private TextView pickupLocationTextView;
      private   TextView destinationTextView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_accept_order_driver);

            pickupLocationTextView = findViewById(R.id.pickupLocationTextView);
            destinationTextView = findViewById(R.id.destinationTextView);

            // Get the current user's ID
            String passengerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Reference to the passenger's data in the database
            DatabaseReference passengerRef = FirebaseDatabase.getInstance().getReference().child("Passengers Request").child(passengerId);

            // Listen for changes in the database
            passengerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Retrieve pickup location and destination from the database
                    String pickupLocation = dataSnapshot.child("pickupLocation").getValue(String.class);
                    String destination = dataSnapshot.child("destination").getValue(String.class);

                    // Update UI with the retrieved data
                    pickupLocationTextView.setText("Pickup Location: " + pickupLocation);
                    destinationTextView.setText("Destination: " + destination);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors if needed
                }
            });
        }
    }



