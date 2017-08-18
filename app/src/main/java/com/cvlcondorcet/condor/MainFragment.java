package com.cvlcondorcet.condor;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Quentin DE MUYNCK on 02/08/2017.
 */

public class MainFragment extends Fragment {
    TextView title, adress;
    ImageView cover, logo;
    private Database db;
    Button bouton;

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
        title = view.findViewById(R.id.cover_title);
        adress = view.findViewById(R.id.adress_highschool);
        cover = view.findViewById(R.id.cover_image);
        logo = view.findViewById(R.id.image_logo);
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
        LinearLayout layout1 = (LinearLayout) view.findViewById(R.id.tel1);
        ImageView image1 = layout1.findViewById(R.id.image_contact);
        TextView contactTitle1 = layout1.findViewById(R.id.title_contact);
        TextView contactValue1 = layout1.findViewById(R.id.value_contact);
        image1.setImageResource(R.drawable.ic_phone_black_32dp);
        contactTitle1.setText("Loge : ");
        contactValue1.setText(db.timestamp("tel1"));

        LinearLayout layout2 = (LinearLayout) view.findViewById(R.id.tel2);
        ImageView image2 = layout2.findViewById(R.id.image_contact);
        TextView contactTitle2 = layout2.findViewById(R.id.title_contact);
        TextView contactValue2 = layout2.findViewById(R.id.value_contact);
        image2.setImageResource(R.drawable.ic_phone_black_32dp);
        contactTitle2.setText("BVS : ");
        contactValue2.setText(db.timestamp("tel2"));

        LinearLayout layout3 = (LinearLayout) view.findViewById(R.id.mail);
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

        ((CardView) layout1.findViewById(R.id.cardview_contact)).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
                  String phone = String.valueOf(((TextView)view.findViewById(R.id.value_contact)).getText());
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
              startActivity(intent);
          }
       });
        ((CardView) layout2.findViewById(R.id.cardview_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = String.valueOf(((TextView)view.findViewById(R.id.value_contact)).getText());
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        ((CardView) layout3.findViewById(R.id.cardview_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = String.valueOf(((TextView)view.findViewById(R.id.value_contact)).getText());
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, mail);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

                }
                startActivity(intent);
            }
        });

        view.findViewById(R.id.facebook_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook));
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



        /*bouton = view.findViewById(R.id.button);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String YourPageURL = "https://www.facebook.com/condobelfort/";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YourPageURL));

                startActivity(browserIntent);
            }
        });*/
    }
}
