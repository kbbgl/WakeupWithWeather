package kobbigal.wakeupwithweather;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.URL;


public class GetWeatherInfoJobService extends JobService {

    GoogleApiClient client;

    public GetWeatherInfoJobService() {

    }

    @Override
    public boolean onStartJob(JobParameters params) {

        client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        client.connect();

        // TODO: 7/21/18 make sure permission for location granted
//        Awareness.SnapshotApi.getWeather(client);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}


