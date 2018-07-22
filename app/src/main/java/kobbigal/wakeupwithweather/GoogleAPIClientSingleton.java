package kobbigal.wakeupwithweather;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by kobbigal on 7/21/18.
 */

public class GoogleAPIClientSingleton {

    private static final String TAG = "GoogleAPIClient";
    private static GoogleAPIClientSingleton instance = null;
    private static GoogleApiClient mGoogleApiClient = null;

    protected GoogleAPIClientSingleton(){

//        GoogleApiClient client = new GoogleApiClient.Builder(this).


    }

    public static GoogleAPIClientSingleton getInstance(GoogleApiClient aGoogleApiClient){

        if (instance == null){
            instance = new GoogleAPIClientSingleton();

            if (mGoogleApiClient == null){
                mGoogleApiClient = aGoogleApiClient;
            }
        }

        return instance;
    }

    public GoogleApiClient getmGoogleApiClient(){
        return mGoogleApiClient;
    }

}
