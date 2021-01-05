package org.o7planning.hale_2.GFitActivities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.gson.Gson;

import org.o7planning.hale_2.R;
import org.o7planning.hale_2.logger.Log;
import org.o7planning.hale_2.logger.LogView;
import org.o7planning.hale_2.logger.LogWrapper;
import org.o7planning.hale_2.logger.MessageOnlyLogFilter;
import org.o7planning.hale_2.utils.ToastUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SleepAnalysisActivity extends AppCompatActivity {

    Button mFetchSleepAnalysisData;

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());

    private static final String[] SLEEP_STAGE_NAMES = {
            "Unused",
            "Awake (during sleep)",
            "Sleep",
            "Out-of-bed",
            "Light sleep",
            "Deep sleep",
            "REM sleep"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gfit_sleep);

        initializeLogging();
        Log.i("Hello", "Hello");


        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                        .addDataType(DataType.TYPE_HEART_RATE_BPM)
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                        .build();


        mFetchSleepAnalysisData = findViewById(R.id.fetch_sleepdata_button);

        mFetchSleepAnalysisData.setOnClickListener(v -> {
            Log.i("-------------", "FETCHING-------------");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                readSleepData();
            } else {
                ToastUtil.makeLongToast(getApplicationContext(), "First enable Step Counter");
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readSleepData() {


        Log.i("readSleepData", "readSleepData()");

        long endDate = System.currentTimeMillis();
        long startDate = System.currentTimeMillis() - (long) 16 * 60 * 60 * 1000;

        //////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////

        ////////////////////////////////////////
//        TimeUnit interval = getInterval("hour");
//        DataReadRequest readRequest = new DataReadRequest.Builder()
//                .read(DataType.TYPE_ACTIVITY_SEGMENT)
//                .bucketByTime(1, interval)
//                .setTimeRange((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
//                .build();
        /////////////////////////////////////////

        SessionReadRequest request = new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeInterval((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getSessionsClient(this, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)))
                .readSession(request)
                .addOnSuccessListener(response -> {

                    ToastUtil.makeLongToast(getApplicationContext(), "Success Sleep read");
                    Log.i("Success Response", response.toString());
                    Gson gson = new Gson();

                    List<Object> sleepSessions = response.getSessions()
                            .stream()
                            .filter(new Predicate<Session>() {
                                @Override
                                public boolean test(Session s) {
//                                    ToastUtil.makeLongToast(getApplicationContext(), "Activity equals Fitness.SLEEP? " + Boolean.toString(s.getActivity().equals(FitnessActivities.SLEEP)));
                                    return s.getActivity().equals(FitnessActivities.SLEEP);
                                }
                            })
                            .collect(Collectors.toList());

                    ArrayList sleep = new ArrayList();

//                    ToastUtil.makeLongToast(getApplicationContext(), "After Collector list size:" + sleepSessions.size());

                    Log.i("Success Response", gson.toJson(sleepSessions));
                    Log.i("sleepSessions.size():", "sleepSessions.size():" + Integer.toString(sleepSessions.size()));

//////////////////////////////////////////////////////////////////////////////////

                    Log.i("TAG", " response.getSessions().size():" + Integer.toString(response.getSessions().size()));

                    for (Session session : response.getSessions()) {
                        long sessionStart = session.getStartTime(TimeUnit.MILLISECONDS);
                        long sessionEnd = session.getEndTime(TimeUnit.MILLISECONDS);
                        Log.i("TAG", "Sleep between $sessionStart and $sessionEnd");

                        // If the sleep session has finer granularity sub-components, extract them:
                        List<DataSet> dataSets = response.getDataSet(session);
                        for (DataSet dataSet : dataSets) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                int sleepStageVal = dataPoint.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt();
                                String sleepStage = SLEEP_STAGE_NAMES[sleepStageVal];
                                long segmentStart = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
                                long segmentEnd = dataPoint.getEndTime(TimeUnit.MILLISECONDS);

                                Log.i("c", "sleepStageVal:" + Integer.toString(sleepStageVal));
                                Log.i("c", "segmentStart:" + Long.toString(segmentStart));
                                Log.i("c", "segmentEnd:" + Long.toString(segmentEnd));

                                Log.i("TAG", "\t* Type $sleepStage between $segmentStart and $segmentEnd");
                            }
                        }
                    }
//////////////////////////////////////////////////////////////////////////////////

                    for (Object session : sleepSessions) {
                        List<DataSet> dataSets = response.getDataSet((Session) session);

                        Log.i("a", "dataSets.size():" + Integer.toString(dataSets.size()));

                        for (DataSet dataSet : dataSets) {
                            processSleep(dataSet, (Session) session, sleep);
                        }
                    }
//                        promise.resolve(sleep);
                })
                .addOnFailureListener(e -> {
                    ToastUtil.makeLongToast(getApplicationContext(), "Failed readSleepData fetching session data");
//                        promise.reject(e);
                    Log.i("STATUS:", "Failed to get response");
                });

    }

    private void processSleep(DataSet dataSet, Session session, ArrayList map) {

        HashMap sleepMap = new HashMap();

        Log.i("processSleep(), dataSet.getDataPoints().size():", Integer.toString(dataSet.getDataPoints().size()));

        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                sleepMap.put("value", dp.getValue(field).asActivity());
                sleepMap.put("sourceId", session.getIdentifier());
                sleepMap.put("added_by", session.getAppPackageName());
                sleepMap.put("startDate", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                sleepMap.put("endDate", dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
//                sleepMap.put("sleepStage", dp.getValue(Field.FIELD_SL).asInt());

                Log.i("value", dp.getValue(field).asActivity());
                Log.i("sourceId", session.getIdentifier());
                Log.i("startDate", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i("endDate", dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

                map.add(sleepMap);
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
