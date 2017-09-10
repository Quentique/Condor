package com.cvlcondorcet.condor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Displays state of sync by using a BroadcastReceiver (data transmitted from {@link Sync})
 * @author Quentin DE MUYNCK
 * @see Sync#displayProgress()
 */
public class SyncingFragment extends Fragment {

    private ImageButton button;
    private TextView displayPercent, message;
    private ProgressBar bar;
    private boolean sync;
    private Intent servicee;

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_syncing, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.sync));
        button = view.findViewById(R.id.button_sync);
        displayPercent = view.findViewById(R.id.sync_display_percent);
        message = view.findViewById(R.id.sync_display_step);
        bar = view.findViewById(R.id.progress_sync);
        if (MainActivity.allowConnect(getActivity())) {
            button.setImageResource(R.drawable.ic_sync_black_300dp);
            message.setText(R.string.sync_ready);
            sync = true;
        } else {
            button.setImageResource(R.drawable.ic_sync_disabled_black_300dp);
            message.setText(R.string.sync_forbidden);
            sync = false;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sync) {
                    button.setVisibility(GONE);
                    bar.setVisibility(VISIBLE);
                    message.setText("Syncing general settings...");
                    servicee = new Intent(getActivity(), Sync.class);
                    getActivity().startService(servicee);
                }
            }
        });
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bar.setVisibility(VISIBLE);
            button.setVisibility(GONE);
            updateUI(intent);
        }
    };

    /**
     * Registers the listener to get state.
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Sync.broadcast_URI));
    }

    /**
     * Unregisters receiver (memory economy)
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    /**
     * Displays data received by the broadcast receiver
     * @param intent    the intent transmitted by the broadcast receiver
     * @see Sync#displayProgress()
     */
    private void updateUI(Intent intent) {
        int progress = intent.getIntExtra("progress", -3);
        displayPercent.setText(String.valueOf(intent.getIntExtra("progress", -3)) + " %");
        switch (progress) {
            case -1:
                button.setImageResource(R.drawable.ic_sync_problem_black_300dp);
                bar.setVisibility(GONE);
                button.setVisibility(VISIBLE);
                sync = false;
                break;
            case 100:
                button.setVisibility(VISIBLE);
                bar.setVisibility(GONE);
                sync = false;
                break;
        }
        message.setText(Html.fromHtml(intent.getStringExtra("progressMessage")));
    }
}
