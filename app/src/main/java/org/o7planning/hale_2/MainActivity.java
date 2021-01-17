package org.o7planning.hale_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.o7planning.hale_2.AppUsageStats.AppUsageActivity;
import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.Charts.ChartAdapter;
import org.o7planning.hale_2.Charts.listviewitems.BarChartItem;
import org.o7planning.hale_2.Charts.listviewitems.ChartItem;
import org.o7planning.hale_2.Charts.listviewitems.LineChartItem;
import org.o7planning.hale_2.Charts.listviewitems.PieChartItem;
import org.o7planning.hale_2.GFitActivities.ScreenAnalysisActivity;
import org.o7planning.hale_2.GFitActivities.HeartBeatBPActivity;
import org.o7planning.hale_2.GFitActivities.SleepAnalysisActivity;
import org.o7planning.hale_2.GFitActivities.StepsActivity;
import org.o7planning.hale_2.Screens.Fragments.UpdateUserInfoFragment;
import org.o7planning.hale_2.Screens.LoginScreenActivity;
import org.o7planning.hale_2.Screens.ProfileScreenActivity;
import org.o7planning.hale_2.Screens.WaterMainActivity;
import org.o7planning.hale_2.retrofit.Activity_Retrofit;
import org.o7planning.hale_2.utils.ToastUtil;

import java.util.ArrayList;

/*

CircularProgress in Steps Activity
getgoal steps from alertbox input
set background

WaterMainActivity: setCustomIntake or setAccordingToWeight option
WaterIntake Notifications

Showing Data in profile Activity

*/


// Dashboard Screen
public class MainActivity extends AppCompatActivity {

    Button mSleepActivityButton;
    Button mHeartBeatActivityButton;
    Button mScreenAnalysisActivityButton;
    Button mStepsActivityButton;
    Button mWaterActivityButton;
    Button mAppUsageActivityButton;

    Button mRetrofitActivityButton;

    ImageView mMenuImageView;

    RecyclerView mChartRecyclerView;

    private static final Intent[] POWERMANAGER_INTENTS = {
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))};

    private int RC_SIGN_IN = 1822;

    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRetrofitActivityButton = findViewById(R.id.retrogit_activity_button);
        mRetrofitActivityButton.setVisibility(View.GONE);

        mRetrofitActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Activity_Retrofit.class);
            startActivity(intent);
        });

        createChannel();

//            Intent serviceintent = new Intent(this, YourService.class);
//            startService(serviceintent);

//---------------------------------------------------------------------------------------------------------------
//        final SharedPreferences.Editor pref = getSharedPreferences("allow_notify", MODE_PRIVATE).edit();
//        pref.apply();
//        final SharedPreferences sp = getSharedPreferences("allow_notify", MODE_PRIVATE);
//        if (!sp.getBoolean("protected", false)) {
//            for (final Intent intent : POWERMANAGER_INTENTS)
//                if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Alert Title").setMessage("Alert Body")
//                            .setPositiveButton("Ok", (dialogInterface, i) -> {
//                                startActivity(intent);
//                                sp.edit().putBoolean("protected", true).apply();
//
//                            })
//                            .setCancelable(false)
//                            .setNegativeButton("Cancel", (dialog, which) -> {
//                            })
//                            .create().show();
//                    break;
//                }
//        }

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser == null) {
//            List<AuthUI.IdpConfig> providers = Arrays.asList(
//                    new AuthUI.IdpConfig.EmailBuilder().build(),
//                    new AuthUI.IdpConfig.PhoneBuilder().build(),
//                    new AuthUI.IdpConfig.GoogleBuilder().build());
//
//            String url1 = "https://joebirch.co/terms.html";
//            String url2 = "https://joebirch.co/privacy.html";
//
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .setLogo(R.drawable.ic_launcher_foreground)
//                            .setTheme(R.style.LoginTheme)
//                            .setTosAndPrivacyPolicyUrls(url1, url2)
//                            .build(),
//                    RC_SIGN_IN
//            );
        } else {
            ToastUtil.makeLongToast(getApplicationContext(), "Hello" + mFirebaseUser.getDisplayName() + " " + mFirebaseUser.getEmail());
        }

        mSleepActivityButton = findViewById(R.id.sleep_activity_button);
        mScreenAnalysisActivityButton = findViewById(R.id.BP_activity_button);
        mWaterActivityButton = findViewById(R.id.water_activity_button);
        mStepsActivityButton = findViewById(R.id.step_counter_activity_button);
        mHeartBeatActivityButton = findViewById(R.id.heartbeat_activity_button);
        mAppUsageActivityButton = findViewById(R.id.app_usage_activity_button);

        mMenuImageView = findViewById(R.id.menu_imageview);

        mMenuImageView.setOnClickListener(v -> {
            popupMenu(mMenuImageView);
        });

        mChartRecyclerView = findViewById(R.id.chart_recyclerview);
        mChartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        setupCharts();

        mSleepActivityButton.setOnClickListener(v -> {
//            ToastUtil.makeLongToast(getApplicationContext(), "Work in Progress");
            Intent intent = new Intent(getApplicationContext(), SleepAnalysisActivity.class);
            startActivity(intent);
        });

        mHeartBeatActivityButton.setOnClickListener(v -> {
//            ToastUtil.makeLongToast(getApplicationContext(), "Work in Progress");
            Intent intent = new Intent(getApplicationContext(), HeartBeatBPActivity.class);
            startActivity(intent);
        });

        mWaterActivityButton.setOnClickListener(v -> {
//            ToastUtil.makeLongToast(getApplicationContext(), "Work in Progress");
            Intent intent = new Intent(getApplicationContext(), WaterMainActivity.class);
            startActivity(intent);
        });

        mScreenAnalysisActivityButton.setOnClickListener(v -> {
//            ToastUtil.makeLongToast(getApplicationContext(), "Work in Progress");
            Intent intent = new Intent(getApplicationContext(), ScreenAnalysisActivity.class);
            startActivity(intent);
        });

        mAppUsageActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppUsageActivity.class);
            startActivity(intent);
        });

        mStepsActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, StepsActivity.class);
            startActivity(intent);
        });

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        BroadcastReceiver mReceiver = new ScreenReceiver();
//        registerReceiver(mReceiver, filter);

    }

    public void popupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.logout) {
                        LogOutAlert();

                    } else if (item.getItemId() == R.id.my_profile) {
                        Intent intent = new Intent(this, ProfileScreenActivity.class);
                        startActivity(intent);
                    } else if (item.getItemId() == R.id.update_info) {
                        BottomSheetDialogFragment bottomSheetDialogFragment = new UpdateUserInfoFragment();
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    }
                    return false;
                }
        );

        popup.show();
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "primary_notification_channel";
            String description = "Channel for Reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("primary_notification_channel_hale", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupCharts() {
        ArrayList<ChartItem> list = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            if (i % 3 == 0) {
                list.add(new LineChartItem(generateDataLine(i + 1), getApplicationContext()));
            } else if (i % 3 == 1) {
                list.add(new BarChartItem(generateDataBar(i + 1), getApplicationContext()));
            } else if (i % 3 == 2) {
                list.add(new PieChartItem(generateDataPie(), getApplicationContext()));
            }
        }

        ChartAdapter chartAdapter = new ChartAdapter(getApplicationContext(), list);
        mChartRecyclerView.setAdapter(chartAdapter);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//            if (resultCode == Activity.RESULT_OK) {
//                Log.d(this.getClass().getName(), "This user signed in with " + response.getProviderType());
////                startUpTasks();
////                updateInfo();
//            } else {
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    Toast.makeText(this, "Signin cancelled", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    Toast.makeText(this, "Check network connection and try again", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                Toast.makeText(this, "Unexpected Error, we are trying to resolve the issue. Please check back soon", Toast.LENGTH_LONG).show();
////                Log.e(TAG, "Sign-in error: ", response.getError());
//            }
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//
//        if (item.getItemId() == R.id.logout) {
//            LogOutAlert();
////            FirebaseAuth.getInstance().signOut();
////            Intent intent = new Intent(this,LoginScreenActivity.class);
////            startActivity(intent);
//        } else if (item.getItemId() == R.id.my_profile) {
//            Intent intent = new Intent(this, ProfileScreenActivity.class);
//            startActivity(intent);
//        } else if (item.getItemId() == R.id.update_info) {
////            BottomSheetDialogFragment bottomSheetDialogFragment = new UpdateUserInfoFragment();
////            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//            ToastUtil.makeLongToast(getApplicationContext(),"Showing Fragment");
//        }
//        return super.onOptionsItemSelected(item);
//    }

    void LogOutAlert() {
        AlertDialog exitDialog = new AlertDialog.Builder(this).setCancelable(false).create();
        exitDialog.setMessage("Are you sure you wanna logout?");
        exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", (dialog, which) -> {

            AuthUI.getInstance().signOut(getApplicationContext());
            FirebaseAuth.getInstance().signOut();

            nullifyPreferenceManagerData();

            Intent logoutIntent = new Intent(MainActivity.this, LoginScreenActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });

        exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
            exitDialog.dismiss();
        });

        exitDialog.show();
    }

    private void nullifyPreferenceManagerData() {
        PreferencesManager.getInstance().putString("current_user", "");
        PreferencesManager.getInstance().putInt("target_water_intake", 0);
        PreferencesManager.getInstance().putInt("current_water_intake", 0);
        PreferencesManager.getInstance().putInt("daily_activity", 0);
    }

    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(values1, "Observed health status");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(3.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setCircleColor(Color.GRAY);
//        d1.setDrawValues(false);

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values2.add(new Entry(i, values1.get(i).getY() - 30));
        }

        LineDataSet d2 = new LineDataSet(values2, "Expected Health Status");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(3.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setCircleColor(Color.GRAY);
//        d2.setColor(Color.rgb(244, 117, 117));
//        d2.setCircleColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//        d2.setDrawValues(false);

        d1.setDrawCircles(true);
        d2.setDrawCircles(true);

        d1.setDrawCircleHole(true);
        d2.setDrawCircleHole(true);
//
//        if (d1.isDrawCirclesEnabled())
//            d1.setDrawCircles(false);
//        else
//            d1.setDrawCircles(true);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
    }

    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 70) + 30));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }

    private PieData generateDataPie() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Quarter " + (i + 1)));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        return new PieData(d);
    }

}
