package rdtronic.csamaar.geoloc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView longCoordinate = findViewById(R.id.longCoordinate);
    TextView latCoordinate = findViewById(R.id.latCoordinate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Intent editIntent = new Intent();
        editIntent.setAction(Intent.ACTION_INSERT_OR_EDIT);
        editIntent.setType("text/plain");
        editIntent.putExtra("long", "");
        editIntent.putExtra("lat", "");
        String longValue = editIntent;*/
    }
}