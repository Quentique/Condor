package com.cvlcondorcet.condor;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Home page of the application, displays some pictures and information:
 * links to social networks & telephone numbers and mail.
 * @author Quentin DE MUYNCK
 */

public class MainFragment extends Fragment {
    private Database db;
    //Button bouton;

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
        Log.i("EEEE", getActivity().getFilesDir().toString() + "/" + db.timestamp("cover"));
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
        db.close();

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
                        Log.i("TEST", "work");
                    }
                    Log.i("FACEBOOK", "TRY");
                } catch (PackageManager.NameNotFoundException ignored) {}
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.twitter_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                startActivity(intent);
            }
        });

        view.findViewById(R.id.highschool_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(high));
                startActivity(intent);
            }
        });
        view.findViewById(R.id.news_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new PostsFragment()).addToBackStack("").commit();
            }
        });
        view.findViewById(R.id.events_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new EventsFragment()).addToBackStack("").commit();
            }
        });
        view.findViewById(R.id.canteen_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new CanteenFragment()).addToBackStack("").commit();
            }
        });

    }
}
