package com.aqil.fyp2.driver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aqil.fyp2.PassengerPayment;
import com.aqil.fyp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class DriverPayment extends Fragment {
    private ImageView paymentphoto ,btnEditQR;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_payment, container, false);

        paymentphoto = view.findViewById(R.id.insertQR);
        ImageView btnEditQR = view.findViewById(R.id.editQR);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnEditQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        return view;
    }

    private static final int GALLERY_REQUEST_CODE = 1;

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            paymentphoto.setImageURI(imageUri);
            selectedImageUri = imageUri;
            uploadImage();
        }
    }

    private void uploadImage() {
        if (selectedImageUri != null) {
            StorageReference fileReference = storageReference.child("payment_images/" + System.currentTimeMillis() + ".jpg");
            fileReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                // Save the image URL to Firebase Firestore
                                savePaymentPhotoUrlToFirestore(imageUrl);
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void savePaymentPhotoUrlToFirestore(String imageUrl) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            String path = "Users/" + userId + "/customer_data/user_details";  // Adjust the path as needed

            // Create a reference to the specific path
            DocumentReference documentReference = firebaseFirestore.document(path);

            // Create a Map to update the data
            Map<String, Object> data = new HashMap<>();
            data.put("payment_photo_url", imageUrl);

            // Update the data in the specified document
            documentReference.update(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Data update successful
                            navigateToPassengerPayment();
                            Toast.makeText(getActivity(), "Payment photo URL saved to Firestore", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle errors
                            Toast.makeText(getActivity(), "Failed to save Payment photo URL to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void navigateToPassengerPayment() {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.bottomNavigationView, new PassengerPayment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}