package com.example.gymtracker;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private String name;
    private int sets;
    private int reps;
    private float weight;

    public Exercise(String name, int sets, int reps, float weight) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    protected Exercise(Parcel in) {
        name = in.readString();
        sets = in.readInt();
        reps = in.readInt();
        weight = in.readFloat();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(sets);
        dest.writeInt(reps);
        dest.writeFloat(weight);
    }
}