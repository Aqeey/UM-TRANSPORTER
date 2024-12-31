package com.aqil.fyp2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private Button Order, Setting;
    private String PassengerID;
    private DatabaseReference PassengerDatabaseRef;
    private LatLng PassengerPickupLocation;
    private int  radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;
    private Boolean requestBol = false;
    private Marker pickupMarker;

    private LinearLayout mDriverInfo;
    private ImageView mDriverProfilePic;
    private TextView mDriverName, mDriverPhoneNum, mDriverCar,mDriverPlate;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        Order = view.findViewById(R.id.order);
        Setting = view.findViewById(R.id.setting);

        // Initialize Firebase variables
        PassengerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        PassengerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Passenger Request");
        DriverLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");

        mDriverInfo = view.findViewById(R.id.driverInfo);
        mDriverProfilePic = view.findViewById(R.id.driverProfileimage);
        mDriverName = view.findViewById(R.id.driverName);
        mDriverPhoneNum = view.findViewById(R.id.driverPhoneNum);
        mDriverCar = view.findViewById(R.id.driverCar);
        mDriverPlate = view.findViewById(R.id.driverPlate);

        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), SettingPassenger.class);
                startActivity(intent);
            }
        });


        Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestBol){
                    requestBol = false;
                    geoQuery.removeAllListeners();

                    // Check if DriverLocationRefListener is not null before removing
                    if (DriverLocationRefListener != null) {
                        DriverLocationRef.removeEventListener(DriverLocationRefListener);
                    }

                    if(driverFoundID != null){
                        DatabaseReference DriverRef = FirebaseDatabase.getInstance().getReference().child("Driver").child(driverFoundID).child("PassengerRequest");
                        DriverRef.setValue(true);
                        driverFoundID = null;
                    }
                    driverFound = false;
                    radius = 1;

                    PassengerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    GeoFire geoFire = new GeoFire(PassengerDatabaseRef);
                    geoFire.removeLocation(PassengerID);

                    if(pickupMarker != null){
                        pickupMarker.remove();
                    }
                    Order.setText("Order");






                }else {
                    requestBol = true;
                    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
                    fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            //location akhir
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // set loc databsezzzzzzzzzzzz
                            GeoFire geoFire = new GeoFire(PassengerDatabaseRef);
                            geoFire.setLocation(PassengerID, new GeoLocation(latitude, longitude));

                            // Add marker to the map
                            PassengerPickupLocation = new LatLng(latitude, longitude);
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(PassengerPickupLocation).title("PickupHere").icon(BitmapDescriptorFactory.fromResource(R.mipmap.people_icononmap)));

                            Order.setText("Searching Driver...");
                            GetClosestDriver();
                        }
                    });


                }




            }
        });

        return view;
    }


            GeoQuery geoQuery;
    private void GetClosestDriver() {

            GeoFire geoFire = new GeoFire(DriverLocationRef);
             geoQuery = geoFire.queryAtLocation(new GeoLocation(PassengerPickupLocation.latitude,PassengerPickupLocation.longitude),radius);
            geoQuery.removeAllListeners();

            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                @Override
                public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                    if (!driverFound && requestBol){
                        driverFound = true;
                        driverFoundID = dataSnapshot.getKey();
                        DatabaseReference DriverRef = FirebaseDatabase.getInstance().getReference().child("Driver").child(driverFoundID).child("PassengerRequest");
                        PassengerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        HashMap map = new HashMap();
                        map.put("PassengerRideID",PassengerID);
                        map.put("Passenger",PassengerID);

                        DriverRef.updateChildren(map);


                        getDriverLocation();
                        getDriverInfo();
                        Order.setText("Driver Found");
                    }

                }

                @Override
                public void onDataExited(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                }

                @Override
                public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {
                    if (!driverFound){
                        radius = radius + 1;
                        GetClosestDriver();
                    }

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
    }
    private Marker mDriverMarker;
    private DatabaseReference DriverLocationRef;
    private ValueEventListener DriverLocationRefListener;

    private void getDriverLocation() {
        DriverLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working").child(driverFoundID).child("1");

        DriverLocationRefListener = DriverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && requestBol) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map != null) {
                        if (map.containsKey("0") && map.containsKey("1")) {
                            double locationLat = Double.parseDouble(map.get("0").toString());
                            double locationLng = Double.parseDouble(map.get("1").toString());

                            LatLng driverLatLng = new LatLng(locationLat, locationLng);

                            if (mDriverMarker != null) {
                                mDriverMarker.remove();
                            }

                            Location loc1 = new Location("");
                            loc1.setLatitude(PassengerPickupLocation.latitude);
                            loc1.setLongitude(PassengerPickupLocation.longitude);

                            Location loc2 = new Location("");
                            loc2.setLatitude(locationLat);  // Use locationLat from the snapshot
                            loc2.setLongitude(locationLng); // Use locationLng from the snapshot

                            float distance = loc1.distanceTo(loc2);

                            if (distance < 100) {
                                Order.setText("Driver Here");
                            } else {
                                Order.setText("Driver Found " + String.valueOf(distance));
                                mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icononmap)));
                            }
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

    private void getDriverInfo() {
        Log.d("DriverMapsFragment", "getAssignedPassengerInfo: Called");  // Add this line for debugging

        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference PassengerDatabase = FirebaseDatabase.getInstance().getReference().child("Driver").child(driverFoundID);
        PassengerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {

                    Object snapshotValue = snapshot.getValue();

                    if (snapshotValue instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) snapshotValue;

                        if (map.get("Name") != null) {
                            String name = "Name: " + map.get("Name").toString();
                            mDriverName.setText(name);
                        }
                        if (map.get("Phone Num") != null) {
                            String phone = "Phone: " + map.get("Phone Num").toString();
                            mDriverPhoneNum.setText(phone);
                        }

                        if (map.get("Car") != null) {
                           String car = "Car: " + map.get("Car").toString();
                            mDriverCar.setText(car);
                        }
                        if (map.get("Plate Num") != null) {
                           String plate = "Plate: " +map.get("Plate Num").toString();
                            mDriverPlate.setText(plate);
                        }
                        if (map.get("profileImageUrl") != null) {
                            Glide.with(getActivity()).load(map.get("profileImageUrl").toString()).into(mDriverProfilePic);
                        }
                    }
                } else {
                    Log.d("DriverMapsFragment", "getAssignedPassengerInfo: Data does not exist");  // Add this line for debugging
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DriverMapsFragment", "getAssignedPassengerInfo: onCancelled", error.toException());  // Add this line for debugging
            }
        });
    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                }
            });
        }
    };
}
