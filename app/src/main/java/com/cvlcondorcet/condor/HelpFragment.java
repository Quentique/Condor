package com.cvlcondorcet.condor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Displays license, author information, send feedbacks, etc.
 * @author Quentin DE MUYNCK
 */

public class HelpFragment extends Fragment {

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.help);
        TextView version = view.findViewById(R.id.version_code);
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
                intent.putExtra(Intent.EXTRA_EMAIL, "cvlcondorcet@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "[CONDOR] Report");
                intent.putExtra(Intent.EXTRA_TEXT, "Subject : \n Message : ");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
