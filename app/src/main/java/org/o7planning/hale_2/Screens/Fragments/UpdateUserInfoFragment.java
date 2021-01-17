package org.o7planning.hale_2.Screens.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.pchmn.materialchips.views.ChipsInputEditText;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.retrofit.Retrofit_Models.User;
import org.o7planning.hale_2.retrofit.ServerApi;
import org.o7planning.hale_2.utils.ToastUtil;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateUserInfoFragment extends BottomSheetDialogFragment {

    public UpdateUserInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View mView;
    Button mUpdateInfoButton;

    EditText mAgeTextView;
    EditText mWeightTextView;
    EditText mHeightTextView;
    EditText mCustomWaterIntakeTextView;
    EditText mDailyActivityTextView;

    int age, weight, height, dailyActivity, customWaterIntake;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_umbeo_user_data_update_bottom_sheet, container, false);

//        ToastUtil.makeShortToast(getContext(), "Creating");

        mView = view;
        mUpdateInfoButton = mView.findViewById(R.id.update_button_update_info_fragment);

        mAgeTextView = mView.findViewById(R.id.age_update_info_fragment_textview);
        mWeightTextView = mView.findViewById(R.id.weight_update_info_fragment_textview);
        mHeightTextView = mView.findViewById(R.id.height_update_info_fragment_textview);
        mCustomWaterIntakeTextView = mView.findViewById(R.id.custom_water_intake_update_info_fragment_textview);
        mDailyActivityTextView = mView.findViewById(R.id.daily_workout_update_info_fragmenr_textview);

        Gson gson = new Gson();
        User user = gson.fromJson(PreferencesManager.getInstance().getString("current_user"), User.class);

        dailyActivity = PreferencesManager.getInstance().getInt("daily_activity");
        customWaterIntake = PreferencesManager.getInstance().getInt("target_water_intake");

        mUpdateInfoButton.setOnClickListener(v -> {

            ToastUtil.makeShortToast(getContext(), "Updating, Please Wait");
//            Objects.requireNonNull(getActivity()).onBackPressed();

            if (!mAgeTextView.getText().toString().equals("")) {
                try {
                    age = Integer.parseInt(mAgeTextView.getText().toString());
                } catch (NumberFormatException numberFormatException) {
                    ToastUtil.makeShortToast(getContext(), "Please enter numeric value for age");
                    return;
                }
                user.setAge(age);

            }

            if (!mWeightTextView.getText().toString().equals("")) {
                try {
                    weight = Integer.parseInt(mWeightTextView.getText().toString());
                } catch (NumberFormatException numberFormatException) {
                    ToastUtil.makeShortToast(getContext(), "Please enter numeric value for weight");
                    return;
                }
                user.setWeight(weight);
            }

            if (!mHeightTextView.getText().toString().equals("")) {
                try {
                    height = Integer.parseInt(mHeightTextView.getText().toString());
                } catch (NumberFormatException numberFormatException) {
                    ToastUtil.makeShortToast(getContext(), "Please enter numeric value for height");
                    return;
                }
                user.setHeight(height);

            }

            if (!mDailyActivityTextView.getText().toString().equals("")) {
                try {
                    dailyActivity = Integer.parseInt(mDailyActivityTextView.getText().toString());
                } catch (NumberFormatException numberFormatException) {
                    ToastUtil.makeShortToast(getContext(), "Please enter numeric value for Daily Activity");
                    return;
                }
                PreferencesManager.getInstance().putInt("daily_activity", dailyActivity);
            }

            if (!mCustomWaterIntakeTextView.getText().toString().equals("")) {
                try {
                    customWaterIntake = Integer.parseInt(mCustomWaterIntakeTextView.getText().toString());
                } catch (NumberFormatException numberFormatException) {
                    ToastUtil.makeShortToast(getContext(), "Please enter numeric value for water intake");
                    return;
                }
                PreferencesManager.getInstance().putInt("target_water_intake", customWaterIntake);

            }


            ServerApi.updateUser(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if (response.isSuccessful()) {
                        PreferencesManager.getInstance().putString("current_user", gson.toJson(user));
                        ToastUtil.makeShortToast(getContext(), "Updated Successfully!");
                    } else {
                        ToastUtil.makeShortToast(getContext(), "Server Error");
                    }
                    dismiss();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ToastUtil.makeShortToast(getContext(), "Wasn't able to connect to server");
                    dismiss();
                }
            });

        });

        setup(user);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return super.onCreateDialog(savedInstanceState);
    }

    private void setup(User user) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (user.getWeight() != -1 && mWeightTextView != null) {
                    mWeightTextView.setText(String.valueOf(user.getWeight()));
                }

                if (user.getHeight() != -1 && mHeightTextView != null) {
                    mHeightTextView.setText(String.valueOf(user.getHeight()));
                }

                if (user.getAge() != -1 && mAgeTextView != null) {
                    mAgeTextView.setText(String.valueOf(user.getAge()));
                }

                dailyActivity = PreferencesManager.getInstance().getInt("daily_activity");
                customWaterIntake = PreferencesManager.getInstance().getInt("target_water_intake");

                if (mDailyActivityTextView != null) {
                    mDailyActivityTextView.setText(String.valueOf(dailyActivity));
                }

                if (mCustomWaterIntakeTextView != null) {
                    mCustomWaterIntakeTextView.setText(String.valueOf(customWaterIntake));
                }


            }
        }, 500);
    }

}
