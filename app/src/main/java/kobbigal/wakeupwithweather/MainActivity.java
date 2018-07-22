package kobbigal.wakeupwithweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private boolean weatherModeEnabled;
    private boolean isAlarmOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwitchCompat enableAlarmToggle = findViewById(R.id.submittimebtn);
        final TimePicker timePicker = findViewById(R.id.timepicker);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            weatherModeEnabled = false;

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            // Permission has already been granted
            weatherModeEnabled = true;
        }

        if (enableAlarmToggle.isChecked()) {
            enableAlarmToggle.setText("On");
        }
        else {
            enableAlarmToggle.setText("Off");
        }

        enableAlarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (((SwitchCompat) v).isChecked()){
                        System.out.println("Alarm enabled");
                        System.out.println("time picked: " + timePicker.getHour() + ":" + timePicker.getMinute());
                        enableAlarmToggle.setText("On");
                        isAlarmOn = true;
                        // TODO: 7/21/18 Add logic for JobScheduler
                    }
                    else {
                        System.out.println("Alarm disabled");
                        enableAlarmToggle.setText("Off");
                        isAlarmOn = false;
                        // TODO: 7/21/18 Add logic to kill JobScheduler
                    }
                }


            }
        );


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Snackbar mSnackbar = Snackbar.make(findViewById(R.id.rootlayout), "Weather mode is off", Snackbar.LENGTH_INDEFINITE);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSnackbar.dismiss();

                    weatherModeEnabled = false;

                } else {
                    mSnackbar.setAction("Request permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    });
                    mSnackbar.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    weatherModeEnabled = true;
                }
            }
        }
    }
}
