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
import java.util.ArrayList;

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

    private String urlFlyers = "http://www.discotecasgratis.com/pases-y-flyers-";
    ArrayList<String> flyersSrc;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button btnBack = (Button)findViewById(R.id.btnBack);
        Button btnGoToMap = (Button)findViewById(R.id.btnGoTo);
        Button btnFlyers = (Button)findViewById(R.id.btnFlyers);

        Bundle extras= getIntent().getExtras();
        if (getIntent().hasExtra("name") ) {
            disco = extras.getString("name");

            urlFlyers = urlFlyers + disco;

            /*Toast.makeText(getApplicationContext(),
                    "Disco: " + disco, Toast.LENGTH_LONG)
                    .show();*/
        }

        // Execute Title AsyncTask
        new Title().execute();

        // Back button to return to main activity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Button to visit de specific address in the map app.
        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = "";
                TextView txtAddress = (TextView)findViewById(R.id.address);
                address = txtAddress.getText().toString();

                // Go to MapsActivity
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("address", address);
                startActivityForResult(i, 1);

                /*
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
                }*/
            }
        });

        // Button to view the disco flyers
        btnFlyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send data to FlyersActivity
                Intent i = new Intent(getApplicationContext(), FlyersActivity.class);
                i.putExtra("flyers", flyersSrc);
                // Go to FlyersActivity
                startActivityForResult(i, 1);
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
                // Connect to the web site and get the html document
                Document doc = Jsoup.connect(url + disco).get();
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

                // Get flyers form specific disco
                Document docFlyers = Jsoup.connect(urlFlyers)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(600000)
                        .get();

                Elements imgFlyers = docFlyers.select("img[class=pasesyflyers]");
                if (!imgFlyers.isEmpty()) {
                    Log.d(TAG, "There are images.");
                    flyersSrc = new ArrayList<String>();
                    for (int i=0; i<imgFlyers.size(); i++) {
                        flyersSrc.add(url + imgFlyers.eq(i).attr("src"));
                    }
                }
                else {
                    Log.d(TAG, "There aren't images.");
                }

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
                    .resize(400, 350)
                    .into(imgBanner);
            Log.d(TAG, "Mi url es: " + bannerUrl); // TODO: Quit one quote in bannerUrl

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
