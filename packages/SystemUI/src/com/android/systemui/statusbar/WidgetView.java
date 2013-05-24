/*
 * Copyright 2011 AOKP
 * Copyright 2013 SlimRoms
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.systemui.R;
import com.android.systemui.WidgetSelectActivity;

public class WidgetView extends LinearLayout {

    private Context mContext;
    private Handler mHandler;
    public FrameLayout mPopupView;
    public WindowManager mWindowManager;
    int originalHeight = 0;
    LinearLayout mWidgetPanel;
    TextView mWidgetLabel;
    ViewPager mWidgetPager;
    WidgetPagerAdapter mAdapter;
    int widgetIds[];
    float mFirstMoveY;
    int mCurrentWidgetPage = 0;
    long mDowntime;
    boolean mMoving = false;
    boolean showing = false;
    int mCurrUiInvertedMode;

    final static String TAG = "Widget";

    public WidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCurrUiInvertedMode = mContext.getResources().getConfiguration().uiInvertedMode;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        mContext.registerReceiver(new WidgetReceiver(), filter);
        mHandler = new Handler();
        SettingsObserver settingsObserver = new SettingsObserver(mHandler);
        settingsObserver.observe();
    }


    public class WidgetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
                // detect inverted ui mode change
                int uiInvertedMode =
                    mContext.getResources().getConfiguration().uiInvertedMode;
                if (uiInvertedMode != mCurrUiInvertedMode) {
                    mCurrUiInvertedMode = uiInvertedMode;
                    createWidgetView();
                }
            }
        }
    }

}
