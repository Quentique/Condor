package com.cvlcondorcet.condor;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

/**
 * Home page of the application, displays some pictures and information:
 * links to social networks & telephone numbers and mail.
 * @author Quentin DE MUYNCK
 */

public class MainFragment extends Fragment {
    private Database db;
    private WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.app_name);
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
        final String facebook = db.timestamp("facebook");
        final String twitter = db.timestamp("twitter");
        final String high = db.timestamp("website");
        final String ent = db.timestamp("ent_link");
        db.close();

        if (MainActivity.allowConnect(getActivity())) {
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

        view.findViewById(R.id.facebook_logo).setOnClickListener(new View.OnClickListener() {
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
        });
        view.findViewById(R.id.news_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new PostsFragment()).addToBackStack("").commit();
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getActivity());
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "PostsFragment");
                analytics.logEvent("page", params);
            }
        });
        view.findViewById(R.id.events_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new EventsFragment()).addToBackStack("").commit();
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getActivity());
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "EventsFragment");
                analytics.logEvent("page", params);
            }
        });
        view.findViewById(R.id.canteen_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new CanteenFragment()).addToBackStack("").commit();
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getActivity());
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CanteenFragment");
                analytics.logEvent("page", params);
            }
        });

    }

    private class LoadingWeb extends AsyncTask<String, Void, Void> {
        String toDisplay = "";
        String high = "";
        protected Void doInBackground(String... args) {
            try {
                high = args[0];
                Document doc = Jsoup.connect(args[0]).postDataCharset("UTF-8").get();
                Element element2 = doc.getElementsByTag("head").first();
                Element element = doc.getElementById("graphene-slider");
                toDisplay = element2.toString();
                toDisplay +="<style>.carousel { background: transparent; width: 100%; margin: auto;} head, body, div { background: transparent; margin: auto;}</style>";
                toDisplay += element.toString();
            } catch (IOException e) {}
            return null;
        }

        protected void onPostExecute(Void nothing) {
            webview.loadDataWithBaseURL(high, toDisplay, null,"utf-8", null);
        }
    }

}
