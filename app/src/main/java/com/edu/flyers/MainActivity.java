package com.edu.flyers;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Eduardo Acuña.
 */
public class MainActivity extends ListActivity {

    private String[] discos;
    ListView discoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        discos = new String[]{"Palace",
                              "Shoko",
                              "Hotel-Puerta-America",
                              "Kapital",
                              "Faena"};

        // Get ListView object from xml
        discoListView = getListView();

        // Create adapter to fill the list
        ArrayAdapter<String> discoAdapter = new ArrayAdapter<String>(this, R.layout.layout_list, R.id.discoName, discos);

        // Set adapter to ListView
        discoListView.setAdapter(discoAdapter);
    }


}
