package com.edu.flyers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int ADDRESS = 9;
    private int METRO = 13;
    private int PARKING = 23;
    private int CUPPRICE = 25;
    private int VIPPRICE = 27;

    //private String titleText = "";

    ProgressDialog mProgressDialog;
    //String url = "http://www.androidbegin.com";
    String url = "http://www.discotecasgratis.com/";
    //String url = "http://www.discotecasgratis.com/pases-y-flyers-Velvet";
    //String url = "http://www.discotecasgratis.com/Velvet";
    //String url = "http://www.discotecasgratis.com/#buscador";
    String[] discos = {"Palace"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get html from website
        //soupRequest();
        //downloadThread.start();
        //TextView title = (TextView)findViewById(R.id.lblTitle);
        //title.setText(titleText);
        // Locate the Buttons in activity_main.xml
        Button btnTitle = (Button) findViewById(R.id.btnTitle);

        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute Title AsyncTask
                new Title().execute();

            }
        });
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Android Basic JSoup Tutorial");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document doc = Jsoup.connect(url + discos[0]).get();
                // Get the html document title
                System.out.println("My document is: " + doc);
                Elements data = doc.select("td");
                Elements srcBanner = doc.select("img[class=logotipos]");
                //System.out.println("My banner is: " + srcBanner.attr("src"));
                //getURLBanner(srcBanner);
                //System.out.println("My data is: " + data.eq(METRO));
                title = doc.title();
                address = data.eq(ADDRESS).toString();
                metro = data.eq(METRO).toString();
                parking = data.eq(PARKING).toString();
                bannerUrl = url + srcBanner.attr("src");
                cupPrice = data.eq(CUPPRICE).toString();
                vipPrice = data.eq(VIPPRICE).toString();

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
            ImageView imgBanner = (ImageView)findViewById(R.id.banner);

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

            mProgressDialog.dismiss();
        }
    }

    /*public void soupRequest() {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://en.wikipedia.org/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements newsHeadlines = doc.select("#mp-itn b a");

        System.out.println(newsHeadlines.size());
    }*/

    /*Thread downloadThread = new Thread() {
        public void run() {
            System.out.println("Starting with the thread...");
            Document doc;
            try {
                doc = Jsoup.connect("http://google.es/").get();
                //String title = doc.title();
                titleText = doc.title();
                System.out.print("My title is " + titleText);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };*/
}
