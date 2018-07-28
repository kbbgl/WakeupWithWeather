package kobbigal.wakeupwithweather;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.state.Weather;


public class GetWeatherInfoJobService extends JobService {

    private static final String TAG = "GetWeatherJobService";
    private static final String CHANNEL_ID = "weathernotif";
    private static final int WEATHER_NOTIFICATION = 0;

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {

        Log.i(TAG, "Job started");
        createNotificationChannel();

        Awareness.getSnapshotClient(this).getWeather()
                    .addOnCompleteListener((t) -> {
                        if (t.isSuccessful()){

                            // TODO: 7/28/18 integrate other weather factors

                            float temperature = t.getResult().getWeather().getTemperature(Weather.CELSIUS);

                            //int CONDITION_UNKNOWN = 0;
                            //int CONDITION_CLEAR = 1;
                            //int CONDITION_CLOUDY = 2;
                            //int CONDITION_FOGGY = 3;
                            //int CONDITION_HAZY = 4;
                            //int CONDITION_ICY = 5;
                            //int CONDITION_RAINY = 6;
                            //int CONDITION_SNOWY = 7;
                            //int CONDITION_STORMY = 8;
                            //int CONDITION_WINDY = 9;
                            int[] conditions = t.getResult().getWeather().getConditions();
                            int humidity = t.getResult().getWeather().getHumidity();
                            float feelsLikeTemp = t.getResult().getWeather().getFeelsLikeTemperature(Weather.CELSIUS);
                            float dewPoint = t.getResult().getWeather().getDewPoint(Weather.CELSIUS);

                            createNotification(temperature);
                            jobFinished(params, false);
                        }
                    })
                    .addOnFailureListener(e -> {

                        Log.e(TAG, "Weather task failed");
                        e.printStackTrace();

                    });

        return true;
    }

    private void createNotification(float temp){

        Log.i(TAG, "Temperature in Celsius: " + temp);

        String notifTitle = temp + "\u00b0";
        NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.sunnyblack)
                .setContentTitle(notifTitle)
                .setContentText("Under construction")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notifManager.notify(WEATHER_NOTIFICATION, notBuilder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "Job stopped" );
        return false;
    }

}


