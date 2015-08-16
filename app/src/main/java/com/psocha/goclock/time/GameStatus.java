package com.psocha.goclock.time;

public class GameStatus {

    public GameStatus() {
        gameState = GAME_STATE.GAME_STATE_INACTIVE;
        currentTurn = CURRENT_TURN.CURRENT_TURN_BLACK;
    }

    public enum GAME_STATE {
        GAME_STATE_INACTIVE,
        GAME_STATE_RUNNING,
        GAME_STATE_TIMEOUT
    };

    public enum CURRENT_TURN {
        CURRENT_TURN_BLACK,
        CURRENT_TURN_WHITE
    };

    private PlayerTimeStatus blackTime;
    private PlayerTimeStatus whiteTime;

    public GAME_STATE gameState;
    public CURRENT_TURN currentTurn;

    public void toggleTurn() {
        if (currentTurn == CURRENT_TURN.CURRENT_TURN_BLACK) {
            currentTurn = CURRENT_TURN.CURRENT_TURN_WHITE;
        } else {
            currentTurn = CURRENT_TURN.CURRENT_TURN_BLACK;
        }
    }

    public boolean isInactive() {
        return gameState != GAME_STATE.GAME_STATE_RUNNING;
    }

    public PlayerTimeStatus getBlackTime() {
        return blackTime;
    }

    public PlayerTimeStatus getWhiteTime() {
        return whiteTime;
    }

    public void setBlackPlayerTime(PlayerTimeStatus timeStatus) {
        blackTime = timeStatus;
    }

    public void setWhitePlayerTime(PlayerTimeStatus timeStatus) {
        whiteTime = timeStatus;
    }
}