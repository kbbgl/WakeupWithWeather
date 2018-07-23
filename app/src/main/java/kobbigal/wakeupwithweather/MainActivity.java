package kobbigal.wakeupwithweather;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private final int WEATHER_JOB_ID = 0;
    public JobScheduler jobScheduler;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private boolean weatherModeEnabled;
    private boolean isAlarmOn;
    public LocalTime selectedTime;
    public Time selectedTimePreAPI26;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwitchCompat enableAlarmToggle = findViewById(R.id.submittimebtn);
        final TimePicker timePicker = findViewById(R.id.timepicker);
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        checkPermissions();

        if (enableAlarmToggle.isChecked()) {
            enableAlarmToggle.setText(R.string.on_toggle);
        }
        else {
            enableAlarmToggle.setText(R.string.off_toggle);
        }

        enableAlarmToggle.setOnClickListener(v -> {

                if (((SwitchCompat) v).isChecked()){


                    int hour = timePicker.getHour();
                    int mins = timePicker.getMinute();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        selectedTime = LocalTime.of(hour, mins);
                        Log.i(TAG, "Alarm enabled @ " + selectedTime.toString());
                    }
                    else {
                        if (mins < 10){
                            selectedTimePreAPI26 = Time.valueOf(hour+":0"+mins+":00");
                        }
                        else {
                            selectedTimePreAPI26 = Time.valueOf(hour+":"+mins+":00");
                        }
                        Log.i(TAG, "Alarm enabled @ " + selectedTimePreAPI26.toString());
                    }

                    enableAlarmToggle.setText(R.string.on_toggle);

                    isAlarmOn = true;

                    scheduleWeatherJob();
                }
                else {
                    Log.i(TAG, "Alarm disabled");
                    enableAlarmToggle.setText(R.string.off_toggle);
                    isAlarmOn = false;
                    cancelWeatherJob();

                }
            }
        );
    }

    public void checkPermissions(){
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

    }

    public void scheduleWeatherJob(){

        Log.i(TAG, "scheduleWeatherJob running");

        ComponentName weatherServiceComponentName = new ComponentName(this, GetWeatherInfoJobService.class);;
        JobInfo weatherJobInfo = new JobInfo.Builder(WEATHER_JOB_ID, weatherServiceComponentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        if (jobScheduler != null) {
            int resultCode = jobScheduler.schedule(weatherJobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS){
                Log.i(TAG, "Job scheduled");
            }
            else {
                Log.e(TAG, "Job not scheduled");
            }
        }
    }

    public void cancelWeatherJob(){

        Log.i(TAG, "weather job cancelled");
        jobScheduler.cancel(WEATHER_JOB_ID);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
