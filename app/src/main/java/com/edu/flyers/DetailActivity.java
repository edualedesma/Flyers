package com.edu.flyers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

/**
 * Created by Eduardo Acuña on 12/1/16.
 */
public class DetailActivity extends AppCompatActivity {

    final static String TAG = DetailActivity.class.getSimpleName();

    private int ADDRESS = 9;
    private int METRO = 13;
    private int PARKING = 23;
    private int CUPPRICE = 25;
    private int VIPPRICE = 27;
    private int DESCRIPTION = 30;

    ProgressDialog mProgressDialog;
    private String url = "http://www.discotecasgratis.com/";
    private String disco;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String yourText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Ut volutpat interdum interdum. Nulla laoreet lacus diam, vitae " +
                "sodales sapien commodo faucibus. Vestibulum et feugiat enim. Donec " +
                "semper mi et euismod tempor. Sed sodales eleifend mi id varius. Nam " +
                "et ornare enim, sit amet gravida sapien. Quisque gravida et enim vel " +
                "volutpat. Vivamus egestas ut felis a blandit. Vivamus fringilla " +
                "dignissim mollis. Maecenas imperdiet interdum hendrerit. Aliquam" +
                " dictum hendrerit ultrices. Ut vitae vestibulum dolor. Donec auctor ante" +
                " eget libero molestie porta. Nam tempor fringilla ultricies. Nam sem " +
                "lectus, feugiat eget ullamcorper vitae, ornare et sem. Fusce dapibus ipsum" +
                " sed laoreet suscipit. ";

        Button btnBack = (Button)findViewById(R.id.btnBack);
        Button btnGoToMap = (Button)findViewById(R.id.btnGoTo);

        Bundle extras= getIntent().getExtras();
        if (getIntent().hasExtra("name") ) {
            disco = extras.getString("name");

            Toast.makeText(getApplicationContext(),
                    "Disco: " + disco, Toast.LENGTH_LONG)
                    .show();
        }

        // Execute Title AsyncTask
        new Title().execute();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = "";
                TextView txtAddress = (TextView)findViewById(R.id.address);
                address = txtAddress.getText().toString();

                //Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4192?q=" + Uri.encode("1st & Pike, Seattle"));
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address + Uri.encode(disco));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "There is no Map App installed.", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // get context
        context = getApplicationContext();
    }

    private String cleanDetailSection(String param) {
        if (param != "") {
            param = param.replaceAll("\\<td\\>", "").replaceAll("\\</td\\>", "");
        }
        return param;
    }

    private String cupFormatTransform(String param) {
        return param.replaceAll("e", "€");
    }

    private String vipFormatTransform(String param) {
        return param.replaceAll("\\<td valign=\"bottom\"\\>", "");
    }

    private String cleanDescriptionSection(String desc) {
        String finalBoldPattern = "</b>";
        desc = desc.replaceAll("\\<p\\>", "").replaceAll("\\</p\\>", "")
                   .replaceAll("\\<div style=\"text-justify: newspaper; text-align: justify\">", "")
                   .replaceAll("\\</div\\>", "");

        for (int i = -1; (i = desc.indexOf(finalBoldPattern, i + 1)) != -1; ) {
            desc = desc.replaceAll("(\\<b\\>).*(\\<\\/b\\>)", "");
        }

        return desc;
    }

    // Title AsyncTask
    private class Title extends AsyncTask<Void, Void, Void> {
        String title = "";
        String address = "";
        String metro = "";
        String parking = "";
        String bannerUrl = "";
        String cupPrice = "";
        String vipPrice = "";
        String description = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(DetailActivity.this);
            mProgressDialog.setTitle("Flyers");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site and get the html document title
                Document doc = Jsoup.connect(url + disco).get();
                System.out.println(TAG);
                Log.d(TAG, "My document is " + doc);

                Elements data = doc.select("td");
                Elements srcBanner = doc.select("img[class=logotipos]");
                Elements desc = doc.select("div");

                title = doc.title();
                address = cleanDetailSection(data.eq(ADDRESS).toString());
                metro = cleanDetailSection(data.eq(METRO).toString());
                parking = cleanDetailSection(data.eq(PARKING).toString());
                bannerUrl = url + srcBanner.attr("src");
                // Transform if url address has spaces
                bannerUrl = bannerUrl.replaceAll(" ", "%20");
                cupPrice = cupFormatTransform(cleanDetailSection(data.eq(CUPPRICE).toString()));
                vipPrice = vipFormatTransform(cleanDetailSection(data.eq(VIPPRICE).toString()));

                description = cleanDescriptionSection(desc.eq(DESCRIPTION).toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            TextView txtTitle = (TextView)findViewById(R.id.lblTitle);
            txtTitle.setText(title);

            // Load image
            ImageView imgBanner = (ImageView)findViewById(R.id.banner);
            Picasso.with(context)
                    .load(bannerUrl)
                    .resize(300, 250)
                    .into(imgBanner);
            System.out.println("Mi url es: " + bannerUrl); // TODO: Quit one quote in bannerUrl

            TextView txtAddress = (TextView)findViewById(R.id.address);
            txtAddress.setText(address);
            TextView txtMetro = (TextView)findViewById(R.id.metro);
            txtMetro.setText(metro);
            TextView txtParking = (TextView)findViewById(R.id.parking);
            txtParking.setText(parking);
            TextView txtCup = (TextView)findViewById(R.id.cup);
            txtCup.setText(cupPrice);
            TextView txtVip = (TextView)findViewById(R.id.vip);
            txtVip.setText(vipPrice);
            ExpandableTextView expandableDescription = (ExpandableTextView)findViewById(R.id.description);
            expandableDescription.setText(description);

            mProgressDialog.dismiss();
        }
    }
}
