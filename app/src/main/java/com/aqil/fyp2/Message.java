package com.aqil.fyp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.aqil.fyp2.driver.DriverLogin;
import com.google.firebase.auth.FirebaseAuth;



public class Message extends Fragment {

    TextView logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        logout = view.findViewById(R.id.logout);

        logout.setOnClickListener(v -> logoutUser());



        return view;
    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), DriverLogin.class));
        getActivity().finish();
    }


        }

