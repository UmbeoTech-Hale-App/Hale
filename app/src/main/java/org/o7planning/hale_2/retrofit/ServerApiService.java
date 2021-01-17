package org.o7planning.hale_2.retrofit;

import org.o7planning.hale_2.retrofit.Retrofit_Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApiService {

    @POST("/users/signup")
    Call<User> signupUser(@Query("email") String email, @Query("password") String password, @Header("authtoken") String authtoken);

    @POST("/users/signup")
    Call<User> signupUser2(@Query("email") String email, @Query("password") String password,@Query("user") String user, @Body String user2, @Header("authtoken") String authtoken);

    @GET("/users/login")
    Call<User> loginUser(@Query("email") String email, @Query("password") String password, @Header("authtoken") String authtoken);

    @POST("/users/update")
    Call<User> updateUser(@Query("email") String email, @Query("password") String password,
                          @Query("age") int age,
                          @Query("height") int height,
                          @Query("weight") int weight,
                          @Query("gender") String gender,
                          @Header("authtoken") String authtoken);

}
