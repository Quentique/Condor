package com.cvlcondorcet.condor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.supercharge.shimmerlayout.ShimmerLayout;

import static android.view.View.GONE;

/**
 * Home page of the application, displays some pictures and information:
 * links to social networks & telephone numbers and mail.
 * @author Quentin DE MUYNCK
 */

public class MainFragment extends Fragment {
    private Database db;
    private WebView webview;
    private ArrayList<String> correspondance;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.app_name);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.defaults_remote_param);

        TextView title = view.findViewById(R.id.cover_title);
        TextView adress = view.findViewById(R.id.adress_highschool);
        ImageView cover = view.findViewById(R.id.cover_image);
        ImageView logo = view.findViewById(R.id.image_logo);
        db.open();
        title.setText(db.timestamp("name"));
        int color;
        try {
            color = Color.parseColor(db.timestamp("color"));
        } catch (Exception e ) {
            color = Color.parseColor("#000000");
            e.printStackTrace();
        }
        title.setTextColor(color);
        title.setShadowLayer(2.0f, 6.0f,6.0f,Color.parseColor("#000000"));
        adress.setTextColor(color);
        adress.setText(db.timestamp("adresse"));
        adress.setShadowLayer(2.0f, 6.0f,6.0f,Color.parseColor("#000000"));
        File file = new File(getActivity().getFilesDir().toString() + "/" + db.timestamp("cover"));
        File file2 = new File(getActivity().getFilesDir().toString() + "/" + db.timestamp("logo"));
        Picasso.with(getActivity()).load(file).into(cover);
        Picasso.with(getActivity()).load(file2).into(logo);
        LinearLayout layout1 = view.findViewById(R.id.tel1);
        ImageView image1 = layout1.findViewById(R.id.image_contact);
        TextView contactTitle1 = layout1.findViewById(R.id.title_contact);
        TextView contactValue1 = layout1.findViewById(R.id.value_contact);
        image1.setImageResource(R.drawable.ic_phone_black_32dp);
        contactTitle1.setText("Loge : ");
        contactValue1.setText(db.timestamp("tel1"));

        LinearLayout layout2 = view.findViewById(R.id.tel2);
        ImageView image2 = layout2.findViewById(R.id.image_contact);
        TextView contactTitle2 = layout2.findViewById(R.id.title_contact);
        TextView contactValue2 = layout2.findViewById(R.id.value_contact);
        image2.setImageResource(R.drawable.ic_phone_black_32dp);
        contactTitle2.setText("BVS : ");
        contactValue2.setText(db.timestamp("tel2"));

        LinearLayout layout3 = view.findViewById(R.id.mail);
        ImageView image3 = layout3.findViewById(R.id.image_contact);
        TextView contactTitle3 = layout3.findViewById(R.id.title_contact);
        TextView contactValue3 = layout3.findViewById(R.id.value_contact);
        image3.setImageResource(R.drawable.ic_mail_black_32dp);
        contactTitle3.setText("Mail : ");
        contactValue3.setText(db.timestamp("mail"));
       /* final String facebook = db.timestamp("facebook");
        final String twitter = db.timestamp("twitter");*/
        final String high = db.timestamp("website");
       // final String ent = db.timestamp("ent_link");
        JsonArray social;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (correspondance.get((Integer) v.getTag()).contains("facebook")) {
                        PackageManager packageManager = getActivity().getPackageManager();
                        String toshow;
                        try {
                            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                            if (versionCode >= 3002850) { //newer versions of fb app
                               toshow=  "fb://facewebmodal/f?href=" + correspondance.get((Integer) v.getTag());
                            } else { //older versions of fb app
                                toshow= "fb://page/" + correspondance.get((Integer) v.getTag());
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            toshow= correspondance.get((Integer) v.getTag()); //normal web url
                        }
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(toshow));
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(correspondance.get((Integer) v.getTag())));
                }
                startActivity(intent);
            }
        };
            JsonParser parser = new JsonParser();
           // social = new JSONArray(db.timestamp("social_networks"));
        Log.i("SOC", "INITALIZING PARSER");
        try {
            social = parser.parse(db.timestamp("social_networks")).getAsJsonArray();
        } catch (IllegalStateException e) { social = new JsonArray(); }
        Log.i("SOCC", String.valueOf(social.size()));
        int i;
        correspondance = new ArrayList<>();
        LinearLayout tablelayout = view.findViewById(R.id.tablelayout_soc);

        Log.i("MATHS", String.valueOf(Math.ceil((double)social.size()/4)));
        for (i = 0 ; i < Math.ceil((double)social.size()/4); i++){
            Log.i("SOC", "Entering first loop");
            LinearLayout row = new LinearLayout(getActivity());
            row.setTag(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f);
            row.setLayoutParams(params);
            row.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            row.setWeightSum(4f);
            int k;
            int kl = (social.size()-4*i <= 4) ? social.size() : (i+1)*4;
            for (k = i*4; k<kl; k++) {
                Log.i("SOC", "Entering second-loop");
                    JsonObject object = social.get(k).getAsJsonObject();
                    ImageButton button = (ImageButton) getLayoutInflater().inflate(R.layout.social_network_button, (ViewGroup) row, false);
                    LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    button.setLayoutParams(lay);
                    button.setTag(k);
                    button.setImageURI(Uri.parse(getActivity().getFilesDir().toString()+"/"+object.get("image").getAsString()));
                   // Picasso.with(getActivity()).load(getActivity().getFilesDir().toString()+"/"+object.get("image").getAsString()).into(button);
                    Log.i("TEST", getActivity().getFilesDir().toString()+"/"+object.get("image").getAsString());
                    correspondance.add(k, object.get("link").getAsString());
                    button.setOnClickListener(listener);
                    row.addView(button);
            }
            tablelayout.addView(row);
        }

       /* tablelayout.setColumnShrinkable(0, true);
        tablelayout.setColumnStretchable(1, true);
        tablelayout.setColumnStretchable(2, true);
        tablelayout.setColumnStretchable(3, true);*/
        db.close();

        if (MainActivity.allowConnect(getActivity()) && mFirebaseRemoteConfig.getBoolean("website")) {
            webview = view.findViewById(R.id.web_view_start);
            webview.getSettings().setSupportZoom(false);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setBackgroundColor(Color.TRANSPARENT);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith(high)) {
                        Intent intent = new Intent(getActivity(), PostViewerActivity.class);
                        intent.putExtra("id", "0");
                        intent.putExtra("link", url);
                        startActivity(intent);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            new LoadingWeb().execute(high);
        }
        (layout1.findViewById(R.id.cardview_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = String.valueOf(((TextView)view.findViewById(R.id.value_contact)).getText());
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        (layout2.findViewById(R.id.cardview_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = String.valueOf(((TextView)view.findViewById(R.id.value_contact)).getText());
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        (layout3.findViewById(R.id.cardview_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = String.valueOf(((TextView)view.findViewById(R.id.value_contact)).getText());
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("*/*");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

      /*  view.findViewById(R.id.facebook_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("https://www.facebook.com/pg/"+facebook);
                try {
                    ApplicationInfo appInfo = getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                    if (appInfo.enabled) {
                        uri = Uri.parse("fb://page/"+ facebook);
                    }
                } catch (PackageManager.NameNotFoundException ignored) {}
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.twitter_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twitter.contentEquals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                    startActivity(intent);
                }
            }
        });

        view.findViewById(R.id.highschool_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!high.contentEquals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(high));
                    startActivity(intent);
                }
            }
        });
        view.findViewById(R.id.ent_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ent.contentEquals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ent));
                    startActivity(intent);
                }
            }
        });*/
        view.findViewById(R.id.news_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new PostsFragment()).addToBackStack("").commit();
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getActivity());
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PostsFragment");
                analytics.logEvent("fragment", params);
            }
        });
        view.findViewById(R.id.events_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new EventsFragment()).addToBackStack("").commit();
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getActivity());
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "EventsFragment");
                analytics.logEvent("fragment", params);
            }
        });
        view.findViewById(R.id.canteen_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new CanteenFragment()).addToBackStack("").commit();
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getActivity());
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CanteenFragment");
                analytics.logEvent("fragment", params);
            }
        });
        if (!mFirebaseRemoteConfig.getBoolean("posts")) {
            view.findViewById(R.id.news_quick).setVisibility(GONE);
        }
        if (!mFirebaseRemoteConfig.getBoolean("events")) {
            view.findViewById(R.id.events_quick).setVisibility(GONE);
        }
        if (!mFirebaseRemoteConfig.getBoolean("canteen")) {
            view.findViewById(R.id.canteen_quick).setVisibility(GONE);
        }
        if (MainActivity.preferences.getInt("posts_count",0) > 0) {
            ShimmerLayout layouttt = view.findViewById(R.id.news_quick).findViewById(R.id.shimmer);
            layouttt.startShimmerAnimation();
        }
        if(MainActivity.preferences.getInt("events_count", 0) > 0) {
            ShimmerLayout layout = view.findViewById(R.id.events_quick).findViewById(R.id.shimmer);
            layout.startShimmerAnimation();
        }
        if (MainActivity.preferences.getBoolean("canteen", false)) {
            ShimmerLayout layout = view.findViewById(R.id.canteen_quick).findViewById(R.id.shimmer);
            layout.startShimmerAnimation();
        }
    }

    private class LoadingWeb extends AsyncTask<String, Void, Void> {
        String toDisplay = "";
        String high = "";
        protected Void doInBackground(String... args) {
            try {
                try {
                    try {
                        high = args[0];
                        Document doc = Jsoup.connect(args[0]).postDataCharset("UTF-8").get();
                        Element element2 = doc.getElementsByTag("head").first();
                        Element element = doc.getElementById("graphene-slider");
                        toDisplay = element2.toString();
                        toDisplay += "<style>.carousel { background: transparent; width: 100%; margin: auto;} head, body, div { background: transparent; margin: auto;}</style>";
                        toDisplay += element.toString();
                    } catch (IllegalArgumentException e) {}
                } catch (NullPointerException e) {}
            } catch (IOException e) {}
            return null;
        }

        protected void onPostExecute(Void nothing) {
            webview.loadDataWithBaseURL(high, toDisplay, null,"utf-8", null);
        }
    }

}
