package com.cvlcondorcet.condor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.firebase.messaging.FirebaseMessaging;
import com.matthewtamlin.sliding_intro_screen_library.buttons.IntroButton;
import com.matthewtamlin.sliding_intro_screen_library.core.IntroActivity;
import com.matthewtamlin.sliding_intro_screen_library.core.LockableViewPager;

import java.util.ArrayList;
import java.util.Collection;

public class ConsentActivity extends IntroActivity {

    public SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NoActionBar);
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setItems(null,null);
        builder2.setTitle("Félicitations !");
        builder2.setMessage(Html.fromHtml(getString(R.string.first_window)));
        builder2.setCancelable(true);
        builder2.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder2.create();
        dialog.show();
        disableLeftButton(true);
        disableRightButton(true);
        disableFinalButton(true);
        disableLeftButtonOnLastPage(false);
        setPagingLockMode(LockableViewPager.LockMode.FULLY_LOCKED);
        changeHorizontalDividerVisibility(true);
        getRootView().setBackgroundColor(getResources().getColor(R.color.secondaryTextColor));
        getRootView().findViewById(com.matthewtamlin.sliding_intro_screen_library.R.id.intro_activity_viewPager).setBackgroundColor(getResources().getColor(R.color.secondaryTextColor));
       // getRootView().findViewById(com.matthewtamlin.sliding_intro_screen_library.R.id.intro_activity_progressIndicatorHolder).setBackgroundColor(getResources().getColor(R.color.primaryColor));
        ((RelativeLayout.LayoutParams) getRootView().findViewById(com.matthewtamlin.sliding_intro_screen_library.R.id.intro_activity_viewPager).getLayoutParams()).setMargins(0, 0, 0, 140);
        getRootView().findViewById(com.matthewtamlin.sliding_intro_screen_library.R.id.intro_activity_progressIndicatorHolder).setBackgroundColor(getResources().getColor(R.color.primaryColor));
        hideStatusBar();

        addPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    setPagingLockMode(LockableViewPager.LockMode.COMMAND_LOCKED);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected Collection<Fragment> generatePages(Bundle savedInstanceState) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        Fragment fragment = new GTUFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", 1);
        fragment.setArguments(bundle);
        Fragment fragment2 = new GTUFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("id", 2);
        fragment2.setArguments(bundle2);
        fragments.add(fragment);
        fragments.add(fragment2);
        return fragments;
    }

    @Override
    protected IntroButton.Behaviour generateFinalButtonBehaviour() {
        /* The pending changes to the shared preferences editor will be applied when the
         * introduction is successfully completed. By setting a flag in the pending edits and
         * checking the status of the flag when the activity starts, the introduction screen can
         * be skipped if it has previously been completed.
         */
      //  final SharedPreferences sp = getSharedPreferences(DISPLAY_ONCE_PREFS, MODE_PRIVATE);
       // final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(DISPLAY_ONCE_KEY, true);

        // Define the next activity intent and create the Behaviour to use for the final button
        final Intent nextActivity = new Intent(this, MainActivity.class);
        edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        Log.i("TEST", PreferenceManager.getDefaultSharedPreferences(this).getString("language", "test"));
        edit.putInt("version", BuildConfig.VERSION_CODE);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        return new IntroButton.ProgressToNextActivity(nextActivity, edit);
       // return null;
    }

}
class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.i("ADAPTER", "HELLO POSITION 0");
            return new GTUFragment();
        } else {return new GTUFragment();}
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            default:
                return "";
        }
    }

}