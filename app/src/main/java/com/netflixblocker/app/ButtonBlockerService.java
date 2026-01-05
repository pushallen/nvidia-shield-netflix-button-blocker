package com.netflixblocker.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class ButtonBlockerService extends AccessibilityService {

    private static final String TAG = "ShieldBlocker";
    private static final String PREFS_NAME = "NetflixBlockerPrefs";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not needed for key interception
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "Service Interrupted");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        // 1. Fetch current user settings
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean("block_enabled", true);

        // If the user turned off blocking in the app, pass all keys through
        if (!isEnabled) {
            return super.onKeyEvent(event);
        }

        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        int action = event.getAction();

        // 2. Identify the Netflix Button
        // Based on getevent: scanCode 440 (0x1b8) is the target
        if (scanCode == 440 || keyCode == 191 || keyCode == KeyEvent.KEYCODE_BUTTON_12) {
            
            // Only act on the initial "Down" press to avoid double-triggering on "Up"
            if (action == KeyEvent.ACTION_DOWN) {
                Log.i(TAG, ">>> NETFLIX BUTTON INTERCEPTED <<<");

                // 3. Optional Toast Notification
                if (prefs.getBoolean("show_toast", true)) {
                    showToast("Netflix Button Blocked");
                }

                // 4. Launch Alternative App if configured
                if (prefs.getBoolean("launch_app", false)) {
                    String targetPackage = prefs.getString("launch_package", "");
                    if (targetPackage != null && !targetPackage.isEmpty()) {
                        launchApp(targetPackage);
                    }
                }
            }
            
            // 5. CONSUME THE EVENT
            // Returning true prevents the system from launching the Netflix app
            return true;
        }

        return super.onKeyEvent(event);
    }

    private void launchApp(String packageName) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Log.e(TAG, "Could not find package: " + packageName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error launching app", e);
        }
    }

    private void showToast(final String message) {
        // Services run on the main thread, but using a Handler ensures 
        // the Toast is displayed correctly in the UI context
        new Handler(Looper.getMainLooper()).post(() -> 
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show()
        );
    }
}