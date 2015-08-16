package com.psocha.goclock.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.psocha.goclock.R;


public class OptionsActivity extends Activity {

    private CheckBox checkBoxHaptic;
    private RadioButton radioDark, radioLight;

    public static final String OPTIONS_SETTINGS = "options";

    static final String HAPTIC_FEEDBACK = "haptic_feedback";
    static final String IS_DARK_BACKGROUND = "is_dark_background";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        SharedPreferences options = getSharedPreferences(OPTIONS_SETTINGS, MODE_PRIVATE);

        boolean isHaptic = options.getBoolean(HAPTIC_FEEDBACK, true);
        checkBoxHaptic = (CheckBox)this.findViewById(R.id.checkboxHaptic);
        checkBoxHaptic.setChecked(isHaptic);

        radioDark = (RadioButton)findViewById(R.id.radioDark);
        radioLight = (RadioButton)findViewById(R.id.radioLight);
        boolean isDark = options.getBoolean(IS_DARK_BACKGROUND, false);
        if (isDark) {
            radioDark.setChecked(true);
        } else {
            radioLight.setChecked(true);
        }
    }

    public void saveButtonPressed(View view) {
        boolean haptic = checkBoxHaptic.isChecked();
        setPreference(HAPTIC_FEEDBACK, haptic);

        boolean isDark = radioDark.isChecked();
        setPreference(IS_DARK_BACKGROUND, isDark);

        this.finish();
    }

    private void setPreference(String prefKey, boolean value) {
        SharedPreferences settings = getSharedPreferences(OPTIONS_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(prefKey, value);
        editor.commit();
    }

}
