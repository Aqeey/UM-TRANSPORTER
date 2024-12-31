package com.aqil.fyp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private RelativeLayout findDriver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findDriver = view.findViewById(R.id.finddriver);

        // Set an OnClickListener for the RelativeLayout
        findDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the action you want when the RelativeLayout is clicked
                // For example, start another activity
                Intent intent = new Intent(getActivity(), FindDriverPassenger.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
