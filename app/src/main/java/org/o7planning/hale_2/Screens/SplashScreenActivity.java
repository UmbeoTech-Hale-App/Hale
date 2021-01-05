package org.o7planning.hale_2.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.MainActivity;
import org.o7planning.hale_2.R;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if(!PreferencesManager.getInstance().getBoolean("swipe_screen_shown"))
                {
                    PreferencesManager.getInstance().putBoolean("swipe_screen_shown",true);

                    // show swipe screen
                    Intent intent = new Intent(getApplicationContext(), SwipeScreenActivity.class);
                    startActivity(intent);

                    // close this activity
                    finish();
                }
                else
                {
                    if(mFirebaseUser!=null)
                    {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        // close this activity
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                        startActivity(intent);

                        // close this activity
                        finish();
                    }
                }

            }
        }, SPLASH_TIME_OUT);
    }

}
