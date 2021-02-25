package com.netsec.taxiplusdriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class ActivarPermisos extends AppCompatActivity {

    private Button activar, volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activar_permisos);

        activar=(Button) findViewById(R.id.activar);
        volver=(Button) findViewById(R.id.volver);

        activar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVolver=new Intent(ActivarPermisos.this,MapsFuncDriver.class);
                startActivity(intentVolver);
            }
        });
    }
}