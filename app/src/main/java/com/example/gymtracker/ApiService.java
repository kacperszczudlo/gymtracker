package com.example.gymtracker;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/api/users/register")
    Call<User> registerUser(@Body User user);

    @PUT("/api/users/{userId}/bodystat")
    Call<BodyStatDto> updateBodyStat(@Path("userId") int userId, @Body BodyStatDto bodyStatDto);


    @GET("/api/users/by-email")
    Call<User> getUserByEmail(@Query("email") String email);
}
