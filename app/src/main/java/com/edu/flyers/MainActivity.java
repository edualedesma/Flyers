package com.edu.flyers;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

        discos = new String[]{
                "Palace","Shoko","Hotel-Puerta-America","Kapital","Faena","Moss-Madrid","Lab",
                "Joy-Eslava","Terraza-D-Gree","New-Garamond","Garamond","Sala-Heineken","Tartufo",
                "Ten","Macumba","Studio-12","Manhattan","Coco","Mondo","Moon-Dance","Velvet",
                "Larios-Cafe","Goya-43","Panorama","Reina-Bruja","Copernico","Penelope","Inn",
                "Mansion","Rumba-salsa","Rumba-Fun-Session","Lemon","Rumba-Society","New-Princess",
                "Kupula","Banloo","Lolita","Teatro-Bodevil","Paddock"};

        // Get ListView object from xml
        discoListView = getListView();

        // Create adapter to fill the list
        ArrayAdapter<String> discoAdapter = new ArrayAdapter<String>(this, R.layout.layout_list, R.id.discoName, discos);

        // Set adapter to ListView
        discoListView.setAdapter(discoAdapter);

        // ListView item click listener
        discoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView clicked item index
                int itemPosition = position;

                // ListView clicked item value
                String itemValue = (String) discoListView.getItemAtPosition(position);

                // Show alert
                Toast.makeText(getApplicationContext(),
                        "Position: " + itemPosition + " ListItem: " + itemValue, Toast.LENGTH_LONG)
                        .show();

                // Send data to DetailActivity
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("name", itemValue);
                // Go to DetailActivity
                startActivityForResult(i, 1);
            }
        });
    }

    protected void onActivityResult(int resultado, int codigo, Intent data) {
        if (resultado == 2 & codigo == RESULT_OK) {
            //nombre = data.getExtras().getString("nombre");
            //apellidos = data.getExtras().getString("apellidos");

            //TextView miEtiqueta = (TextView) findViewById(R.id.txtDatosContacto);
            //miEtiqueta.setText("Nombre: " + nombre + "\n" + "Appelidos:" + apellidos);
        }
    }
}
