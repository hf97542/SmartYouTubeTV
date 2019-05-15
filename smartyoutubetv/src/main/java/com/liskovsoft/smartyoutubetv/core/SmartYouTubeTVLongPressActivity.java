package com.liskovsoft.smartyoutubetv.core;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.KeyEvent;
import com.liskovsoft.browser.addons.MainBrowserActivity;

public abstract class SmartYouTubeTVLongPressActivity extends MainBrowserActivity {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //Handle what you want on short press.
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            //Handle what you want in long press.
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
