package org.o7planning.hale_2.Screens;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.o7planning.hale_2.MainActivity;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.utils.SnackbarUtil;
import org.o7planning.hale_2.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

public class LoginScreenActivity extends AppCompatActivity {

    private ProgressBar progress_bar;
    private FloatingActionButton mFloatingActionButton;
    private View parent_view;

    private int RC_SIGN_IN = 1822;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parent_view = findViewById(android.R.id.content);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        ((View) findViewById(R.id.sign_up_for_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parent_view, "Sign up for an account", Snackbar.LENGTH_SHORT).show();
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchAction();
            }
        });

    }

    private void searchAction() {
        progress_bar.setVisibility(View.VISIBLE);
        mFloatingActionButton.setAlpha(0f);
//        SnackbarUtil.makeLongSnack(parent_view,"Logging In");

        new Handler().postDelayed(() -> {
            progress_bar.setVisibility(View.GONE);
            mFloatingActionButton.setAlpha(1f);

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            String url1 = "https://joebirch.co/terms.html";
            String url2 = "https://joebirch.co/privacy.html";

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.hale)
                            .setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar)
                            .setTosAndPrivacyPolicyUrls(url1, url2)
                            .build(),
                    RC_SIGN_IN
            );

//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
        }, 500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
//                Log.d(this.getClass().getName(), "This user signed in with " + response.getProviderType());
                assert response != null;
                ToastUtil.makeLongToast(getApplicationContext(),"Signup Successful");

                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Signin cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Check network connection and try again", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, "Unexpected Error, we are trying to resolve the issue. Please check back soon", Toast.LENGTH_LONG).show();
//                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }


}
