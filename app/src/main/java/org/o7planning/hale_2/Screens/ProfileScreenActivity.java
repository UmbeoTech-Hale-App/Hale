package org.o7planning.hale_2.Screens;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.o7planning.hale_2.AppUsageStats.util.PreferencesManager;
import org.o7planning.hale_2.R;
import org.o7planning.hale_2.retrofit.Retrofit_Models.User;

public class ProfileScreenActivity extends AppCompatActivity {

    FirebaseUser mFirebaseUser;

    TextView mNameTextView;
    TextView mProfileTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mNameTextView = findViewById(R.id.name_profile_textview);
        mProfileTextView = findViewById(R.id.profile_data_textview);

        Gson gson = new Gson();
        User user = gson.fromJson(PreferencesManager.getInstance().getString("current_user"), User.class);

        mNameTextView.setText(String.format("Hello %s", user.getEmail()));

        String userData = "";

        if (user.getAge() != -1) {
            userData += "Age : " + Integer.toString(user.getAge());
        } else {
            userData += "Age : " + "?";
        }
        userData += "\n\n";


        if (user.getAge() != -1) {
            userData += "Weight : " + Integer.toString(user.getWeight());
        } else {
            userData += "Weight : " + "?";
        }
        userData += "\n\n";


        if (user.getAge() != -1) {
            userData += "Height : " + Integer.toString(user.getHeight());
        } else {
            userData += "Height : " + "?";
        }
        userData += "\n\n";


        mProfileTextView.setText(userData);

    }
}
