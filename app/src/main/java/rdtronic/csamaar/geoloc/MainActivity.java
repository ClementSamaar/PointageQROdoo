package rdtronic.csamaar.geoloc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    final String[] permissionList = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private TextView coordinatesLabel, longLabel, latLabel, longCoordinate, latCoordinate;
    private Button permReqButton;

    private FusedLocationProviderClient fusedLocationClient;

    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatesLabel = findViewById(R.id.coordinatesLabel);
        longLabel = findViewById(R.id.longLabel);
        latLabel = findViewById(R.id.latLabel);
        longCoordinate = findViewById(R.id.longCoordinate);
        latCoordinate = findViewById(R.id.latCoordinate);
        permReqButton = findViewById(R.id.permissionButton);
        permReqButton.setOnClickListener(view -> requestLocationPermission());
        display();
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    longitude = String.valueOf(location.getLongitude());
                    latitude = String.valueOf(location.getLatitude());
                    Log.v("LONGITUDE", String.valueOf(longitude));
                    Log.v("LATITUDE", String.valueOf(latitude));
                    display();
                    if (longitude != null && latitude != null) fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        }
    };

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    @SuppressLint("MissingPermission")
    private void getLocalisation(){
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    private void display(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocalisation();
            if (longitude == null && latitude == null){
                coordinatesLabel.setText(R.string.coordinatesLabel_onWait);
                if (longLabel.getVisibility() == View.VISIBLE) longLabel.setVisibility(View.INVISIBLE);
                if (latLabel.getVisibility() == View.VISIBLE) latLabel.setVisibility(View.INVISIBLE);
            }
            else{
                longCoordinate.setText(longitude);
                latCoordinate.setText(latitude);
                coordinatesLabel.setText(R.string.coordinatesLabel_onGranted);
                if (longLabel.getVisibility() == View.INVISIBLE) longLabel.setVisibility(View.VISIBLE);
                if (latLabel.getVisibility() == View.INVISIBLE) latLabel.setVisibility(View.VISIBLE);
            }
            permReqButton.setVisibility(View.INVISIBLE);
        }
        else{
            coordinatesLabel.setText(R.string.coordinatesLabel_onDenied);
            if (longLabel.getVisibility() == View.VISIBLE) longLabel.setVisibility(View.INVISIBLE);
            if (latLabel.getVisibility() == View.VISIBLE) latLabel.setVisibility(View.INVISIBLE);
            if (permReqButton.getVisibility() == View.INVISIBLE) permReqButton.setVisibility(View.VISIBLE);
        }
    }


    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this).setTitle(R.string.permRationale_title)
                    .setMessage(R.string.permRationale_msg)
                    .setPositiveButton(R.string.permRationale_posBtn, (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this, permissionList, 0))
                    .setNegativeButton(R.string.permRationale_negBtn, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(this, permissionList, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, R.string.permissionGranted_toastText, Toast.LENGTH_SHORT).show();
                display();
            } else {
                Toast.makeText(MainActivity.this, R.string.permissionDenied_toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}