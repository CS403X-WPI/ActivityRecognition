package name.heqian.cs528.googlefit;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String PACKAGE_NAME = "name.heqian.cs528.googlefit";
    public static final String STRING_ACTION = PACKAGE_NAME + ".STRING_ACTION";
    public static final String STRING_EXTRA = PACKAGE_NAME + ".STRING_EXTRA";
    public GoogleApiClient mApiClient;
    private GoogleMap mMap;
    private LatLng latLng;
    Marker currLocationMarker;
    private InfoDBHandler dbHandler;
    public static ImageView imgView;
    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    private static long prevTime = 0;
    private Handler handler;
    private MediaPlayer mPlayer2 = new MediaPlayer();
    private static int previous = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        imgView = (ImageView) findViewById(R.id.imagepic);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        //creating the database
        dbHandler = new InfoDBHandler(this, null, null, 1);

        //mapFragment.getMapAsync(this);
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    // Show rationale and request permission.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    mMap.setMyLocationEnabled(true);
                }
            }
        });
        //imgView.setImageResource(R.drawable.in_vehicle);
        mApiClient.connect();
    }

    public int getDetectedActivity(int detectedActivityType) {
        Log.d("MESS", "Recieved activity string " + detectedActivityType);


        if (previous != detectedActivityType) {
            long currentTime = System.currentTimeMillis();
            Log.d("MESS", "Changed from " + previous + " after " + (currentTime - prevTime) + " ms");
            long totalTime = ((currentTime - prevTime) / 1000);
            long minutes = totalTime/60;
            long seconds = totalTime%60;
            showToast(getName(previous) + Long.toString(minutes) +" mins, " +seconds + " secs.");
            Log.d("MESS", "total time is " + totalTime + " ms");

            prevTime = currentTime;
            previous = detectedActivityType;

        }


//
//        if (detectedActivityType.contains("VEHICLE")) {
//            return R.drawable.in_vehicle;
//        } else if (detectedActivityType.contains("RUNNING")) {
//            return R.drawable.running;
//        } else if (detectedActivityType.contains("WALKING")) {
//            return R.drawable.walking;
//        } else if (detectedActivityType.contains("STILL")) {
//            return R.drawable.still;
//        } else {
//            return 0;
//        }

        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return R.drawable.in_vehicle;
            case DetectedActivity.RUNNING:
                playMedia();
                return R.drawable.running;
            case DetectedActivity.WALKING:
                playMedia();
                return R.drawable.walking;
            case DetectedActivity.STILL:
                return R.drawable.still;
            default:
                return -1;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = null;
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 100, pendingIntent);
        if ((this.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))
                == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mApiClient);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mApiClient);
        }
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
        }
        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(STRING_ACTION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
        }
        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MESS", "Recieved something " + intent.getStringExtra(STRING_EXTRA));
            int detectedAct = intent.getIntExtra(STRING_EXTRA, -1);
            int imageRes = getDetectedActivity(detectedAct);
            Log.d("MESS", "Image resource is " + imageRes);
            if (imageRes != -1) {
                imgView.setImageResource(imageRes);
            } else {
                imgView.setImageResource(R.drawable.startbutton);
            }

        }
    }


    private void showToast(final String txt) {
                Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();

    }

    public void playMedia() {

        try {
            //mPlayer2.reset();
            mPlayer2.setDataSource(this, Uri.parse("android.resource://name.heqian.cs528.googlefit/drawable/beat_02"));
            mPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer2.setLooping(false);
            mPlayer2.prepare();
            mPlayer2.start();


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public String getName(int type) {

         String text = "";

            switch (type) {

                case 0:
                    text = "You were in a vehicle for ";
                    break;

                case 1:
                    text = "You were on a bike for ";

                break;

                case 2:
                    text = "You were on foot for ";

                break;

                case 3:
                    text = "You were still for ";

                break;

                case 4:
                    text = "You were unknown for ";

                break;

                case 5:
                    text = "You were tilting for ";

                break;

                case 7:
                    text = "You were walking for ";

                break;

                case 8:
                    text = "You were running for ";

                break;

                default:
                    text = "error";

            }
            return text;
        }
    }
