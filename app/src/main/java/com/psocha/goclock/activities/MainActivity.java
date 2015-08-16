package com.psocha.goclock.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.psocha.goclock.GameMemory;
import com.psocha.goclock.R;
import com.psocha.goclock.time.GameStatus;
import com.psocha.goclock.time.PlayerTimeStatus;


public class MainActivity extends Activity {

    private GameStatus gameStatus;

    private Handler tickHandler = new Handler();
    private TickThread tickThread = new TickThread();

    private Button toggleButton, optionsButton, configurationButton, resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadGameStatus();

        toggleButton = (Button)findViewById(R.id.buttonToggle);
        optionsButton = (Button)findViewById(R.id.buttonOptions);
        configurationButton = (Button)findViewById(R.id.buttonConfigure);
        resetButton = (Button)findViewById(R.id.buttonReset);
        toggleControlButtons(true);

        loadBackground();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View whiteStone = this.findViewById(R.id.whiteStone);
        View blackStone = this.findViewById(R.id.blackStone);

        int whiteWidth = whiteStone.getWidth();
        int blackWidth = blackStone.getWidth();

        RelativeLayout.LayoutParams whiteLayout = new RelativeLayout.LayoutParams(whiteWidth, whiteWidth);
        RelativeLayout.LayoutParams blackLayout = new RelativeLayout.LayoutParams(blackWidth, blackWidth);

        whiteLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        blackLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        whiteStone.setLayoutParams(whiteLayout);
        blackStone.setLayoutParams(blackLayout);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveGameStatus();
        pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGameStatus();
        pause();
        loadBackground();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        saveGameStatus();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loadGameStatus();
    }

    public void optionsButtonPressed(View button) {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    public void configureButtonPressed(View button) {
        Intent intent = new Intent(this, ConfigurationActivity.class);
        startActivity(intent);
    }

    public void resetButtonPressed(View button) {
        reset();
    }

    public void toggleButtonClicked(View button) {
        if (isHapticFeedbackEnabled()) {
            toggleButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }

        switch (gameStatus.gameState) {
            case GAME_STATE_INACTIVE:
                toggleButton.setText(R.string.main_pause);
                gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_RUNNING;
                startCountdown();
                break;
            case GAME_STATE_RUNNING:
                toggleButton.setText(R.string.main_start);
                gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_INACTIVE;
                pause();
                break;
            case GAME_STATE_TIMEOUT:
                toggleButton.setText(R.string.main_start);
                gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_INACTIVE;
                reset();
                break;
        }
    }

    public void clockButtonPressed(View clockButton) {
        if (gameStatus.isInactive()) return;
        boolean is_black = clockButton.getId() == R.id.blackClockButton;
        if (is_black && gameStatus.currentTurn == GameStatus.CURRENT_TURN.CURRENT_TURN_WHITE ||
                !is_black && gameStatus.currentTurn == GameStatus.CURRENT_TURN.CURRENT_TURN_BLACK) {
            return;
        }
        if (isHapticFeedbackEnabled()) {
            clockButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        gameStatus.toggleTurn();

        if (is_black) {
            gameStatus.getBlackTime().registerMove();
        } else {
            gameStatus.getWhiteTime().registerMove();
        }

        refreshButtons();

        tickHandler.removeCallbacks(tickThread);
        tickHandler.postDelayed(tickThread, 1000);
    }

    private void reset() {
        tickHandler.removeCallbacks(tickThread);
        gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_INACTIVE;
        gameStatus.currentTurn = GameStatus.CURRENT_TURN.CURRENT_TURN_BLACK;

        gameStatus.getBlackTime().reset();
        gameStatus.getWhiteTime().reset();

        toggleControlButtons(true);

        refreshButtons();
    }

    private void pause() {
        gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_INACTIVE;
        tickHandler.removeCallbacks(tickThread);

        toggleButton.setText(R.string.main_start);
        toggleControlButtons(true);
    }

    private void startCountdown() {
        gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_RUNNING;
        tickHandler.removeCallbacks(tickThread);
        tickHandler.postDelayed(tickThread, 1000);

        toggleControlButtons(false);

        refreshButtons();
    }

    private class TickThread implements Runnable {
        public void run() {
            if (gameStatus.currentTurn == GameStatus.CURRENT_TURN.CURRENT_TURN_BLACK) {
                gameStatus.getBlackTime().decrementSecond();
            } else {
                gameStatus.getWhiteTime().decrementSecond();
            }
            refreshButtons();
            tickHandler.removeCallbacks(this);
            tickHandler.postDelayed(tickThread, 1000);
        }
    }

    private void loadGameStatus() {
        gameStatus = new GameStatus();

        PlayerTimeStatus blackTime = GameMemory.loadBlackGameState();
        PlayerTimeStatus whiteTime = GameMemory.loadWhiteGameState();
        gameStatus.setBlackPlayerTime(blackTime);
        gameStatus.setWhitePlayerTime(whiteTime);

        gameStatus.currentTurn = GameMemory.loadCurrentTurnStatus();

        refreshButtons();
    }

    private void saveGameStatus() {
        GameMemory.saveBlackGameState(gameStatus.getBlackTime());
        GameMemory.saveWhiteGameState(gameStatus.getWhiteTime());
        GameMemory.saveCurrentTurnStatus(gameStatus.currentTurn);
    }

    private void refreshButtons() {
        // Black.
        TextView blackTime = (TextView)findViewById(R.id.blackTime);
        blackTime.setText(gameStatus.getBlackTime().clockTime());
        if (blackTime.getText().length() > 5) {
            blackTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);
        } else {
            blackTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64);
        }

        TextView blackExplanation = (TextView)findViewById(R.id.blackDescription);
        blackExplanation.setText(gameStatus.getBlackTime().timeExplanation());

        TextView blackDescription = (TextView)findViewById(R.id.blackTimeSetting);
        blackDescription.setText(gameStatus.getBlackTime().timeDescription());

        // White.
        TextView whiteTime = (TextView)findViewById(R.id.whiteTime);
        whiteTime.setText(gameStatus.getWhiteTime().clockTime());
        if (whiteTime.getText().length() > 5) {
            whiteTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);
        } else {
            whiteTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64);
        }

        TextView whiteExplanation = (TextView)findViewById(R.id.whiteDescription);
        whiteExplanation.setText(gameStatus.getWhiteTime().timeExplanation());

        TextView whiteDescription = (TextView)findViewById(R.id.whiteTimeSetting);
        whiteDescription.setText(gameStatus.getWhiteTime().timeDescription());

        // Text colors.
        if (gameStatus.currentTurn == GameStatus.CURRENT_TURN.CURRENT_TURN_BLACK) {
            blackTime.setTextColor(getResources().getColor(R.color.active_yellow));
            whiteTime.setTextColor(getResources().getColor(R.color.primary_text_default_material_dark));
        } else {
            blackTime.setTextColor(getResources().getColor(R.color.primary_text_default_material_dark));
            whiteTime.setTextColor(getResources().getColor(R.color.active_yellow));
        }

        // Timeouts.
        LinearLayout blackDisplay = (LinearLayout)findViewById(R.id.blackDisplay);
        if (gameStatus.getBlackTime().isTimedOut()) {
            blackDisplay.setBackgroundColor(getResources().getColor(R.color.timeout));
            gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_TIMEOUT;
        } else {
            blackDisplay.setBackgroundColor(getResources().getColor(R.color.primary_dark_material_light));
        }
        LinearLayout whiteDisplay = (LinearLayout)findViewById(R.id.whiteDisplay);
        if (gameStatus.getWhiteTime().isTimedOut()) {
            whiteDisplay.setBackgroundColor(getResources().getColor(R.color.timeout));
            gameStatus.gameState = GameStatus.GAME_STATE.GAME_STATE_TIMEOUT;
        } else {
            whiteDisplay.setBackgroundColor(getResources().getColor(R.color.primary_dark_material_light));
        }

        if (gameStatus.gameState == GameStatus.GAME_STATE.GAME_STATE_TIMEOUT) {
            Button toggleButton = (Button)findViewById(R.id.buttonToggle);
            toggleButton.setText(R.string.menu_reset);
        }
    }

    private boolean isHapticFeedbackEnabled() {
        SharedPreferences settings = getSharedPreferences(OptionsActivity.OPTIONS_SETTINGS, 0);
        return settings.getBoolean(OptionsActivity.HAPTIC_FEEDBACK, true);
    }

    private void loadBackground() {
        SharedPreferences settings = getSharedPreferences(OptionsActivity.OPTIONS_SETTINGS, 0);
        boolean isDark = settings.getBoolean(OptionsActivity.IS_DARK_BACKGROUND, false);
        LinearLayout layoutContainer = (LinearLayout)findViewById(R.id.layoutContainer);
        if (isDark) {
            layoutContainer.setBackgroundColor(getResources().getColor(R.color.background_main));
        } else {
            layoutContainer.setBackgroundColor(getResources().getColor(R.color.background_main_light));
        }
    }

    private void toggleControlButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        optionsButton.setVisibility(visibility);
        configurationButton.setVisibility(visibility);
        resetButton.setVisibility(visibility);
    }
}