package com.netsec.taxiplusdriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Viaje extends AppCompatActivity {

    private SegundoPlano segundoPlano;
    private String SERVIDOR_CONTROLADOR = new Servidor().servidor;
    private SharedPreferences datosViaje,datosConductor;
    private SharedPreferences.Editor editorViaje;
    private String android_id, id_sesion, id_viaje, estado;

    private TextView mensaje,boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_viaje);

        datosViaje = getSharedPreferences("Viaje", this.MODE_PRIVATE);
        datosConductor = getSharedPreferences("Conductor", this.MODE_PRIVATE);
        editorViaje = datosViaje.edit();
        android_id=datosConductor.getString("android_id"," ");
        id_sesion=datosConductor.getString("id_sesion"," ");
        id_viaje=datosViaje.getString("id"," ");
        estado=datosViaje.getString("estado"," ");
        Log.e("android_id",android_id);
        Log.e("id_sesion",id_sesion);

        mensaje=findViewById(R.id.mensaje);
        boton=findViewById(R.id.continuar);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Viaje.this, MapsFuncDriver.class);
                startActivity(intent);
            }
        });

        segundoPlano=new SegundoPlano();
        segundoPlano.execute();
    }

    private class SegundoPlano extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            enviarRespuestaViaje();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public void enviarRespuestaViaje()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"respuesta_viaje.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("respuesta",response);


                        if (response.equals("ocupado")){
                            mensaje.setText("Lo sentimos. \n El viaje ha sido tomado por otro conductor.");
                            boton.setVisibility(View.VISIBLE);

                            editorViaje.putString("id"," ");
                            editorViaje.putString("id_caseta", " ");
                            editorViaje.putString("ruta"," ");
                            editorViaje.putString("numero_pasajeros"," ");
                            editorViaje.putString("nombre_pasajeros"," ");
                            editorViaje.putString("lat"," ");
                            editorViaje.putString("lng"," ");
                            editorViaje.putString("empresa"," ");
                            editorViaje.putString("estado"," ");
                            editorViaje.apply();
                        }
                        if (response.equals("success")){
                            editorViaje.putString("estado","destino");
                            editorViaje.apply();
                            Intent intent = new Intent(Viaje.this, MapsFuncDriver.class);
                            startActivity(intent);
                        }
                        if (response.equals("rechazado")){

                            editorViaje.putString("id"," ");
                            editorViaje.putString("id_caseta", " ");
                            editorViaje.putString("ruta"," ");
                            editorViaje.putString("numero_pasajeros"," ");
                            editorViaje.putString("nombre_pasajeros"," ");
                            editorViaje.putString("lat"," ");
                            editorViaje.putString("lng"," ");
                            editorViaje.putString("empresa"," ");
                            editorViaje.putString("estado"," ");
                            editorViaje.apply();

                            Intent intent = new Intent(Viaje.this, MapsFuncDriver.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mensaje.setText("Hubo un error de conexión. \n Revisa tu red.");
                boton.setVisibility(View.VISIBLE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("android_id",android_id);
                map.put("id_sesion",id_sesion);
                map.put("id_viaje", id_viaje);
                map.put("estado", estado);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /*@Override
    public void onBackPressed() {
    }*/
}