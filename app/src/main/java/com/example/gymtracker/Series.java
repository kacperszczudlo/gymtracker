package com.example.gymtracker;

import android.os.Parcel;
import android.os.Parcelable;

public class Series implements Parcelable {
    private int reps;
    private float weight;

    public Series(int reps, float weight) {
        this.reps = reps;
        this.weight = weight;
    }

    protected Series(Parcel in) {
        reps = in.readInt();
        weight = in.readFloat();
    }

    public static final Creator<Series> CREATOR = new Creator<Series>() {
        @Override
        public Series createFromParcel(Parcel in) {
            return new Series(in);
        }

        @Override
        public Series[] newArray(int size) {
            return new Series[size];
        }
    };
// t
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
        dest.writeInt(reps);
        dest.writeFloat(weight);
    }
}