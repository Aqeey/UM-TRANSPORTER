package com.aqil.fyp2.driver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.aqil.fyp2.R;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DriverMapsFragment extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private String PassengerID = "";
    private LinearLayout mPassengerInfo;
    private ImageView mPassengerProfilePic;
    private TextView mPassengerName, mPassengerPhoneNum, mPassengerPickup, mPassengerDestination;
    private Button btnEditDetail;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
                return;
            }

            mMap.setMyLocationEnabled(true);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                    String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference driverAvailRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                    DatabaseReference driverWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");

                    GeoFire geoFireAvailable = new GeoFire(driverAvailRef);
                    GeoFire geoFireWorking = new GeoFire(driverWorkingRef);

                    switch (PassengerID) {
                        case "":
                            geoFireWorking.removeLocation(UserID);
                            geoFireAvailable.setLocation(UserID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;

                        default:
                            geoFireAvailable.removeLocation(UserID);
                            geoFireWorking.setLocation(UserID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }
            });
        }
    };

    @Override
    public void onStop() {
        super.onStop();

        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvailRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geofire = new GeoFire(DriverAvailRef);
        geofire.removeLocation(UserID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_maps, container, false);

        mPassengerInfo = view.findViewById(R.id.passengerInfo);
        mPassengerProfilePic = view.findViewById(R.id.customerProfileimage);
        mPassengerName = view.findViewById(R.id.customerName);
        mPassengerPhoneNum = view.findViewById(R.id.customerPhoneNum);
        mPassengerPickup = view.findViewById(R.id.pickup);
        mPassengerDestination = view.findViewById(R.id.destination);
        btnEditDetail = view.findViewById(R.id.setting);

        btnEditDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), SettingDriver.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        getAssignedPassenger();
    }

    private void getAssignedPassenger() {
        String driverID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference AssignedPassengerRef = FirebaseDatabase.getInstance().getReference().child("Driver").child(driverID).child("PassengerRequest");
        AssignedPassengerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming PassengerID is a map with keys "Passenger" and "PassengerRideID"
                    Map<String, Object> passengerIdMap = (Map<String, Object>) snapshot.getValue();

                    if (passengerIdMap != null && passengerIdMap.containsKey("Passenger")) {
                        PassengerID = passengerIdMap.get("Passenger").toString();
                        getAssignedPassengerInfo();
                    } else {
                        mPassengerInfo.setVisibility(View.GONE);
                    }
                } else {
                    mPassengerInfo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DriverMapsFragment", "getAssignedPassenger: onCancelled", error.toException());
            }
        });
    }




    private void getAssignedPassengerInfo() {
        Log.d("DriverMapsFragment", "getAssignedPassengerInfo: PassengerID = " + PassengerID);

        mPassengerInfo.setVisibility(View.VISIBLE);
        DatabaseReference PassengerDatabase = FirebaseDatabase.getInstance().getReference().child("Passenger").child(PassengerID);
        PassengerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Log.d("DriverMapsFragment", "getAssignedPassengerInfo: Data exists");

                    Object snapshotValue = snapshot.getValue();

                    if (snapshotValue instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) snapshotValue;

                        if (map.get("Name") != null) {
                            String name = "Name: " + map.get("Name").toString();
                            mPassengerName.setText(name);
                        }
                        if (map.get("Phone Num") != null) {
                            String phone = "Phone: " + map.get("Phone Num").toString();
                            mPassengerPhoneNum.setText(phone);
                        }

                        if (map.get("Pickup Location") != null) {
                            String pickup = "Pickup Location: " + map.get("Pickup Location").toString();
                            mPassengerPickup.setText(pickup);
                        }
                        if (map.get("Destination") != null) {
                            String destination = "Destination: " + map.get("Destination").toString();
                            mPassengerDestination.setText(destination);
                            // Add any additional processing or formatting for the destination if needed
                        }

                        if (map.get("profileImageUrl") != null) {
                            if (getActivity() != null) {
                                Glide.with(getActivity()).load(map.get("profileImageUrl").toString()).into(mPassengerProfilePic);
                            }
                        }

                    }
                } else {
                    Log.d("DriverMapsFragment", "getAssignedPassengerInfo: Data does not exist");  // Add this line for debugging
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DriverMapsFragment", "getAssignedPassengerInfo: onCancelled", error.toException());
            }
        });
    }

}
