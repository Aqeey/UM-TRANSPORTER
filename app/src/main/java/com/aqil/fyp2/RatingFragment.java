package com.aqil.fyp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;



public class RatingFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        RatingBar rateBarFeedback = view.findViewById(R.id.ratingBar);
        EditText etFeedback = view.findViewById(R.id.etRating);
        TextView tvRating = view.findViewById(R.id.tvRating);
        Button btnFeedback = view.findViewById(R.id.buttonrating);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Thanks for feedback!!!";

                if (etFeedback.getText().toString().isEmpty())
                    message = message + " Your rate really helps us!!";

                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        rateBarFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvRating.setText("You have rated " + rating);
            }
        });

        return view;
    }
}