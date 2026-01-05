package com.netflixblocker.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    
    private static final String PREFS_NAME = "NetflixBlockerPrefs";
    private CheckBox checkBlockEnabled, checkShowToast, checkLaunchApp;
    private EditText editPackageName;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // --- UI Setup for 10-foot Experience ---
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(80, 60, 80, 60); // padding for TV edges (Overscan)
        
        // Title (Large for distance)
        TextView title = new TextView(this);
        title.setText("Netflix Button Blocker");
        title.setTextSize(32);
        title.setTextColor(Color.WHITE);
        title.setPadding(0, 0, 0, 40);
        layout.addView(title);

        // Checkboxes with focus styling
        checkBlockEnabled = createTvCheckBox("Enable Blocking", "block_enabled", true);
        checkShowToast = createTvCheckBox("Show Block Notification", "show_toast", true);
        checkLaunchApp = createTvCheckBox("Launch Alternative App", "launch_app", false);
        
        layout.addView(checkBlockEnabled);
        layout.addView(checkShowToast);
        layout.addView(checkLaunchApp);

        // Package Input
        TextView label = new TextView(this);
        label.setText("\nAlternative App Package Name:");
        label.setTextColor(Color.LTGRAY);
        layout.addView(label);

        editPackageName = new EditText(this);
        editPackageName.setHint("com.google.android.youtube.tv");
        editPackageName.setText(prefs.getString("launch_package", ""));
        editPackageName.setFocusable(true);
        editPackageName.setBackgroundResource(android.R.drawable.edit_text);
        layout.addView(editPackageName);

        // Save Button (The primary action)
        Button btnSave = new Button(this);
        btnSave.setText("Save & Apply");
        btnSave.setFocusable(true);
        btnSave.requestFocus(); // Set initial focus here for D-pad
        btnSave.setOnClickListener(v -> saveSettings());
        
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 50, 0, 20);
        layout.addView(btnSave, btnParams);

        // Instructions (Updated for Accessibility Service)
        TextView footer = new TextView(this);
        footer.setText("\nIMPORTANT:\n1. Click Save.\n2. Go to Shield Settings > Device Preferences.\n" +
                "3. Accessibility > Netflix Button Blocker > Enable.\n\n" +
                "Common Apps:\nYouTube: com.google.android.youtube.tv\nPlex: com.plexapp.android");
        footer.setTextSize(14);
        footer.setTextColor(Color.GRAY);
        layout.addView(footer);

        scrollView.addView(layout);
        setContentView(scrollView);
    }

    private CheckBox createTvCheckBox(String text, String key, boolean defaultValue) {
        CheckBox cb = new CheckBox(this);
        cb.setText(text);
        cb.setTextSize(18);
        cb.setTextColor(Color.WHITE);
        cb.setChecked(prefs.getBoolean(key, defaultValue));
        cb.setFocusable(true);
        cb.setPadding(20, 20, 20, 20);
        // Standard Android TV background gives a highlight when D-pad is on it
        cb.setBackgroundResource(android.R.drawable.list_selector_background);
        return cb;
    }

    private void saveSettings() {
        prefs.edit()
            .putBoolean("block_enabled", checkBlockEnabled.isChecked())
            .putBoolean("show_toast", checkShowToast.isChecked())
            .putBoolean("launch_app", checkLaunchApp.isChecked())
            .putString("launch_package", editPackageName.getText().toString().trim())
            .apply();
        
        Toast.makeText(this, "Settings Saved! Ensure Accessibility is ON.", Toast.LENGTH_LONG).show();
    }
}