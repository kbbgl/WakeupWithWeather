package kobbigal.wakeupwithweather;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName().toUpperCase();
    public JobScheduler jobScheduler;
    public Context ctx;
    private boolean isConnected;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: receiver started");
        ctx = context;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnected();
        }

        if (isConnected){
            jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            getWeatherJob();
        }
        // TODO: 7/28/18 send notification that network was off

    }

    public void getWeatherJob(){

        Log.i(TAG, "getWeatherJob running");

        ComponentName weatherServiceComponentName = new ComponentName(ctx, GetWeatherInfoJobService.class);;

        JobInfo weatherJobInfo = new JobInfo.Builder(0, weatherServiceComponentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        if (jobScheduler != null) {
            int resultCode = jobScheduler.schedule(weatherJobInfo);
            Log.d(TAG, "getWeatherJob: " + resultCode);
            if (resultCode == JobScheduler.RESULT_SUCCESS){
                Log.i(TAG, "Job scheduled");
                Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
            }
            else {
                Log.e(TAG, "Job not scheduled");
            }
        }
    }


}
