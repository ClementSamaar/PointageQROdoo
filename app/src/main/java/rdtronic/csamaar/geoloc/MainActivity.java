package rdtronic.csamaar.geoloc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
        initializeViews();
        display();
        ReqXMLRPC clientOdoo = new ReqXMLRPC();
        /*Thread xmlReqThread = */new Thread(() -> {
            int uid = clientOdoo.authenticate();
            Log.v("UID", String.valueOf(uid));
            clientOdoo.getValuesFromOdoo(uid);
        }).start();

           /* try {
                XMLRPCClient client = new XMLRPCClient(new URL(SERVER_URL), XMLRPCClient.FLAGS_FORWARD);
                client.setLoginData(USERNAME, PASSWORD);
                client.callAsync(new XMLRPCCallback() {
                    @Override
                    public void onResponse(long id, Object result) {
                        Log.v("Response", result.toString());
                    }

                    @Override
                    public void onError(long id, XMLRPCException error) {
                        error.printStackTrace();
                        Log.v("Call Error " + id , error.getMessage());
                    }

                    @Override
                    public void onServerError(long id, XMLRPCServerException error) {
                        error.printStackTrace();
                        Log.v("Server Error " + id, error.getMessage());
                    }
                },"authenticate", "odoo_pointage", USERNAME, PASSWORD, "");
            } catch (Exception ex) {
                ex.getStackTrace();
                Log.v("Ex", ex.getMessage());
            }
        }*/

        /*xmlrpc.client.callAsync(new XMLRPCCallback() {
            @Override
            public void onResponse(long id, Object result) {
                Log.v("Response", result.toString());
            }

            @Override
            public void onError(long id, XMLRPCException error) {
                Log.v("Call Error " + id , error.getMessage());
                try {
                    Log.v("test", xmlrpc.test.getResponseMessage());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                error.printStackTrace();
            }

            @Override
            public void onServerError(long id, XMLRPCServerException error) {
                Log.v("Server Error " + id, error.getMessage());
                error.printStackTrace();
            }
        },"search_read", "public.hr_attendance");*/
    }

    private void initializeViews(){
        coordinatesLabel = findViewById(R.id.coordinatesLabel);
        longLabel = findViewById(R.id.longLabel);
        latLabel = findViewById(R.id.latLabel);
        longCoordinate = findViewById(R.id.longCoordinate);
        latCoordinate = findViewById(R.id.latCoordinate);
        permReqButton = findViewById(R.id.locPermissionButton);
        permReqButton.setOnClickListener(view -> requestLocationPermission());
        Button qrCodeButton = findViewById(R.id.mainToQrCode);
        qrCodeButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, QrCodeReader.class)));
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    longitude = String.valueOf(location.getLongitude());
                    latitude = String.valueOf(location.getLatitude());
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
                    .setMessage(R.string.locPermRationale_msg)
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
                Toast.makeText(MainActivity.this, R.string.locPermissionGranted_toastText, Toast.LENGTH_SHORT).show();
                display();
            } else {
                Toast.makeText(MainActivity.this, R.string.locPermissionDenied_toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}