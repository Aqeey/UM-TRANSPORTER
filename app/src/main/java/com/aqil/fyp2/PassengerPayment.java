package com.aqil.fyp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PassengerPayment extends Fragment {

    private ImageView paymentImageView, btnConfirm;
    private FirebaseUser firebaseUser;
    private Button donepayment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_payment, container, false);

        donepayment = view.findViewById(R.id.payment);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set an onClickListener for the "Make Payment" button
        donepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to handle the payment process
                makePayment();
            }
        });

        return view;
    }

    private void makePayment() {
        // Placeholder logic for making the payment
        // Replace this with your actual payment processing logic

        // Show a toast message indicating that the payment is done
        showToast("Payment Done!");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
