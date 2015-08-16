package com.psocha.goclock;

import android.content.Context;
import android.content.SharedPreferences;

import com.psocha.goclock.time.ByoyomiPlayerTimeStatus;
import com.psocha.goclock.time.CanadianPlayerTimeStatus;
import com.psocha.goclock.time.GameStatus;
import com.psocha.goclock.time.PlayerTimeStatus;
import com.psocha.goclock.time.SuddenDeathPlayerTimeStatus;

public class GameMemory {

    static final String TIME_SETTINGS = "time_settings";
    static final String BLACK = "black";
    static final String WHITE = "white";
    static final String TYPE = "type";

    static final String IS_TURN_BLACK = "is_black";

    static final String ORIG = "original";
    static final String PRES = "present";
    static final String MAIN = "main";
    static final String PARAM2 = "param2";
    static final String PARAM3 = "param3";

    static final int SUDDEN_DEATH = 0;
    static final int BYOYOMI = 1;
    static final int CANADIAN = 2;

    public static void saveCurrentTurnStatus(GameStatus.CURRENT_TURN turn) {
        SharedPreferences settings = GoClockApp.getContext().getSharedPreferences(
                TIME_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (turn == GameStatus.CURRENT_TURN.CURRENT_TURN_BLACK) {
            editor.putBoolean(IS_TURN_BLACK, true);
        } else {
            editor.putBoolean(IS_TURN_BLACK, false);
        }
        editor.commit();
    }

    public static void saveBlackGameState(PlayerTimeStatus status) {
        saveGameState(status, true);
    }

    public static void saveWhiteGameState(PlayerTimeStatus status) {
        saveGameState(status, false);
    }

    public static GameStatus.CURRENT_TURN loadCurrentTurnStatus() {
        SharedPreferences settings = GoClockApp.getContext().getSharedPreferences(
                TIME_SETTINGS, Context.MODE_PRIVATE);
        boolean is_black = settings.getBoolean(IS_TURN_BLACK, true);
        if (is_black) {
            return GameStatus.CURRENT_TURN.CURRENT_TURN_BLACK;
        } else {
            return GameStatus.CURRENT_TURN.CURRENT_TURN_WHITE;
        }
    }

    public static PlayerTimeStatus loadBlackGameState() {
        return loadGameState(true);
    }

    public static PlayerTimeStatus loadWhiteGameState() {
        return loadGameState(false);
    }

    private static void saveGameState(PlayerTimeStatus status, boolean is_black) {
        SharedPreferences settings = GoClockApp.getContext().getSharedPreferences(
                TIME_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String colour = is_black ? BLACK : WHITE;

        if (status.getClass() == SuddenDeathPlayerTimeStatus.class) {
            SuddenDeathPlayerTimeStatus suddenDeathStatus = (SuddenDeathPlayerTimeStatus)status;
            editor.putInt(colour + TYPE, SUDDEN_DEATH);
            editor.putInt(colour + ORIG + MAIN, suddenDeathStatus.getOriginalTime());
            editor.putInt(colour + PRES + MAIN, suddenDeathStatus.getTime());
        } else if (status.getClass() == ByoyomiPlayerTimeStatus.class) {
            ByoyomiPlayerTimeStatus byoyomiStatus = (ByoyomiPlayerTimeStatus)status;
            editor.putInt(colour + TYPE, BYOYOMI);
            editor.putInt(colour + ORIG + MAIN, byoyomiStatus.getOriginalMainTime());
            editor.putInt(colour + ORIG + PARAM2, byoyomiStatus.getOriginalNumPeriods());
            editor.putInt(colour + ORIG + PARAM3, byoyomiStatus.getOriginalPeriodTime());
            editor.putInt(colour + PRES + MAIN, byoyomiStatus.getMainTime());
            editor.putInt(colour + PRES + PARAM2, byoyomiStatus.getNumPeriods());
            editor.putInt(colour + PRES + PARAM3, byoyomiStatus.getPeriodTime());
        } else if (status.getClass() == CanadianPlayerTimeStatus.class) {
            CanadianPlayerTimeStatus canadianStatus = (CanadianPlayerTimeStatus)status;
            editor.putInt(colour + TYPE, CANADIAN);
            editor.putInt(colour + ORIG + MAIN, canadianStatus.getOriginalMainTime());
            editor.putInt(colour + ORIG + PARAM2, canadianStatus.getOriginalPeriodLength());
            editor.putInt(colour + ORIG + PARAM3, canadianStatus.getOriginalStonesPerPeriod());
            editor.putInt(colour + PRES + MAIN, canadianStatus.getMainTime());
            editor.putInt(colour + PRES + PARAM2, canadianStatus.getPeriodTime());
            editor.putInt(colour + PRES + PARAM3, canadianStatus.getStonesLeft());
        }

        editor.commit();
    }

    // If nothing is present, default is Sudden Death 45:00 (will be set at first load).
    public static PlayerTimeStatus loadGameState(boolean is_black) {
        SharedPreferences settings = GoClockApp.getContext().getSharedPreferences(
                TIME_SETTINGS, Context.MODE_PRIVATE);
        String colour = is_black ? BLACK : WHITE;

        int timeType = settings.getInt(colour + TYPE, 0);
        switch (timeType) {
            case BYOYOMI:
                int originalByoyomiMain = settings.getInt(colour + ORIG + MAIN, 25*60);
                int originalNumPeriods = settings.getInt(colour + ORIG + PARAM2, 5);
                int originalPeriodTime = settings.getInt(colour + ORIG + PARAM3, 30);
                ByoyomiPlayerTimeStatus byoyomiPlayerStatus = new ByoyomiPlayerTimeStatus(
                        originalByoyomiMain, originalNumPeriods, originalPeriodTime);
                int currentByoyomiMain = settings.getInt(colour + PRES + MAIN, 25*60);
                int currentByoyomiNumPeriods = settings.getInt(colour + PRES + PARAM2, 5);
                int currentByoyomiPeriodTime = settings.getInt(colour + PRES + PARAM3, 30);
                byoyomiPlayerStatus.setMainTime(currentByoyomiMain);
                byoyomiPlayerStatus.setNumPeriods(currentByoyomiNumPeriods);
                byoyomiPlayerStatus.setPeriodTime(currentByoyomiPeriodTime);
                return byoyomiPlayerStatus;
            case CANADIAN:
                int originalCanadianMain = settings.getInt(colour + ORIG + MAIN, 60);
                int originalCanadianPeriodTime = settings.getInt(colour + ORIG + PARAM2, 10*60);
                int originalStonesPerPeriod = settings.getInt(colour + ORIG + PARAM3, 25);
                CanadianPlayerTimeStatus canadianTimeStatus = new CanadianPlayerTimeStatus(
                        originalCanadianMain, originalCanadianPeriodTime, originalStonesPerPeriod);
                int currentCanadianMain = settings.getInt(colour + PRES + MAIN, 60);
                int currentCanadianPeriodTime = settings.getInt(colour + PRES + PARAM2, 10*60);
                int currentStonesPerPeriod = settings.getInt(colour + PRES + PARAM3, 25);
                canadianTimeStatus.setMainTime(currentCanadianMain);
                canadianTimeStatus.setPeriodTime(currentCanadianPeriodTime);
                canadianTimeStatus.setStonesLeft(currentStonesPerPeriod);
                return canadianTimeStatus;
            case SUDDEN_DEATH:
                // fall through.
            default:
                int originalSuddenDeath = settings.getInt(colour + ORIG + MAIN, 45*60);
                SuddenDeathPlayerTimeStatus suddenDeathPlayerStatus =
                        new SuddenDeathPlayerTimeStatus(originalSuddenDeath);
                int suddenDeathTimeRemaining = settings.getInt(colour + PRES + MAIN, 45*60);
                suddenDeathPlayerStatus.setTime(suddenDeathTimeRemaining);
                return suddenDeathPlayerStatus;
        }
    }
}