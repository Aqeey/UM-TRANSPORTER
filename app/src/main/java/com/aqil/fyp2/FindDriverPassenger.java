package com.aqil.fyp2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FindDriverPassenger extends AppCompatActivity {

    String[] location = {"kk13", "kk10", "kk9", "DTC", "KPS", "LIBRARY"};
    String[] destination = {"kk13", "kk10", "kk9", "DTC", "KPS", "LIBRARY"};
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompletedestination;
    ArrayAdapter<String> adapterPickupLocation;
    ArrayAdapter<String> adapterDestination;
    Button findDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_driver_passenger);

        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        autoCompletedestination = findViewById(R.id.destination);
        findDriver = findViewById(R.id.finddriver);

        adapterPickupLocation = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, location);
        autoCompleteTextView.setAdapter(adapterPickupLocation);

        adapterDestination = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, destination);
        autoCompletedestination.setAdapter(adapterDestination);

        findDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pickupLocation = autoCompleteTextView.getText().toString();
                String destination = autoCompletedestination.getText().toString();

                if (!pickupLocation.isEmpty() && !destination.isEmpty()) {
                    saveToDatabase(pickupLocation, destination);
                    Toast.makeText(FindDriverPassenger.this, "Pickup Location and Destination Successfully Entered!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindDriverPassenger.this, "Please select both Location and Destination", Toast.LENGTH_SHORT).show();
                }
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Handle item click if needed
            }
        });

        autoCompletedestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Handle item click if needed
            }
        });
    }
    // Passenger.java
// Passenger.java
    public class Passenger {
        private String pickupLocation;
        private String destination;

        // Constructors, getters, and setters
        // ...

        public Passenger() {
            // Default constructor required for calls to DataSnapshot.getValue(Passenger.class)
        }

        public String getPickupLocation() {
            return pickupLocation;
        }

        public void setPickupLocation(String pickupLocation) {
            this.pickupLocation = pickupLocation;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }
    }


    private void saveToDatabase(String pickupLocation, String destination) {
        String passengerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference passengerRef = FirebaseDatabase.getInstance().getReference().child("Passengers Request").child(passengerId);

        Passenger passenger = new Passenger();
        passenger.setPickupLocation(pickupLocation);
        passenger.setDestination(destination);

        passengerRef.setValue(passenger);
    }
}