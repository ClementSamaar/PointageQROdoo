package rdtronic.csamaar.geoloc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {

    private final Intent editIntent = new Intent();
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;

    String[] permissionList = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    LocationListener locationListener;
    LocationManager locationManager;

    private TextView coordinatesLabel, longLabel, latLabel, longCoordinate, latCoordinate;
    private Button permReqButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatesLabel = findViewById(R.id.coordinatesLabel);
        longLabel = findViewById(R.id.longLabel);
        latLabel = findViewById(R.id.latLabel);
        longCoordinate = findViewById(R.id.longCoordinate);
        latCoordinate = findViewById(R.id.latCoordinate);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        permReqButton = findViewById(R.id.permissionButton);
        permReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();
            }
        });

        editIntent.setAction(Intent.ACTION_INSERT_OR_EDIT);
        editIntent.setType("");

        display();

        /*fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    Log.v("erreur", "no");
                } else {
                    Log.v("long", String.valueOf(location.getLongitude()));
                    Log.v("lat", String.valueOf(location.getLatitude()));
                    longCoordinate.setText(String.valueOf(location.getLongitude()));
                    latCoordinate.setText(String.valueOf(location.getLatitude()));
                }
            }
        });*/

        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                editIntent.putExtra("long", String.valueOf(location.getLongitude()));
                editIntent.putExtra("lat", String.valueOf(location.getLatitude()));
                Log.v("long", String.valueOf(location.getLongitude()));
            }
        };*/

    }

    private void display(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
            coordinatesLabel.setText(R.string.coordinatesLabel_onGranted);
            if (longLabel.getVisibility() == View.INVISIBLE) longLabel.setVisibility(View.VISIBLE);
            if (latLabel.getVisibility() == View.INVISIBLE) latLabel.setVisibility(View.VISIBLE);
            if (permReqButton.getVisibility() == View.VISIBLE) permReqButton.setVisibility(View.INVISIBLE);
        }
        else {
            coordinatesLabel.setText(R.string.coordinatesLabel_onDenied);
            if (longLabel.getVisibility() == View.VISIBLE) longLabel.setVisibility(View.INVISIBLE);
            if (latLabel.getVisibility() == View.VISIBLE) latLabel.setVisibility(View.INVISIBLE);
            if (permReqButton.getVisibility() == View.INVISIBLE) permReqButton.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation () {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.v("long", String.valueOf(location.getLongitude()));
                    longCoordinate.setText(String.valueOf(location.getLongitude()));
                    latCoordinate.setText(String.valueOf(location.getLatitude()));
                }
                else{
                    Log.v("Erreur", "Location == null");
                }
            }
        });
    }

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this).setTitle("Autorisation necessaire")
                    .setMessage("GeoLoc à besoin d'accéder à votre position afin de pouvoir fonctionner.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissionList, 0);
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
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
                Toast.makeText(MainActivity.this, "GeoLoc utilise votre position", Toast.LENGTH_SHORT).show();
                display();
                //locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 10, locationListener);
            } else {
                Toast.makeText(MainActivity.this, "L'application n'utilisera pas votre position", Toast.LENGTH_SHORT).show();
            }
        }
    }
}