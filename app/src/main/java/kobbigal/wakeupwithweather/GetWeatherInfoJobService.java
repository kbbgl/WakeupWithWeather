package kobbigal.wakeupwithweather;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.state.Weather;


public class GetWeatherInfoJobService extends JobService {

    private static final String TAG = "GetWeatherJobService";
//    private boolean isJobCancelled = false;

    public GetWeatherInfoJobService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {

        Log.i(TAG, "Job started");

        Awareness.getSnapshotClient(this).getWeather()
                    .addOnCompleteListener((t) -> {
                        if (t.isSuccessful()){

                            Log.i(TAG, "Temperature in Celsius: " + t.getResult().getWeather().getTemperature(Weather.CELSIUS));
                            jobFinished(params, false);
                        }
                    })
                    .addOnFailureListener(e -> {

                        Log.e(TAG, "Weather task failed");
                        e.printStackTrace();

                    });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "Job stopped" );
        return false;
    }

}


