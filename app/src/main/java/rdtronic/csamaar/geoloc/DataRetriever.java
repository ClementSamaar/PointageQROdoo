package rdtronic.csamaar.geoloc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DataRetriever extends AppCompatActivity {

    private String dataRetieved;
    private TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retreiver);
        initializeViews();
        retrieveData();
        data.setText(dataRetieved);
    }

    private void initializeViews(){
        data = findViewById(R.id.dataRetrieved);
        Button geoLocButton, qrCodeButton;
        geoLocButton = findViewById(R.id.dataRetrievedToGeoLoc);
        geoLocButton.setOnClickListener(view -> startActivity(new Intent(DataRetriever.this, MainActivity.class)));

        qrCodeButton = findViewById(R.id.dataRetrievedToQrCode);
        qrCodeButton.setOnClickListener(view -> startActivity(new Intent(DataRetriever.this, QrCodeReader.class)));
    }

    private void retrieveData(){
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("qrcodedata")) dataRetieved = intent.getStringExtra("qrcodedata");
        }
    }
}