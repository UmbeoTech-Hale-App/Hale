package org.o7planning.hale_2.Screens;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.MainActivity;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.retrofit.Retrofit_Models.User;
import org.o7planning.hale_2.retrofit.ServerApi;
import org.o7planning.hale_2.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreenActivity extends AppCompatActivity {

    private ProgressBar progress_bar;
    private FloatingActionButton mSignUpLoginButton;
    private View parent_view;

    TextView mUserNameTextView;
    TextView mPasswordTextView;
    TextView mCurrentActionTextView;
    TextView mSignUpLoginOptionBottomTextView;

    LinearLayout mSignupLoginOptionLinearLayout;

    int state = 1;

    private int RC_SIGN_IN = 1822;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parent_view = findViewById(android.R.id.content);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        mSignUpLoginButton = (FloatingActionButton) findViewById(R.id.fab);

        mUserNameTextView = findViewById(R.id.username_login_textview);
        mPasswordTextView = findViewById(R.id.password_login_textview);
        mCurrentActionTextView = findViewById(R.id.current_action_login_textview);
        mSignUpLoginOptionBottomTextView = findViewById(R.id.signup_login_option_bottom_textview);

        mSignupLoginOptionLinearLayout = findViewById(R.id.login_signup_option_bottom_linearlayout);

        mSignupLoginOptionLinearLayout.setOnClickListener(v -> {
            state = 1 - state;

            if (state == 1) {
                mCurrentActionTextView.setText("Log In");
                mSignUpLoginOptionBottomTextView.setText("Sign up for an account?");
            } else {
                mCurrentActionTextView.setText("Sign Up");
                mSignUpLoginOptionBottomTextView.setText("Login to your account?");
            }
            mPasswordTextView.setText("");
            mUserNameTextView.setText("");
        });

        mSignUpLoginButton.setOnClickListener(v -> {
            process();
        });

    }

    private void process() {

        String email = mUserNameTextView.getText().toString();
        String password = mUserNameTextView.getText().toString();

        progress_bar.setVisibility(View.VISIBLE);
        mSignUpLoginButton.setAlpha(0f);

        Handler handler = new Handler();
        User userRequest = new User(email, password);

        if (email.equals("")) {
            ToastUtil.makeLongToast(getApplicationContext(), "username cannot be empty");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSignUpLoginButton.setAlpha(1f);
                }
            }, 800);
        }
        if (password.equals("")) {
            ToastUtil.makeLongToast(getApplicationContext(), "password cannot be empty");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSignUpLoginButton.setAlpha(1f);
                }
            }, 800);
        }

        Gson gson = new Gson();

        if (state == 1) {
            // login
            ServerApi.loginUser(userRequest).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();

                    ToastUtil.makeLongToast(getApplicationContext(), Integer.toString(response.code()));
//                    showMessage("Login Response", gson.toJson(user));
                    if (response.code() == 200) {
//                        showMessage("Sign-up Response", gson.toJson(user));
                        PreferencesManager.getInstance().putString("current_user", gson.toJson(user));

                        if (user.getAge() != -1 && user.getWeight() != -1 && user.getHeight() != -1) {
                            goToMainActivity();
                        } else {
                            goToCompleteProfileActivity();
                        }
                    } else {
                        ToastUtil.makeLongToast(getApplicationContext(), "No Such Account");
                    }
                    mSignUpLoginButton.setAlpha(1f);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ToastUtil.makeLongToast(getApplicationContext(), "Failed");
                    Log.e("RETROFIT", t.getMessage());
                    mSignUpLoginButton.setAlpha(1f);
                }
            });

        } else {

            //signup
            ServerApi.signupUser(userRequest).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();

                    ToastUtil.makeLongToast(getApplicationContext(), Integer.toString(response.code()));
//                    showMessage("Sign-up Response", gson.toJson(user));
                    if (response.code() == 200) {
                        PreferencesManager.getInstance().putString("current_user", gson.toJson(user));

                        if (user.getAge() != -1 && user.getWeight() != -1 && user.getHeight() != -1) {
                            goToMainActivity();
                        } else {
                            goToCompleteProfileActivity();
                        }
                    } else {
                        ToastUtil.makeLongToast(getApplicationContext(), "Please Choose a different Username");
                    }
                    mSignUpLoginButton.setAlpha(1f);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ToastUtil.makeLongToast(getApplicationContext(), "Failed");
                    Log.e("RETROFIT", t.getMessage());
                    mSignUpLoginButton.setAlpha(1f);
                }
            });

        }

    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle(title);
        alert.setMessage(Message);
        alert.show();
    }

    private void setUpPreferences()
    {
        PreferencesManager.getInstance().putInt("target_water_intake",3000);
        PreferencesManager.getInstance().putInt("target_steps",1000);
    }

    private void goToCompleteProfileActivity() {
        Intent intent = new Intent(this, CompleteProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

//    private void setup() {
//        progress_bar.setVisibility(View.VISIBLE);
//        mSignUpLoginButton.setAlpha(0f);
//        SnackbarUtil.makeLongSnack(parent_view,"Logging In");

//       setupGoogleSignin();
//    }

//    private void setupGoogleSignin() {
//        new Handler().postDelayed(() -> {
//            progress_bar.setVisibility(View.GONE);
//            mFloatingActionButton.setAlpha(1f);
//
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
//                            .setLogo(R.drawable.hale)
//                            .setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar)
//                            .setTosAndPrivacyPolicyUrls(url1, url2)
//                            .build(),
//                    RC_SIGN_IN
//            );
//
////            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////            startActivity(intent);
//        }, 500);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if (resultCode == Activity.RESULT_OK) {
////                Log.d(this.getClass().getName(), "This user signed in with " + response.getProviderType());
//                assert response != null;
//                ToastUtil.makeLongToast(getApplicationContext(),"Signup Successful");
//
//                Intent intent = new Intent(this,MainActivity.class);
//                startActivity(intent);
//                finish();
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


}
