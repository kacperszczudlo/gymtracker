package api.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<UserProfileResponse> login(@Body LoginRequest request);

    @PUT("auth/users/{id}")
    Call<Void> updateUserProfile(@Path("id") int userId, @Body UpdateUserProfileRequest request);




}