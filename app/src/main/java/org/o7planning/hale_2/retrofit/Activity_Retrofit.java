package org.o7planning.hale_2.retrofit;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.o7planning.hale_2.R;
import org.o7planning.hale_2.retrofit.Retrofit_Models.UserRequest;
import org.o7planning.hale_2.retrofit.Retrofit_Models.User;
import org.o7planning.hale_2.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Activity_Retrofit extends AppCompatActivity {

    TextView mEmailTextView;
    TextView mPasswordTextView;

    Button mLoginUserButton;
    Button mSignupUserButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_retrofit);

        mEmailTextView = findViewById(R.id.email_textview_retrofit);
        mPasswordTextView = findViewById(R.id.password_textview_retrofit);

        mLoginUserButton = findViewById(R.id.retrofit_login_button);
        mSignupUserButton = findViewById(R.id.retrofit_signup_button);

        Gson gson = new Gson();

        mSignupUserButton.setOnClickListener(v -> {

            String email = mEmailTextView.getText().toString();
            String password = mPasswordTextView.getText().toString();

            User userRequest = new User(email, password);

            ServerApi.signupUser(userRequest).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();

                    ToastUtil.makeLongToast(getApplicationContext(),Integer.toString(response.code()));
                    if(response.code()==200)
                    {
                        showMessage("Sign-up Response", gson.toJson(user));
                    }
                    else
                    {
                        ToastUtil.makeLongToast(getApplicationContext(),"UnSuccessful");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ToastUtil.makeLongToast(getApplicationContext(), "Failed");
                    Log.e("RETROFIT",t.getMessage());
                }
            });
        });

        mLoginUserButton.setOnClickListener(v -> {

            String email = mEmailTextView.getText().toString();
            String password = mPasswordTextView.getText().toString();

            User userRequest = new User(email, password);

            ServerApi.loginUser(userRequest).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    showMessage("Login Response", gson.toJson(user));
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ToastUtil.makeLongToast(getApplicationContext(), "Failed");
                    Log.e("RETROFIT",t.getMessage());
                }
            });
        });
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle(title);
        alert.setMessage(Message);
        alert.show();
    }

}

