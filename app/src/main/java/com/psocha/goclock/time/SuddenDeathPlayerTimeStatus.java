package com.psocha.goclock.time;

import com.psocha.goclock.GoClockApp;
import com.psocha.goclock.R;

public class SuddenDeathPlayerTimeStatus implements PlayerTimeStatus {

    private int time;
    private int originalTime;

    public SuddenDeathPlayerTimeStatus(int totalSeconds) {
        originalTime = totalSeconds;
        time = originalTime;
    }

    public void registerMove() {
        return;
    }

    public void decrementSecond() {
        time--;
    }

    public void reset() {
        time = originalTime;
    }

    public boolean isTimedOut() {
        return time <= 0;
    }

    public String clockTime() {
        return TimeHelper.secondsToFormattedTime(time, 0, 2, 2);
    }

    public String timeExplanation() {
        if (isTimedOut()) {
            return GoClockApp.getContext().getResources().getString(R.string.out_of_time);
        } else {
            return GoClockApp.getContext().getResources().getString(R.string.sudden_death);
        }
    }

    public String timeDescription() {
        return TimeHelper.secondsToFormattedTime(originalTime, 0, 1, 2);
    }

    public int getOriginalTime() { return originalTime; }

    public int getTime() { return time; }
    public void setTime(int t) { time = Math.min(originalTime, t); }
}
