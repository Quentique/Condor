package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matthewtamlin.sliding_intro_screen_library.core.IntroActivity;
import com.matthewtamlin.sliding_intro_screen_library.core.LockableViewPager;

public class GTUFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i("FRAGMENT", "CREATED");
        return inflater.inflate(R.layout.fragment_consent_base, parent, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        WebView vview = view.findViewById(R.id.webview_consent);
        vview.loadUrl("file:///android_asset/cgu.html");
      /*  LinearLayout check1 = (LinearLayout) getLayoutInflater().inflate(R.layout.consent_checkbox, (ViewGroup) view.findViewById(R.id.base_consent), true);
        check1.findViewById(R.id.checkbox_consent).setTag(1);

       ((TextView) check1.findViewById(R.id.consent_text)).setText("COUCOU");
       ((CheckBox) check1.findViewById(R.id.checkbox_consent)).setOnCheckedChangeListener(myCheckboxListener);*/
       if (getArguments().containsKey("id")) {
           if (getArguments().getInt("id") == 1) {
               vview.loadUrl("file:///android_asset/cgu.html");
               LinearLayout check1 = (LinearLayout) getLayoutInflater().inflate(R.layout.consent_checkbox, (ViewGroup) view.findViewById(R.id.base_consent), true);
               ((TextView) check1.findViewById(R.id.consent_text)).setText(R.string.gtu_accept);
               check1.findViewById(R.id.checkbox_consent).setTag(1);
               ((CheckBox) check1.findViewById(R.id.checkbox_consent)).setOnCheckedChangeListener(myCheckboxListener);
           }
           else {
               vview.loadUrl("file:///android_asset/cgu.html");
               LinearLayout check1 = (LinearLayout) ((LinearLayout) getLayoutInflater().inflate(R.layout.consent_checkbox, (ViewGroup) view.findViewById(R.id.base_consent), true)).getChildAt(0);
               ((TextView) check1.findViewById(R.id.consent_text)).setText(R.string.conf_accept);
               check1.findViewById(R.id.checkbox_consent).setTag(2);
               ((CheckBox) check1.findViewById(R.id.checkbox_consent)).setOnCheckedChangeListener(myCheckboxListener);

               LinearLayout check2 = (LinearLayout)((LinearLayout) getLayoutInflater().inflate(R.layout.consent_checkbox, (ViewGroup) view.findViewById(R.id.base_consent), true)).getChildAt(1);
               ((TextView) check2.findViewById(R.id.consent_text)).setText(R.string.stats_accept);
               check2.findViewById(R.id.checkbox_consent).setTag(3);
               ((CheckBox) check2.findViewById(R.id.checkbox_consent)).setOnCheckedChangeListener(myCheckboxListener);

               LinearLayout check3 = (LinearLayout) ((LinearLayout) getLayoutInflater().inflate(R.layout.consent_checkbox, (ViewGroup) view.findViewById(R.id.base_consent), true)).getChildAt(2);
               ((TextView) check3.findViewById(R.id.consent_text)).setText(R.string.crash_accepts);
               check3.findViewById(R.id.checkbox_consent).setTag(4);
               ((CheckBox) check3.findViewById(R.id.checkbox_consent)).setOnCheckedChangeListener(myCheckboxListener);
           }
       }
    }

    private CompoundButton.OnCheckedChangeListener myCheckboxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch ((Integer) buttonView.getTag()) {
                case 1:
                    if (isChecked) {
                        ((IntroActivity) getActivity()).disableRightButton(false);
                        ((IntroActivity) getActivity()).setPagingLockMode(LockableViewPager.LockMode.UNLOCKED);
                    } else {
                        ((IntroActivity) getActivity()).disableRightButton(true);
                        ((IntroActivity) getActivity()).setPagingLockMode(LockableViewPager.LockMode.FULLY_LOCKED);
                    }
                    break;
                case 2:
                    if (isChecked) {
                        ((IntroActivity) getActivity()).disableRightButton(false);
                        ((IntroActivity) getActivity()).disableFinalButton(false);
                        ((IntroActivity) getActivity()).setPagingLockMode(LockableViewPager.LockMode.UNLOCKED);
                    } else {
                        ((IntroActivity) getActivity()).disableRightButton(true);
                        ((IntroActivity) getActivity()).disableFinalButton(true);
                        ((IntroActivity) getActivity()).setPagingLockMode(LockableViewPager.LockMode.COMMAND_LOCKED);
                    }
                    break;
                case 3:
                    if (isChecked) {
                        ((ConsentActivity) getActivity()).edit.putBoolean("firebase", true);
                    } else {
                        ((ConsentActivity) getActivity()).edit.putBoolean("firebase", false);
                    }
                    break;
                case 4:
                    if (isChecked) {
                        ((ConsentActivity) getActivity()).edit.putBoolean("crashlytics", true);
                    } else {
                        ((ConsentActivity) getActivity()).edit.putBoolean("crashlytics", false);
                    }
                default:
                    break;
            }
        }
    };
}
