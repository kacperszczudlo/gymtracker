package com.example.gymtracker;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Exercise implements Parcelable {
    private String name;
    private ArrayList<Series> seriesList;

    public Exercise(String name) {
        this.name = name;
        this.seriesList = new ArrayList<>();
    }

    public Exercise(String name, ArrayList<Series> seriesList) {
        this.name = name;
        this.seriesList = seriesList != null ? seriesList : new ArrayList<>();
    }

    protected Exercise(Parcel in) {
        name = in.readString();
        seriesList = new ArrayList<>();
        in.readList(seriesList, Series.class.getClassLoader());
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

    public ArrayList<Series> getSeriesList() {
        return seriesList;
    }

    public void addSeries(Series series) {
        seriesList.add(series);
    }

    public void removeSeries(int index) {
        if (index >= 0 && index < seriesList.size()) {
            seriesList.remove(index);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(seriesList);
    }
}