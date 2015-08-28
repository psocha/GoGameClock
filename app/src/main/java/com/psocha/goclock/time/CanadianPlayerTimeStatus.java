package com.psocha.goclock.time;

import com.psocha.goclock.GoClockApp;
import com.psocha.goclock.R;

public class CanadianPlayerTimeStatus implements PlayerTimeStatus {

    // All times in seconds.
    private int mainTime;
    private int periodTime;
    private int stonesLeft;

    private int originalMainTime;
    private int originalPeriodLength;
    private int originalStonesPerPeriod;

    public CanadianPlayerTimeStatus(int main, int periodDuration, int stones) {
        originalMainTime = main;
        originalPeriodLength = periodDuration;
        originalStonesPerPeriod = stones;
        mainTime = originalMainTime;
        periodTime = originalPeriodLength;
        stonesLeft = originalStonesPerPeriod;
    }

    // Decrement remaining stones. Possibly start a new period.
    public void registerMove() {
        if (!isInMainTime()) {
            stonesLeft--;
            if (stonesLeft == 0) {
                periodTime = originalPeriodLength;
                stonesLeft = originalStonesPerPeriod;
            }
        }
    }

    public void decrementSecond() {
        if (mainTime > 1) {
            mainTime--;
        } else if (mainTime == 1) {
            mainTime = 0;
            stonesLeft = originalStonesPerPeriod;
            periodTime = originalPeriodLength;
        } else if (periodTime > 0) {
            periodTime--;
        }
    }

    public void reset() {
        mainTime = originalMainTime;
        stonesLeft = originalStonesPerPeriod;
        periodTime = originalPeriodLength;
    }

    public boolean isTimedOut() {
        return mainTime <= 0 && periodTime <= 0;
    }

    public String clockTime() {
        if (isInMainTime() || isTimedOut()) {
            return TimeHelper.secondsToFormattedTime(mainTime, 0, 2, 2);
        } else {
            return TimeHelper.secondsToFormattedTime(periodTime, 0, 2, 2);
        }
    }

    public String timeExplanation() {
        if (isInMainTime()) {
            return GoClockApp.getContext().getResources().getString(R.string.main_time);
        } else if (isTimedOut()) {
            return GoClockApp.getContext().getResources().getString(R.string.out_of_time);
        } else {
            return GoClockApp.getContext().getResources().getString(R.string.stones) + " " +
                    Integer.toString(stonesLeft);
        }
    }

    public String timeDescription() {
        return TimeHelper.secondsToFormattedTime(originalMainTime, 0, 1, 2) + " + " +
                TimeHelper.secondsToFormattedTime(originalPeriodLength, 0, 1, 2) + "/" +
                Integer.toString(originalStonesPerPeriod);
    }

    private boolean isInMainTime() {
        return mainTime > 0;
    }

    public int getMainTime() { return mainTime; }
    public void setMainTime(int t) { mainTime = Math.min(originalMainTime, t); }

    public int getPeriodTime() { return periodTime; }
    public void setPeriodTime(int t) { periodTime = Math.min(originalPeriodLength, t); }

    public int getStonesLeft() { return stonesLeft; }
    public void setStonesLeft(int t) { stonesLeft = Math.min(originalStonesPerPeriod, t); }

    public int getOriginalMainTime() { return originalMainTime; }
    public int getOriginalPeriodLength() { return originalPeriodLength; }
    public int getOriginalStonesPerPeriod() { return originalStonesPerPeriod; }
}