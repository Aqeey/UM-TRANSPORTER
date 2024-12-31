package com.aqil.fyp2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {


    private Button logout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


            logout = view.findViewById(R.id.logout);

            logout.setOnClickListener(v -> logoutUser());

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");
        String matrics = preferences.getString("matrics", "");

        // Example: Display user information in TextViews
        TextView usernameTextView = view.findViewById(R.id.name);
        TextView emailTextView = view.findViewById(R.id.email);
        TextView passwordTextView = view.findViewById(R.id.password);
        TextView matricsTextView = view.findViewById(R.id.matricsnumber);

        usernameTextView.setText(username);
        emailTextView.setText(email);
        passwordTextView.setText(password);
        matricsTextView.setText(matrics);



            return view;
        }
        private void logoutUser() {
            FirebaseAuth.getInstance().signOut();
            FirebaseDatabase.getInstance().goOffline();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }



}