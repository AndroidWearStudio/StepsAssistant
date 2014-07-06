package com.sm.stepsassistant;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.app.ActivityManager;
import android.content.Context;
import android.app.ActivityManager.RunningServiceInfo;

import com.sm.stepsassistant.fragments.MainFragment;
import com.sm.stepsassistant.fragments.SettingFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyWearActivity extends Activity {

    private ImageView firstIndicator;
    private ImageView secondIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int steps = StartListenerService.calculateSteps(this);
        final String time = StartListenerService.calculateTime(this);
        setPercentage(steps);

        setContentView(R.layout.activity_my_wear);
        setupViews(steps, time);

        setInitialAlarm();
        if (!isMyServiceRunning(StartListenerService.class)) {
            Intent listenerIntent = new Intent(this, StartListenerService.class);
            startService(listenerIntent);
        }
    }

    private void setupViews(int steps, String time){
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        firstIndicator = (ImageView) findViewById(R.id.indicator_0);
        secondIndicator = (ImageView) findViewById(R.id.indicator_1);
        final FPA adapter = new FPA(getFragmentManager());
        MainFragment mainFragment = new MainFragment(steps,time);
        SettingFragment settingFragment = new SettingFragment();
        adapter.addFragment(mainFragment);
        adapter.addFragment(settingFragment);
        setIndicator(0);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                setIndicator(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mPager.setAdapter(adapter);
    }

    private void setIndicator(int i) {
        switch (i) {
            case 0:
                firstIndicator.setImageResource(R.drawable.full_10);
                secondIndicator.setImageResource(R.drawable.empty_10);
                break;
            case 1:
                firstIndicator.setImageResource(R.drawable.empty_10);
                secondIndicator.setImageResource(R.drawable.full_10);
                break;
        }
    }

    public void setInitialAlarm(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR,0);
        c.add(Calendar.DATE,1);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        Intent resetIntent = new Intent(this, ResetReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, resetIntent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), 86400000, pi); //86400000 is ms per day
        Log.d("OUTPUT","Alarm set for midnight");
    }

    public void setPercentage(int steps){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int stepGoal = prefs.getInt(StartListenerService.DAILY_GOAL,10000);
        PercentView.percent = (float)steps/(float)stepGoal;
        if (PercentView.percent > 1) PercentView.percent = 1;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public class FPA extends FragmentPagerAdapter {

        List<Fragment> mFragments = null;

        public FPA(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<Fragment>();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }
    }
}
