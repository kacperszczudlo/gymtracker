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

    @SerializedName("id")
    private int id;  // <-- pole ID

    // Gettery i Settery (opcjonalne, ale często wygodne)

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
}

