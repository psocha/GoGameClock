package com.psocha.goclock.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.psocha.goclock.GameMemory;
import com.psocha.goclock.R;
import com.psocha.goclock.time.ByoyomiPlayerTimeStatus;
import com.psocha.goclock.time.CanadianPlayerTimeStatus;
import com.psocha.goclock.time.GameStatus;
import com.psocha.goclock.time.PlayerTimeStatus;
import com.psocha.goclock.time.SuddenDeathPlayerTimeStatus;
import com.psocha.goclock.time.TimeHelper;


public class ConfigurationActivity extends Activity {

    private final int BLACK = 0;
    private final int WHITE = 1;

    private RelativeLayout blackLayout, whiteLayout;

    private TextView blackLabelSingle, whiteLabelSingle;

    private RadioButton suddenDeathRadio, byoyomiRadio, canadianRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        populateElements();
        setNumPickerProperties(blackLayout);
        setNumPickerProperties(whiteLayout);

        loadCurrentSettings(BLACK);
        loadCurrentSettings(WHITE);
    }

    private void populateElements() {
        blackLayout = (RelativeLayout)findViewById(R.id.blackTimeLayout);
        whiteLayout = (RelativeLayout)findViewById(R.id.whiteTimeLayout);

        blackLabelSingle = (TextView)findViewById(R.id.blackLabelSingle);
        whiteLabelSingle = (TextView)findViewById(R.id.whiteLabelSingle);

        suddenDeathRadio = (RadioButton)findViewById(R.id.radioSuddenDeath);
        byoyomiRadio = (RadioButton)findViewById(R.id.radioByoyomi);
        canadianRadio = (RadioButton)findViewById(R.id.radioCanadian);
    }

    private void setNumPickerProperties(RelativeLayout timeLayout) {
        for (int i = 0; i < timeLayout.getChildCount(); ++i) {
            View view = timeLayout.getChildAt(i);
            if (view instanceof NumberPicker) {
                NumberPicker picker = (NumberPicker)view;
                picker.setMinValue(0);
                picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                picker.setOnLongPressUpdateInterval(100);
                String tag = picker.getTag().toString();
                if (tag.equals("mainTimeHours") || tag.equals("secondaryTimeHours") || tag.equals("singleInput")) {
                    picker.setMaxValue(99);
                } else {
                    picker.setMaxValue(59);
                    picker.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int i) {
                            return String.format("%02d", i);
                        }
                    });
                }
                picker.setValue(0);
            }
        }
    }

    private void loadCurrentSettings(int color) {
        RelativeLayout topLayout;
        if (color == BLACK) topLayout = blackLayout;
        else if (color == WHITE) topLayout = whiteLayout;
        else return;

        PlayerTimeStatus timeStatus;
        if (color == BLACK) {
            timeStatus = GameMemory.loadBlackGameState();
        } else {
            timeStatus = GameMemory.loadWhiteGameState();
        }

        int mainHours = 0, mainMinutes = 0, mainSeconds = 0, singleInput = 0,
                secondaryHours = 0, secondaryMinutes = 0, secondarySeconds = 0;

        if (timeStatus.getClass() == SuddenDeathPlayerTimeStatus.class) {
            SuddenDeathPlayerTimeStatus status = (SuddenDeathPlayerTimeStatus)timeStatus;

            int mainTimeSeconds = status.getOriginalTime();
            mainHours = TimeHelper.getHours(mainTimeSeconds);
            mainMinutes = TimeHelper.getMinutes(mainTimeSeconds);
            mainSeconds = TimeHelper.getSeconds(mainTimeSeconds);

            suddenDeathRadio.setChecked(true);
            radioButtonPressed(suddenDeathRadio);
        } else if (timeStatus.getClass() == ByoyomiPlayerTimeStatus.class) {
            ByoyomiPlayerTimeStatus status = (ByoyomiPlayerTimeStatus)timeStatus;

            int mainTimeSeconds = status.getOriginalMainTime();
            mainHours = TimeHelper.getHours(mainTimeSeconds);
            mainMinutes = TimeHelper.getMinutes(mainTimeSeconds);
            mainSeconds = TimeHelper.getSeconds(mainTimeSeconds);

            singleInput = status.getOriginalNumPeriods();

            int periodSeconds = status.getOriginalPeriodTime();
            secondaryHours = TimeHelper.getHours(periodSeconds);
            secondaryMinutes = TimeHelper.getMinutes(periodSeconds);
            secondarySeconds = TimeHelper.getSeconds(periodSeconds);

            byoyomiRadio.setChecked(true);
            radioButtonPressed(byoyomiRadio);
        } else if (timeStatus.getClass() == CanadianPlayerTimeStatus.class) {
            CanadianPlayerTimeStatus status = (CanadianPlayerTimeStatus)timeStatus;

            int mainTimeSeconds = status.getOriginalMainTime();
            mainHours = TimeHelper.getHours(mainTimeSeconds);
            mainMinutes = TimeHelper.getMinutes(mainTimeSeconds);
            mainSeconds = TimeHelper.getSeconds(mainTimeSeconds);

            singleInput = status.getOriginalStonesPerPeriod();

            int periodSeconds = status.getOriginalPeriodLength();
            secondaryHours = TimeHelper.getHours(periodSeconds);
            secondaryMinutes = TimeHelper.getMinutes(periodSeconds);
            secondarySeconds = TimeHelper.getSeconds(periodSeconds);

            canadianRadio.setChecked(true);
            radioButtonPressed(canadianRadio);
        }

        ((NumberPicker)(topLayout.findViewWithTag("mainTimeHours"))).setValue(mainHours);
        ((NumberPicker)(topLayout.findViewWithTag("mainTimeMinutes"))).setValue(mainMinutes);
        ((NumberPicker)(topLayout.findViewWithTag("mainTimeSeconds"))).setValue(mainSeconds);
        ((NumberPicker)(topLayout.findViewWithTag("singleInput"))).setValue(singleInput);
        ((NumberPicker)(topLayout.findViewWithTag("secondaryTimeHours"))).setValue(secondaryHours);
        ((NumberPicker)(topLayout.findViewWithTag("secondaryTimeMinutes"))).setValue(secondaryMinutes);
        ((NumberPicker)(topLayout.findViewWithTag("secondaryTimeSeconds"))).setValue(secondarySeconds);
    }

    public void radioButtonPressed(View view) {
        RadioButton radioButton = (RadioButton)view;
        int radioId = radioButton.getId();

        switch (radioId) {
            case R.id.radioSuddenDeath:
                toggleSecondaryVisibility(blackLayout, false);
                toggleSecondaryVisibility(whiteLayout, false);
                break;
            case R.id.radioByoyomi:
                toggleSecondaryVisibility(blackLayout, true);
                toggleSecondaryVisibility(whiteLayout, true);
                blackLabelSingle.setText(R.string.num_periods);
                whiteLabelSingle.setText(R.string.num_periods);
                break;
            case R.id.radioCanadian:
                toggleSecondaryVisibility(blackLayout, true);
                toggleSecondaryVisibility(whiteLayout, true);
                blackLabelSingle.setText(R.string.num_stones);
                whiteLabelSingle.setText(R.string.num_stones);
                break;
        }
    }

    private void toggleSecondaryVisibility(RelativeLayout topLayout, boolean show) {
        for (int i = 0; i < topLayout.getChildCount(); ++i) {
            View view = topLayout.getChildAt(i);
            if (view.getTag() == null) {
                continue;
            }
            String tag = view.getTag().toString();
            if (tag.equals("singleLabel") || tag.equals("singleInput") || tag.equals("secondaryLabel") ||
                    tag.equals("secondaryTimeHours") || tag.equals("secondaryTimeMinutes") ||
                    tag.equals("secondaryTimeSeconds")) {
                if (show) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    public void saveButtonPressed(View view) {
        saveConfig(BLACK);
        saveConfig(WHITE);
    }

    private void saveConfig(int color) {
        RelativeLayout topLayout;
        if (color == BLACK) topLayout = blackLayout;
        else if (color == WHITE) topLayout = whiteLayout;
        else return;

        int mainTime = ((NumberPicker)topLayout.findViewWithTag("mainTimeHours")).getValue() * 3600;
        mainTime += ((NumberPicker)topLayout.findViewWithTag("mainTimeMinutes")).getValue() * 60;
        mainTime += ((NumberPicker)topLayout.findViewWithTag("mainTimeSeconds")).getValue();

        int singleValue = ((NumberPicker)topLayout.findViewWithTag("singleInput")).getValue();

        int secondaryTime = ((NumberPicker)topLayout.findViewWithTag("secondaryTimeHours")).getValue() * 3600;
        secondaryTime += ((NumberPicker)topLayout.findViewWithTag("secondaryTimeMinutes")).getValue() * 60;
        secondaryTime += ((NumberPicker)topLayout.findViewWithTag("secondaryTimeSeconds")).getValue();

        PlayerTimeStatus status;
        if (suddenDeathRadio.isChecked()) {
            status = new SuddenDeathPlayerTimeStatus(mainTime);
        } else if (byoyomiRadio.isChecked()) {
            status = new ByoyomiPlayerTimeStatus(mainTime, singleValue, secondaryTime);
        } else {
            status = new CanadianPlayerTimeStatus(mainTime, secondaryTime, singleValue);
        }

        if (color == BLACK) GameMemory.saveBlackGameState(status);
        else GameMemory.saveWhiteGameState(status);

        GameMemory.saveCurrentTurnStatus(GameStatus.CURRENT_TURN.CURRENT_TURN_BLACK);

        this.finish();
    }
}
