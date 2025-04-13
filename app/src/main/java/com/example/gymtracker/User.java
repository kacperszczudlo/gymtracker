package com.example.gymtracker;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("email")
    public String email;

    @SerializedName("passwordHash")
    public String passwordHash;

    @SerializedName("gender")
    public String gender;

    @SerializedName("height")
    public int height;
}
