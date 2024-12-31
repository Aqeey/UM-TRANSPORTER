package com.aqil.fyp2.driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aqil.fyp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class DriverRegister extends AppCompatActivity {

    private EditText editEmail, editPassword, editUsername, editMatric, editPlate;
    private Button registerButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        editUsername = findViewById(R.id.username);
        editMatric = findViewById(R.id.matrics);
        editPlate = findViewById(R.id.platenum);
        registerButton = findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(DriverRegister.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(true);

        registerButton.setOnClickListener(v -> {
            if (editUsername.getText().length() > 0 && editEmail.getText().length() > 0 && editPassword.getText().length() > 0
                    && editMatric.getText().length() > 0 && editPlate.getText().length() > 0) {
                register(editUsername.getText().toString(), editEmail.getText().toString(),
                        editPassword.getText().toString(), editMatric.getText().toString(), editPlate.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "please insert all data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void register(String username, String email, String password, String matrics, String platenum) {
        progressDialog.show();

        // Check if the email is a valid format
        if (!email.endsWith("@siswa.um.edu.my")) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please use a valid email address (@siswa.um.edu.my)", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if matrics is 8 digits
        if (matrics.length() != 8 || !matrics.matches("\\d+")) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Matrics should be 8 digits", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FirebaseUser firebaseUser = task.getResult().getUser();

                    // Save user information in SharedPreferences
                    saveUserInfo(username, email, password, matrics);

                    if (firebaseUser != null) {
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();

                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                reload();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Register fail", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reload() {
        startActivity(new Intent(getApplicationContext(), DriverHomePage.class));
    }
    private void saveUserInfo(String username, String email, String password, String matrics) {
        // Use SharedPreferences to save user information
        SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("matrics", matrics);
        editor.apply();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }
}
