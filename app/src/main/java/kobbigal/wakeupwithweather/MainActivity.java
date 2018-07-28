package kobbigal.wakeupwithweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Calendar.YEAR;


public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    public JobScheduler jobScheduler;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private boolean weatherModeEnabled;
    private boolean isAlarmOn;
    public TimePicker timePicker;
    public SwitchCompat enableAlarmToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableAlarmToggle = findViewById(R.id.submittimebtn);
        timePicker = findViewById(R.id.timepicker);
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        checkPermissions();

        if (enableAlarmToggle.isChecked()) {
            enableAlarmToggle.setText(R.string.on_toggle);
        }
        else {
            enableAlarmToggle.setText(R.string.off_toggle);
        }

        enableAlarmToggle.setOnClickListener(v -> {

            handleAlarm(((SwitchCompat) v).isChecked());
            }
        );
    }

    public void handleAlarm(boolean alarmOn){

        if (alarmOn){

            enableAlarmToggle.setText(R.string.on_toggle);

            int hour = timePicker.getHour();
            int mins = timePicker.getMinute();

            // Set to current system time
            Calendar systemTime = Calendar.getInstance();

            Calendar pickedTime = Calendar.getInstance();
            pickedTime.set(systemTime.get(Calendar.YEAR), systemTime.get(Calendar.MONTH), systemTime.get(Calendar.DAY_OF_MONTH), hour, mins);

            Log.i(TAG, "pickedTime: " + pickedTime.getTime().toString());
            Log.i(TAG, "systemTime: " + systemTime.getTime().toString());
            Log.i(TAG, "systemTime.after(pickedTime):" + systemTime.after(pickedTime));

            if (systemTime.before(pickedTime)){

                Log.i(TAG, "Alarm should be set for today");
            }
            else {
                Log.i(TAG, "Alarm should be set for tomorrow");
                pickedTime.set(Calendar.DAY_OF_MONTH, pickedTime.get(Calendar.DAY_OF_MONTH) + 1);
            }

            Log.i(TAG, "Alarm set for: " + pickedTime.getTime().toString());

            broadcastAlarm(pickedTime);
        }
        else {
            Log.i(TAG, "Alarm disabled");
            enableAlarmToggle.setText(R.string.off_toggle);
            cancelWeatherJob();
        }

    }

    public void broadcastAlarm(Calendar pickedTime){

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                System.out.println(calendar.getTime().toString());
            }
        }, 0, 1000);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {

            // TODO: 7/27/18 Check why alarm triggered only when device wakes up
//            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, pickedTime.getTimeInMillis(), pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, pickedTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, pickedTime.getTimeInMillis(), pendingIntent);
        }
    }

    public void cancelWeatherJob(){

        Log.i(TAG, "weather job cancelled");
        jobScheduler.cancel(0);

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
