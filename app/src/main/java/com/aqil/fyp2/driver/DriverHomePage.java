package com.aqil.fyp2.driver;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aqil.fyp2.ProfileFragment;
import com.aqil.fyp2.R;
import com.aqil.fyp2.databinding.ActivityDriverHomePageBinding;

public class DriverHomePage extends AppCompatActivity {


    ActivityDriverHomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriverHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new DriverMapsFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new DriverMapsFragment());
            } else if (item.getItemId() == R.id.payment) {
                replaceFragment(new DriverPayment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment((new ProfileFragment()));
            }

            return (true);
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }
}