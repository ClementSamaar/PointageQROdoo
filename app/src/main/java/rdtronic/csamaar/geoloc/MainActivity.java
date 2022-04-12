package rdtronic.csamaar.geoloc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private final Intent editIntent = new Intent();
    String latDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editIntent.setAction(Intent.ACTION_INSERT_OR_EDIT);
        editIntent.setType("text/plain");

        String[] permissionList = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (checkSelfPermission(LOCATION_SERVICE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(permissionList, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView longCoordinate = findViewById(R.id.longCoordinate);
        TextView latCoordinate = findViewById(R.id.latCoordinate);
        TextView coordinatesLabel = findViewById(R.id.coordinatesLabel);
        TextView longLabel = findViewById(R.id.longLabel);
        TextView latLabel = findViewById(R.id.latLabel);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latDebug = String.valueOf(location.getLatitude());
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

        if (checkSelfPermission(LOCATION_SERVICE) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        String longValue = editIntent.getStringExtra("long");
        String latValue = editIntent.getStringExtra("lat");

        longCoordinate.setText(longValue);
        latCoordinate.setText(latValue);
    }
}