package myapp.techforwarder.com.myapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.anastr.speedviewlib.base.Gauge;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kavin on 27-05-2017.
 */
public class LocationListener implements android.location.LocationListener {

    private TextView textView;
    private boolean alert;
    private long startTime;
    private boolean tolerancePeriod;
    private MediaPlayer mp;
    private TextView alertView;
    private Gauge gauge;
    private long lastUpdated=0;
    public static final String mypreference = "mypref";
    public static final String url = "url";
    public static final String alertKm = "alertKm";
    private float distance=0.0f;
    private Location previouslocation;

    SharedPreferences sharedpreferences;

    public LocationListener(TextView textView,MediaPlayer mp,TextView alertView,Gauge gauge,Context context)
    {
        this.textView=textView;

        this.mp=mp;
        this.alertView=alertView;
        this.gauge=gauge;
        sharedpreferences = context.getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);



    }
    @Override
    public void onLocationChanged(Location location) {
        String webService=sharedpreferences.getString(url, "http://tracker-test.96.lt/trackers/tracker.php");
        String alertSpeed=sharedpreferences.getString(alertKm, "60");

        if(previouslocation!=null && ((location.getSpeed() * 3600) / 1000)>1 ){
            distance+=location.distanceTo(previouslocation);

        }
        previouslocation=location;


       // Log.d("LocationListener", "Url "+webService+" Alert Speed: "+alertSpeed);
        float speed=(float) ((location.getSpeed() * 3600) / 1000);
        gauge.setSpeedAt(speed);
        if(speed > (Integer.valueOf(alertSpeed)) )
        {
            Log.d("LocationListener", "Speed :"+speed);
            if(!tolerancePeriod) {
                startTime = location.getTime();
                tolerancePeriod = true;
            }else if((location.getTime()-startTime)>10000 && !alert)
            {


                mp.setLooping(true);
                mp.start();
                alert=true;
                alertView.setText("Warning : Please reduce the speed");

            }


        }
        Log.d("LocationListener", "Alert: "+alert);
        if(alert && (speed < (Integer.valueOf(alertSpeed))-3))
        {
            mp.pause();
            tolerancePeriod=false;
            alert=false;
            Log.d("LocationListener", "Inside condition");
            alertView.setText("Thank you!!!");
        }

        double tmpDis=distance/1000.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        textView.setText(df.format(tmpDis));

        if(location.getTime()-lastUpdated>50000) {

            lastUpdated=location.getTime();
            RequestParams params = new RequestParams();
            params.add("deviceId", "2");
            params.add("lat", String.valueOf(location.getLatitude()));
            params.add("long", String.valueOf(location.getLongitude()));
            params.add("speed", String.valueOf(speed));
            params.add("direction", String.valueOf(location.getBearing()));
            params.add("time", String.valueOf((location.getTime() / 1000L)));


            final AsyncHttpClient client = new AsyncHttpClient();
            client.setLoggingEnabled(true);
            client.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            client.get("http://tracker-test.96.lt/trackers/tracker.php", params, new AsyncHttpResponseHandler() {




                @Override
                public void onStart() {
                    // called before request is started


                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"

                    Log.d("LocationListener", "Log");
                    Log.d("LocationListener", new String(response));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.d("LocationListener", "Error Response" + statusCode);
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });


        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
