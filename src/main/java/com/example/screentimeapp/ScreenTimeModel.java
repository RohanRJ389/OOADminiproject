// ScreenTimeModel.java
package com.example.screentimeapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ScreenTimeModel extends Observable {

    private static ScreenTimeModel instance;
    private int totalScreenTime;
    private List<String> appNames;
    private List<Integer> appUsages;

    public ScreenTimeModel() {
        appNames = new ArrayList<>();
        appUsages = new ArrayList<>();
    }

    public static synchronized ScreenTimeModel getInstance() {
        if (instance == null) {
            instance = new ScreenTimeModel();
        }
        return instance;
    }

    public int getTotalScreenTime() {
        return totalScreenTime;
    }

    public void setTotalScreenTime(int totalScreenTime) {
        this.totalScreenTime = totalScreenTime;
        setChanged();
        notifyObservers();
    }

    public List<String> getAppNames() {
        return appNames;
    }

    public List<Integer> getAppUsages() {
        return appUsages;
    }

    public void updateScreenTimeData(List<String> appNames, List<Integer> appUsages) {
        this.appNames = appNames;
        this.appUsages = appUsages;
        setChanged();
        notifyObservers();
    }
}
