package com.cvlcondorcet.condor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays license, author information, send feedbacks, etc.
 * @author Quentin DE MUYNCK
 */

public class HelpFragment extends Fragment {

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/
    int click = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.help);
        TextView version = view.findViewById(R.id.version_code);
        ImageView logo = view.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click++;
                if (click == 7) {
                   // final Toast toast = Toast.makeText(getActivity(), "« Quand les gens retrouveraient nos corps calcinés, ils se demanderaient ce qu'on fabriquait ensemble. »", Toast.LENGTH_LONG);
				   final Toast toast = Toast.makeText(getActivity(), "« S'enfuir, se retrouver, s'ouvrir, s'embrasser, s'émanciper, s'acharner, se découvrir, s'associer, se compromettre, se confronter. »", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.TOP, 0, 70);
                    toast.show();
                    new CountDownTimer(7000, 1000) {
                        public void onTick(long millisUntilFInished) {toast.show();}
                        public void onFinish() {toast.show();}
                    }.start();
                    Animation anim = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    anim.setDuration(2000);
                    anim.setInterpolator(new AccelerateDecelerateInterpolator());
                    anim.setAnimationListener(null);
                    view.startAnimation(anim);
                    click = 0;

                }
            }
        });
        version.setText("Version " + BuildConfig.VERSION_NAME);
        Button button = view.findViewById(R.id.licenses);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LicensesActivity.class);
                getActivity().startActivity(intent);
            }
        });
        Button button2 = view.findViewById(R.id.bug_report);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("*/*");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"cvlcondorcet@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[CONDOR] Report");
                intent.putExtra(Intent.EXTRA_TEXT, "Subject : \n Message : ");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
