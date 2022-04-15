package rdtronic.csamaar.geoloc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class QrCodeReader extends AppCompatActivity {

    private TextView appTitle;
    private SurfaceView scanner;
    private Button cameraPermissionButton;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    final String[] permissionList = new String[] {
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_reader);
        appTitle = findViewById(R.id.qrApp_title);
        scanner = findViewById(R.id.scanner);
        cameraPermissionButton = findViewById(R.id.cameraPermissionButton);
        cameraPermissionButton.setOnClickListener(view -> requestCameraPermission());

        display();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.locPermissionGranted_toastText, Toast.LENGTH_SHORT).show();
                display();
            } else {
                Toast.makeText(this, R.string.locPermissionDenied_toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeDetectorAndSource(){
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(263, 225)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .build();
    }
}