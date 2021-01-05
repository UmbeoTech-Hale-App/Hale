package org.o7planning.hale_2.GFitActivities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.data.HealthFields;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.o7planning.hale_2.R;
import org.o7planning.hale_2.logger.Log;
import org.o7planning.hale_2.logger.LogView;
import org.o7planning.hale_2.logger.LogWrapper;
import org.o7planning.hale_2.logger.MessageOnlyLogFilter;
import org.o7planning.hale_2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HeartBeatBPActivity extends AppCompatActivity {

    Button mFetchData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gfit_heartbeat);
        mFetchData = findViewById(R.id.fetch_heartdata_button);

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                        .addDataType(DataType.TYPE_HEART_RATE_BPM)
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                        .build();

        mFetchData.setOnClickListener(v -> {
            Log.i("-------------", "-----------FETCHING-------------");

            if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                readHeartRate();
            } else {
                ToastUtil.makeLongToast(getApplicationContext(), "First enable Step Counter");
            }
        });

        initializeLogging();
    }

    public void readHeartRate() {

        TimeUnit interval = getInterval("hour");

        long endDate = System.currentTimeMillis();
        long startDate = System.currentTimeMillis() - (long) 6 * 60 * 60 * 1000;

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .bucketByTime(1, interval)
                .setTimeRange((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {

                        ToastUtil.makeLongToast(getApplicationContext(), "Success Heart Rate" + dataReadResponse.getBuckets().size());

                        Log.i("ReadHeartRate():", "dataReadResponse.getBuckets().size()" + Integer.toString(dataReadResponse.getBuckets().size()));

                        if (dataReadResponse.getBuckets().size() > 0) {

                            ArrayList heartRates = new ArrayList();

                            for (Bucket bucket : dataReadResponse.getBuckets()) {
                                List<DataSet> dataSets;
                                dataSets = bucket.getDataSets();

                                Log.i("HeartRate:", "bucket.getDataSets().size():" + Integer.toString(bucket.getDataSets().size()));

                                for (DataSet dataSet : dataSets) {
                                    processHeartRateDataSet(dataSet, heartRates);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastUtil.makeLongToast(getApplicationContext(), "Falied to get heart data");
                        Log.i("Error", e.getMessage());
                    }
                });
    }

    private void processHeartRateDataSet(DataSet dataSet, ArrayList map) {

        //Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
//        Format formatter = new SimpleDateFormat("EEE");
//        WritableMap stepMap = Arguments.createMap();

        Log.i("processHeartRateDataSet():", "Heart Dataset length: " + dataSet.getDataPoints().size());

        for (DataPoint dp : dataSet.getDataPoints()) {
            HashMap stepMap = new HashMap();
//            String day = formatter.format(new Date(dp.getStartTime(TimeUnit.MILLISECONDS)));
            int i = 0;

            for (Field field : dp.getDataType().getFields()) {
                i++;
                if (i > 1) continue;
//                stepMap.put("day", day);
                stepMap.put("startDate", dp.getStartTime(TimeUnit.MILLISECONDS));
                stepMap.put("endDate", dp.getEndTime(TimeUnit.MILLISECONDS));

//                Log.i("day", day);
                Log.i("startDate", String.valueOf(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i("endDate", String.valueOf(dp.getEndTime(TimeUnit.MILLISECONDS)));

//                if (this.dataType == HealthDataTypes.TYPE_BLOOD_PRESSURE) {
                // zz
                if (dp.getDataType() == HealthDataTypes.TYPE_BLOOD_PRESSURE) {
                    stepMap.put("diastolic", dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat());
                    stepMap.put("systolic", dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat());

                    Log.i("diastolic", String.valueOf(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat()));
                    Log.i("systolic", String.valueOf(dp.getValue(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat()));

                } else {
                    stepMap.put("value", dp.getValue(field).asFloat());

                    Log.i("value", String.valueOf(dp.getValue(field).asFloat()));
                }

                map.add(stepMap);
            }
        }
    }

    private static TimeUnit getInterval(String customInterval) {
        if (customInterval.equals("minute")) {
            return TimeUnit.MINUTES;
        }
        if (customInterval.equals("hour")) {
            return TimeUnit.HOURS;
        }
        return TimeUnit.DAYS;
    }


    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = findViewById(R.id.sample_logview);

        // Fixing this lint error adds logic without benefit.
        // noinspection AndroidLintDeprecation
        //logView.setTextAppearance(R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i("TAG", "Ready");
    }

}
