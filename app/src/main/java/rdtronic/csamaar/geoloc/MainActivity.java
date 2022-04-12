package rdtronic.csamaar.geoloc;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {

    private final Intent editIntent = new Intent();
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView longCoordinate = findViewById(R.id.longCoordinate);
        TextView latCoordinate = findViewById(R.id.latCoordinate);
        TextView coordinatesLabel = findViewById(R.id.coordinatesLabel);
        TextView longLabel = findViewById(R.id.longLabel);
        TextView latLabel = findViewById(R.id.latLabel);

        editIntent.setAction(Intent.ACTION_INSERT_OR_EDIT);
        editIntent.setType("");

        String[] permissionList = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(LOCATION_SERVICE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(permissionList, 0);
        }
        else if (checkSelfPermission(LOCATION_SERVICE) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        Log.v("erreur", "no");
                    }
                    else{
                        Log.v("long", String.valueOf(location.getLongitude()));
                        Log.v("lat", String.valueOf(location.getLatitude()));
                        longCoordinate.setText(String.valueOf(location.getLongitude()));
                        latCoordinate.setText(String.valueOf(location.getLatitude()));
                    }
                }
            });
        }

        /*LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                editIntent.putExtra("long", String.valueOf(location.getLongitude()));
                editIntent.putExtra("lat", String.valueOf(location.getLatitude()));
                Log.v("long", String.valueOf(location.getLongitude()));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                if (editIntent.getStringExtra("long") != null){
                    coordinatesLabel.setText("La localisation GPS est désactivée, voici les dernières coordonnées reçues");
                }
                else{
                    coordinatesLabel.setText("La localisation GPS est désactivée");
                    if (longLabel.getVisibility() == View.VISIBLE) {
                        longLabel.setVisibility(View.INVISIBLE);
                        latLabel.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
                coordinatesLabel.setText("Voici les coordonnées GPS de votre position");

                if (longLabel.getVisibility() == View.INVISIBLE) {
                    longLabel.setVisibility(View.VISIBLE);
                    latLabel.setVisibility(View.VISIBLE);
                }
            }
        };

        if (checkSelfPermission(LOCATION_SERVICE) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(LOCATION_SERVICE) == PackageManager.PERMISSION_DENIED)
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }
            catch (Exception e){
                Log.v("Erreur","Erreur (requestLocationUpdates) : " + e);
            }
     */
    }
}