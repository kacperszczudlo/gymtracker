package com.example.gymtracker;

import java.util.List;

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

    // Nowa metoda do zapisu treningu dla użytkownika:
    @POST("/api/users/{userId}/workouts")
    Call<WorkoutDto> saveWorkout(@Path("userId") int userId, @Body WorkoutDto workoutDto);

    // Metoda pobierająca treningi dla użytkownika z opcjonalnym filtrem dnia
    @GET("/api/users/{userId}/workouts")
    Call<List<WorkoutDto>> getWorkoutsForUser(@Path("userId") int userId, @Query("day") String day);
}
