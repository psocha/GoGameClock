package com.psocha.goclock.time;

import com.psocha.goclock.GoClockApp;
import com.psocha.goclock.R;

public class ByoyomiPlayerTimeStatus implements PlayerTimeStatus {

    // All times in seconds.
    private int mainTime;
    private int numPeriods;
    private int periodTime;

    private int originalMainTime;
    private int originalNumPeriods;
    private int originalPeriodTime;

    public ByoyomiPlayerTimeStatus(int main, int periods, int timePeriod) {
        originalMainTime = main;
        originalNumPeriods = periods;
        originalPeriodTime = timePeriod;
        mainTime = originalMainTime;
        numPeriods = originalNumPeriods;
        periodTime = originalPeriodTime;
    }

    // Reset to the start of the period.
    public void registerMove() {
        if (!isInMainTime()) {
            periodTime = originalPeriodTime;
        }
    }

    public void decrementSecond() {
        if (mainTime > 1) {
            mainTime--;
        } else if (mainTime == 1) {
            mainTime = 0;
            periodTime = originalPeriodTime;
        } else {
            if (periodTime > 1) {
                periodTime--;
            } else if (periodTime == 1) {
                periodTime = originalPeriodTime;
                numPeriods--;
            }
        }
    }

    public void reset() {
        mainTime = originalMainTime;
        periodTime = originalPeriodTime;
        numPeriods = originalNumPeriods;
    }

    public boolean isTimedOut() {
        return (mainTime <= 0 && numPeriods <= 0);
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
            return GoClockApp.getContext().getResources().getString(R.string.periods) + " " +
                    Integer.toString(numPeriods);
        }
    }

    public String timeDescription() {
        return TimeHelper.secondsToFormattedTime(originalMainTime, 0, 1, 2) + " + " +
                Integer.toString(originalNumPeriods) + "*" +
                TimeHelper.secondsToFormattedTime(originalPeriodTime, 0, 1, 2);
    }

    private boolean isInMainTime() {
        return mainTime > 0;
    }

    public int getMainTime() { return mainTime; }
    public void setMainTime(int t) { mainTime = Math.min(originalMainTime, t); }

    public int getNumPeriods() { return numPeriods; }
    public void setNumPeriods(int t) { numPeriods = Math.min(originalNumPeriods, t); }

    public int getPeriodTime() { return periodTime; }
    public void setPeriodTime(int t) { periodTime = Math.min(originalPeriodTime, t); }

    public int getOriginalMainTime() { return originalMainTime; }
    public int getOriginalNumPeriods() { return originalNumPeriods; }
    public int getOriginalPeriodTime() { return originalPeriodTime; }
}
