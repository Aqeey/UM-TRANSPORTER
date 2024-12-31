package com.aqil.fyp2.driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aqil.fyp2.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingDriver extends AppCompatActivity {

    private EditText editName, editPhoneNum, editCar, editPlate;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private String userID;
    private String mName;
    private String mPhone;
    private String mCar;
    private String mPlate;
    private ImageView Profilepic;
    private Uri resultUri;
    private String mProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_driver);

        editName = findViewById(R.id.name);
        editPhoneNum = findViewById(R.id.phone);
        editCar = findViewById(R.id.car);
        editPlate = findViewById(R.id.platenumber);



        Profilepic = findViewById(R.id.profileimage);

        submitButton = findViewById(R.id.submit);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Driver").child(userID);

        getUserInfo();

        Profilepic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        submitButton.setOnClickListener(v -> {
            saveUserInformation();
            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
        });
    }

    private void getUserInfo() {
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Object snapshotValue = snapshot.getValue();

                    if (snapshotValue instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) snapshotValue;

                        if (map.get("Name") != null) {
                            mName = map.get("Name").toString();
                            editName.setText(mName);
                        }

                        if (map.get("Car") != null) {
                            mCar = map.get("Car").toString();
                            editCar.setText(mCar);
                        }
                        if (map.get("Plate Num") != null) {
                            mPlate = map.get("Plate Num").toString();
                            editPlate.setText(mPlate);
                        }
                        if (map.get("Phone Num") != null) {
                            mPhone = map.get("Phone Num").toString();
                            editPhoneNum.setText(mPhone);
                        }
                        if (map.get("profileImageUrl") != null) {
                            mProfileImageUrl = map.get("profileImageUrl").toString();
                            Glide.with(getApplication()).load(mProfileImageUrl).into(Profilepic);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void saveUserInformation() {
        mName = editName.getText().toString();
        mPhone = editPhoneNum.getText().toString();
        mCar = editCar.getText().toString();
        mPlate = editPlate.getText().toString();




        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("Name", mName);
        userInfo.put("Phone Num", mPhone);
        userInfo.put("Car", mCar);
        userInfo.put("Plate Num", mPlate);


        mDriverDatabase.updateChildren(userInfo);

        if (resultUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_image").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnFailureListener(e -> {
                    // Handle upload failure
                });

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL from the StorageReference
                    filePath.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        // Now you can use the download URL
                        Map<String, Object> newImage = new HashMap<>();
                        newImage.put("profileImageUrl", downloadUrl.toString());
                        mDriverDatabase.updateChildren(newImage);
                    });
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            Profilepic.setImageURI(resultUri);
        }
    }
}
