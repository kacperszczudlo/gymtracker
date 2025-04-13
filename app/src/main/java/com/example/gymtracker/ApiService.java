package com.example.gymtracker;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/api/users/register")
    Call<User> registerUser(@Body User user);

    @GET("/api/users/by-email")
    Call<User> getUserByEmail(@Query("email") String email);
}
