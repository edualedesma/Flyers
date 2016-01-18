package com.edu.flyers;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by edualedesma on 18/1/16.
 */
public class FlyersActivity extends AppCompatActivity {

    final static String TAG = FlyersActivity.class.getSimpleName();

    private ArrayList<String> flyers;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flyers);

        Button btnBack = (Button)findViewById(R.id.btnBack);

        // Get context
        context = getApplicationContext();

        Bundle extras= getIntent().getExtras();
        if (getIntent().hasExtra("flyers")) {
            flyers = extras.getStringArrayList("flyers");

            LinearLayout ll = (LinearLayout)findViewById(R.id.flyersLayout);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;

            // Check if that disco has flyers
            if (flyers != null) {
                for (int i = 0; i < flyers.size(); i++) {
                    Log.d(TAG, flyers.get(i));
                    // Create ImageView in XML
                    ImageView iv = new ImageView(this);

                    Picasso.with(context)
                            .load(flyers.get(i))
                            .resize(0, 800)
                            .into(iv);

                    ll.addView(iv, lp);
                }
            }
            else {
                // Show a message info in the Activity
                TextView noImagesInfo = new TextView(this);
                noImagesInfo.setText(R.string.noImagesInfo);
                ll.addView(noImagesInfo);
            }

            // Back button to return to main activity
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
