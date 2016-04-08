package name.heqian.cs528.googlefit;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.media.AudioManager;
import android.media.MediaPlayer;
import java.util.Calendar;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;
import android.os.Handler;

/**
 * Created by Paul on 2/1/16.
 */



public class ActivityRecognizedService extends IntentService {
    public static final String PACKAGE_NAME = "name.heqian.cs528.googlefit";
    public static final String STRING_ACTION = PACKAGE_NAME + ".STRING_ACTION";
    public static final String STRING_EXTRA = PACKAGE_NAME + ".STRING_EXTRA";
    public static final String Mess = "OTHER";
    static private int previousActivity = -1;
    private Handler handler;
    private MediaPlayer mPlayer2 = new MediaPlayer();
    long elapsedTime = 0;
    Calendar c = Calendar.getInstance();
    long startTime = System.currentTimeMillis();

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(chooseBestResult(result.getProbableActivities()));
            Intent i = new Intent(STRING_ACTION);
            i.putExtra(STRING_EXTRA, chooseBestResult(result.getProbableActivities()).getType());
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    private DetectedActivity chooseBestResult (List<DetectedActivity> probableActivities) {
        int max = 0;
        DetectedActivity best = null;
        for (DetectedActivity act: probableActivities) {
            if (act.getType() == DetectedActivity.IN_VEHICLE || act.getType() == DetectedActivity.RUNNING
                    || act.getType() == DetectedActivity.WALKING || act.getType() == DetectedActivity.STILL) {
                if (act.getConfidence() >= max) {
                    max = act.getConfidence();
                    best = act;
                }
            }
        }
        return best;

    }
    private void handleDetectedActivities(DetectedActivity activity) {
//            System.out.println(activity);
//        System.out.println("previous activity:"+ previousActivity);
//            switch (activity.getType()) {
//                case DetectedActivity.IN_VEHICLE: {
//                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
//                    if (activity.getConfidence() >= 75) {
//                        if(previousActivity!=activity.getType()) {
//                            Log.d(Mess, "changing activity to vehicle");
//                            startTime = System.currentTimeMillis();
//                        }
//                        mPlayer2.stop();
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                        builder.setContentText("Are you in a Automobile?");
//                        builder.setSmallIcon(R.mipmap.ic_launcher);
//                        builder.setContentTitle(getString(R.string.app_name));
//                        NotificationManagerCompat.from(this).notify(0, builder.build());
//                        //previousActivity = currentActivity;
//
//                        //currentActivity = DetectedActivity.IN_VEHICLE;
//                        //System.out.println("Current Activity for Vehicle is: "+ currentActivity);
//
//                        System.out.println("Setting start time in vehicle");
//
//                    }
//                    break;
//                }
//
//
//                case DetectedActivity.RUNNING: {
//                    Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
//                    if (activity.getConfidence() >= 75) {
//                        if(previousActivity!=activity.getType()) {
//                            startTime = System.currentTimeMillis();
//                            Log.d(Mess, "changing activity to running");
//
//                        }
//                        System.out.println("Start Time Running: " + startTime);
//                        playMedia();
//                        System.out.println("PLaying media");
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                        builder.setContentText("Are you Running?");
//                        builder.setSmallIcon(R.mipmap.ic_launcher);
//                        builder.setContentTitle(getString(R.string.app_name));
//                        NotificationManagerCompat.from(this).notify(0, builder.build());
//                        //previousActivity = currentActivity;
//
////                        currentActivity = DetectedActivity.RUNNING;
//                        //System.out.println("Current Activity for Runing is: "+ currentActivity);
//
//                        System.out.println("Setting start time in running");
//
//                    }
//                    break;
//                }
//                case DetectedActivity.STILL: {
//                    Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
//                    if (activity.getConfidence() >= 75) {
//                        if(previousActivity!=activity.getType()) {
//                            startTime = System.currentTimeMillis();
//                            Log.d(Mess,"changing activity to still");
//                        }
//                        System.out.println("Start Time Still: " +startTime);
//                        System.out.println("Elapsed Time Still: " + elapsedTime);
//                        mPlayer2.stop();
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                        builder.setContentText("Are you standing still?");
//                        builder.setSmallIcon(R.mipmap.ic_launcher);
//                        builder.setContentTitle(getString(R.string.app_name));
//                        NotificationManagerCompat.from(this).notify(0, builder.build());
//                        //previousActivity = currentActivity;
//
////                        currentActivity = DetectedActivity.STILL;
//                        //System.out.println("Current Activity for Still is: " +currentActivity);
//
//                        System.out.println("Setting start time in still");
//
//
//                    }
//                    break;
//                }
//
//                case DetectedActivity.WALKING: {
//                    Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
//                    if (activity.getConfidence() >= 75) {
//                        if(previousActivity!=activity.getType()) {
//                            startTime = System.currentTimeMillis();
//                            Log.d(Mess,"changing activity to walking");
//                        }
//                        System.out.println("Start Time Walking: " + startTime);
//                        playMedia();
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                        builder.setContentText("Are you walking?");
//                        builder.setSmallIcon(R.mipmap.ic_launcher);
//                        builder.setContentTitle(getString(R.string.app_name));
//                        NotificationManagerCompat.from(this).notify(0, builder.build());
//                        //previousActivity = currentActivity;
//                        //currentActivity = DetectedActivity.WALKING;
//                        //System.out.println("Current Activity for Walking is: " +currentActivity);
//                        System.out.println("Setting start time in walking");
//
//
//                    }
//                    break;
//                }
//
//            }

//        if (previousActivity != activity.getType()) {
//
//           System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//            System.out.println("DIFFERENT");
//
//            long currentTime = System.currentTimeMillis();
//
//            System.out.println("Current Time is: " + currentTime);
//            System.out.println("Start Time is: " + startTime);
//
//            elapsedTime = currentTime - startTime;
//            System.out.println("Elapsed Time After 1000: "+elapsedTime);
//            int minutes = 0;
//            int seconds = 0;
//
//            while (elapsedTime > 60) {
//                elapsedTime = elapsedTime - 60;
//                minutes++;
//            }
//
//            seconds = (int) elapsedTime;
//
//            String time = Integer.toString(minutes) + "min, " + Integer.toString(seconds) + " seconds.";
//
//            if(previousActivity >= 0) {
//                String text = "";
//
//                switch (previousActivity) {
//
//                    case 0:
//                        text = "You were in a vehicle for ";
//                        break;
//
//                    case 1:
//                        text = "You were on a bike for ";
//                        break;
//
//                    case 2:
//                        text = "You were on foot for ";
//                        break;
//
//                    case 3:
//                        text = "You were still for ";
//                        break;
//
//                    case 4:
//                        text = "You were unknown for ";
//                        break;
//
//                    case 5:
//                        text = "You were tilting for ";
//                        break;
//
//                    case 7:
//                        text = "You were walking for ";
//                        break;
//
//                    case 8:
//                        text = "You were running for ";
//                        break;
//
//                }
//
//
//                showToast(text + time);
//            }
//            previousActivity = activity.getType();
//        }
    }
    private void showToast(final String txt)
    {
        handler.post(new Runnable(){
            @Override
            public void run()
            {
                Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void playMedia(){

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
}
