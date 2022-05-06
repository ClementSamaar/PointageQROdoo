package rdtronic.csamaar.geoloc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QrCodeReader extends AppCompatActivity {

    private SurfaceView scanner;
    private Button cameraPermissionButton;
    private CameraSource cameraSource;

    final String[] permissionList = new String[] {
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_reader);
        initializeViews();
        display();
    }

    private void initializeViews(){
        scanner = findViewById(R.id.scanner);
        cameraPermissionButton = findViewById(R.id.camPermissionButton);
        cameraPermissionButton.setOnClickListener(view -> requestCameraPermission());
        Button geoLocButton = findViewById(R.id.qrCodeToGeoLoc);
        geoLocButton.setOnClickListener(view -> startActivity(new Intent(QrCodeReader.this, MainActivity.class)));
    }

    private void display(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            if (scanner.getVisibility() == View.INVISIBLE) scanner.setVisibility(View.VISIBLE);
            if (cameraPermissionButton.getVisibility() == View.VISIBLE) cameraPermissionButton.setVisibility(View.INVISIBLE);
        }
        else{
            if (scanner.getVisibility() == View.VISIBLE) scanner.setVisibility(View.INVISIBLE);
            if (cameraPermissionButton.getVisibility() == View.INVISIBLE) cameraPermissionButton.setVisibility(View.VISIBLE);

        }
    }

    public void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            new AlertDialog.Builder(this).setTitle(R.string.permRationale_title)
                    .setMessage(R.string.camPermRationale_msg)
                    .setPositiveButton(R.string.permRationale_posBtn, (dialogInterface, i) -> ActivityCompat.requestPermissions(this, permissionList, 1))
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
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.camPermissionGranted_toastText, Toast.LENGTH_SHORT).show();
                display();
            } else {
                Toast.makeText(this, R.string.camPermissionDenied_toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeDetectorAndSource(){
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(225, 225)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .build();

        scanner.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QrCodeReader.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(scanner.getHolder());
                    } else {
                        requestCameraPermission();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { cameraSource.stop(); }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(QrCodeReader.this, DataRetriever.class);
                    bundle.putString("qrcodedata", (qrCode.valueAt(0).rawValue));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeDetectorAndSource();
    }
}