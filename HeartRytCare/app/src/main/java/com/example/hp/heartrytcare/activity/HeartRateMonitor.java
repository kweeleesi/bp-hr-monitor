package com.example.hp.heartrytcare.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.example.hp.heartrytcare.HeartRytCare;
import com.example.hp.heartrytcare.R;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.db.HeartRateData;
import com.example.hp.heartrytcare.db.HeartRateDataDao;
import com.example.hp.heartrytcare.db.LimitValues;
import com.example.hp.heartrytcare.db.LimitValuesDao;
import com.example.hp.heartrytcare.fragment.CriticalRateFragment;
import com.example.hp.heartrytcare.helper.Constants;
import com.example.hp.heartrytcare.helper.ImageProcess;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.greenrobot.dao.query.QueryBuilder;


public class HeartRateMonitor extends FragmentActivity {
    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static Context context;
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static AlertDialog.Builder dialog = null;
    private static TextView bpm = null;
    private static ArcProgress arcProgress = null;
    private static AVLoadingIndicatorView loading = null;
    private static TextView bpmFinal;
    private static TextView bpmLabel;

    private static WakeLock wakeLock = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    private static HeartRateDataDao hrDao;
    private static LimitValuesDao lvDao;
    private static LimitValues lv;

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    private static float progress = 0;
    private static float percentage = 0;
    private static boolean isSaved = false;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_monitor);
        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        dialog = new AlertDialog.Builder(getApplicationContext());
        bpm = (TextView) findViewById(R.id.bpm);
        loading = (AVLoadingIndicatorView) findViewById(R.id.loading);
        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);
        arcProgress.setMax(100);
        bpmFinal = (TextView) findViewById(R.id.bpmFinal);
        bpmLabel = (TextView) findViewById(R.id.bpmLabel);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        DaoSession daoSession = ((HeartRytCare)getApplicationContext()).getDaoSession();
        hrDao = daoSession.getHeartRateDataDao();
        lvDao = daoSession.getLimitValuesDao();

        QueryBuilder<LimitValues> query = lvDao.queryBuilder();
        query.where(LimitValuesDao.Properties.Firebase_user_id.eq(Constants.FIREBASE_UID));
        if (query.list() != null && query.list().size() != 0) {
            lv = query.list().get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

        isSaved = false;
        percentage = 0f;
        progress = 0f;

        camera = Camera.open();

        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private static PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            int z = 0;
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcess.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            // Log.i(TAG, "imgAvg="+imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;                                        //count as beat
                    // Log.d(TAG, "BEAT!! beats="+beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
            }

            if (!isSaved) {
                progress = (percentage++ / 140f) * 100f;
                arcProgress.setProgress((int) progress);
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {                            // hits every 10secs
                double bps = (beats / totalTimeInSecs);             // beats DIVIDED BY timeInSec above 10secs
                int dpm = (int) (bps * 60d);                        // bps * 60secs(1min) = 1 BPM value
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                // Log.d(TAG,
                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);     //total average beat for the whole duration of session7

                if (!isSaved) {
                    bpm.setText("YOUR HEART RATE IS");
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    arcProgress.setProgress(100);
                    arcProgress.setSuffixText("");
                    arcProgress.setTextColor(ContextCompat.getColor(HeartRytCare.getAppContext(), R.color.main_app_bg));
                    loading.setVisibility(View.GONE);
                    bpmLabel.setVisibility(View.VISIBLE);
                    bpmFinal.setVisibility(View.VISIBLE);
                    bpmFinal.setText(String.valueOf(beatsAvg));

                    Date d = new Date();
                    CharSequence date = DateFormat.format("MM/dd", d.getTime());

                    HeartRateData hr = new HeartRateData();
                    hr.setFirebase_user_id(Constants.FIREBASE_UID);
                    hr.setBpm(beatsAvg);
                    hr.setDate(date.toString());
                    hr.setTimestamp(System.currentTimeMillis());
                    hrDao.insert(hr);

                    if (lv != null && !lv.getHrLimit().equals("")) {
                        if (beatsAvg > Integer.parseInt(lv.getHrLimit())) {
                            MessageEvent event = new MessageEvent();
                            event.hr = beatsAvg;
                            EventBus.getDefault().post(event);
                        }
                    }

                    Log.e("HeartRateMonitor", "SAVED!!!");
                    isSaved = true;
                }
            }
            processing.set(false);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkBPAlert(MessageEvent event) {
        CriticalRateFragment criticalRateFragment = CriticalRateFragment.newInstance(
                CriticalRateFragment.CASE_TYPE_HR,
                lv.getHrLimit(),
                String.valueOf(event.hr));
        criticalRateFragment.show(getSupportFragmentManager(), "criticalState");
    }

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    public static class MessageEvent {
        public int hr;
    }
}
