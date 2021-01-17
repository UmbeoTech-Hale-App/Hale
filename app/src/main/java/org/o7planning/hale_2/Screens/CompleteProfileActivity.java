package org.o7planning.hale_2.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.MainActivity;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.retrofit.Retrofit_Models.User;
import org.o7planning.hale_2.retrofit.ServerApi;
import org.o7planning.hale_2.utils.SnackbarUtil;
import org.o7planning.hale_2.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompleteProfileActivity extends AppCompatActivity {

    TextView mAgeTextView;
    TextView mWeightTextView;
    TextView mHeightTextView;

    Button mSubmitButton;
    Button mSkipButton;

    RadioButton mFemaleRadioButton;
    RadioButton mMaleRadioButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umbeo_complete_registration);

        mAgeTextView = findViewById(R.id.complete_profile_age_textview);
        mWeightTextView = findViewById(R.id.complete_profile_weight_textview);
        mHeightTextView = findViewById(R.id.complete_profile_height_textview);

        mSubmitButton = findViewById(R.id.submit_button_complete_profile);
        mSkipButton = findViewById(R.id.skip_button_complete_profile);

        mMaleRadioButton = findViewById(R.id.male_radio_button_complete_registration);
        mFemaleRadioButton = findViewById(R.id.female_radio_button_complete_registration);

        mSubmitButton.setOnClickListener(v -> {
            Gson gson = new Gson();
            User user = gson.fromJson(PreferencesManager.getInstance().getString("current_user"), User.class);
            int age, weight, height;

            try {
                age = Integer.parseInt(mAgeTextView.getText().toString());
            } catch (NumberFormatException nfe) {
                ToastUtil.makeLongToast(getApplicationContext(), "Please Enter Numeric value for age");
                return;
            }

            try {
                weight = Integer.parseInt(mWeightTextView.getText().toString());
            } catch (NumberFormatException nfe) {
                ToastUtil.makeLongToast(getApplicationContext(), "Please Enter Numeric value for weight");
                return;
            }

            try {
                height = Integer.parseInt(mHeightTextView.getText().toString());
            } catch (NumberFormatException nfe) {
                ToastUtil.makeLongToast(getApplicationContext(), "Please Enter Numeric value for height");
                return;
            }

            user.setAge(age);
            user.setHeight(height);
            user.setWeight(weight);

//            if (user.getAge() != -1) {
//                mAgeTextView.setText(user.getAge());
//            }
//            if (user.getHeight() != -1) {
//                mHeightTextView.setText(user.getHeight());
//            }
//            if (user.getWeight() != -1) {
//                mWeightTextView.setText(user.getWeight());
//            }

            if (mFemaleRadioButton.isChecked()) {
                user.setGender("female");
            } else if (mMaleRadioButton.isChecked()) {
                user.setGender("male");
            } else {
                ToastUtil.makeShortToast(getApplicationContext(), "Gender?");
                return;
            }

            final User user1 = user;

            ServerApi.updateUser(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if (response.code() == 200) {
                        User user = response.body();

                        PreferencesManager.getInstance().putString("current_user", gson.toJson(user1));

                        goToMainActivity();
                    } else {
                        ToastUtil.makeLongToast(getApplicationContext(), "Server Error");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ToastUtil.makeLongToast(getApplicationContext(), "Please Try Again After Some Time");
                }
            });
        });

        mSkipButton.setOnClickListener(v -> {
            goToMainActivity();
        });

    }

    private void goToMainActivity() {
        Handler handler = new Handler();
        Intent intent = new Intent(this, MainActivity.class);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 1000);

    }

}
