package org.o7planning.hale_2.retrofit;


import com.google.gson.Gson;

import org.o7planning.hale_2.retrofit.Retrofit_Models.UserRequest;
import org.o7planning.hale_2.retrofit.Retrofit_Models.User;

import retrofit2.Call;

public class ServerApi {

    final private static String API_TOKEN = "abcd";
    private static Gson gson = new Gson();

    public static Call<User> signupUser(User userRequest) {
        return ServerAPIBuilder.getApiService().signupUser(userRequest.getEmail(), userRequest.getPassword(), API_TOKEN);
    }

    public static Call<User> signupUser2(User userRequest) {
        return ServerAPIBuilder.getApiService().signupUser2(userRequest.getEmail(), gson.toJson(userRequest), userRequest.getPassword(), gson.toJson(userRequest), API_TOKEN);
    }

    public static Call<User> loginUser(User userRequest) {
        return ServerAPIBuilder.getApiService().loginUser(userRequest.getEmail(), userRequest.getPassword(), API_TOKEN);
    }


    public static Call<User> updateUser(User userRequest) {
        return ServerAPIBuilder.getApiService().updateUser(userRequest.getEmail(),
                userRequest.getPassword(),
                userRequest.getAge(),
                userRequest.getHeight(),
                userRequest.getWeight(),
                userRequest.getGender(),
                API_TOKEN);
    }

}
