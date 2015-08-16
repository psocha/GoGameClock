package com.psocha.goclock.time;

public class TimeHelper {

    public static int getHours(int totalSeconds) {
        return totalSeconds / 3600;
    }

    public static int getMinutes(int totalSeconds) {
        return (totalSeconds - 3600*getHours(totalSeconds)) / 60;
    }

    public static int getSeconds(int totalSeconds) {
        return totalSeconds - 3600*getHours(totalSeconds) - 60*getMinutes(totalSeconds);
    }

    public static String secondsToFormattedTime(int totalSeconds, int hourDigits,
                                                int minuteDigits, int secondDigits) {
        String formattedTime = "";
        int baseHours = getHours(totalSeconds);
        int baseMinutes = getMinutes(totalSeconds);
        int baseSeconds = getSeconds(totalSeconds);

        if (baseHours == 0) {
            if (hourDigits == 1) formattedTime += "0:";
            else if (hourDigits == 2) formattedTime += "00:";
        } else if (baseHours > 0 && baseHours < 10) {
            if (hourDigits <= 1) formattedTime += Integer.toString(baseHours) + ":";
            else if (hourDigits == 2) formattedTime += String.format("%02d", baseHours) + ":";
        } else {
            formattedTime += Integer.toString(baseHours) + ":";
        }

        if (baseMinutes == 0) {
            if (baseHours > 0) formattedTime += "00:";
            else if (minuteDigits == 1) formattedTime += "0:";
            else if (minuteDigits == 2) formattedTime += "00:";
        } else if (baseMinutes > 0 && baseMinutes < 10) {
            if (baseHours > 0) formattedTime += String.format("%02d", baseMinutes) + ":";
            else if (minuteDigits <= 1) formattedTime += Integer.toString(baseMinutes) + ":";
            else if(minuteDigits == 2) formattedTime += String.format("%02d", baseMinutes) + ":";
        } else {
            formattedTime += Integer.toString(baseMinutes) + ":";
        }

        if (baseSeconds == 0) {
            if (baseHours > 0 || baseMinutes > 0) formattedTime += "00";
            else if (secondDigits <= 1) formattedTime += "0";
            else if (secondDigits == 2) formattedTime += "00";
        } else if (baseSeconds > 0 && baseSeconds < 10) {
            if (baseHours > 0 || baseMinutes > 0) formattedTime += String.format("%02d", baseSeconds);
            else if (secondDigits <= 1) formattedTime += Integer.toString(baseSeconds);
            else if(secondDigits == 2) formattedTime += String.format("%02d", baseSeconds);
        } else {
            formattedTime += Integer.toString(baseSeconds);
        }

        return formattedTime;
    }
}
