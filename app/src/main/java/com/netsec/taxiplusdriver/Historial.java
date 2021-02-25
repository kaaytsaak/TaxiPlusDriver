package com.netsec.taxiplusdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Historial extends AppCompatActivity {

    private ImageView btnConductor,btnMapa;
    private SegundoPlanoHistorial segundoPlanoHistorial;
    private static final String SERVIDOR_CONTROLADOR = new Servidor().servidor;
    private SharedPreferences sharedPreferences;
    private SharedPreferences preferencias;
    private String id_usuario, id_sesion, android_id;
    private RecyclerView recyclerView;
    private ArrayList<TaxiRecycler> listarank;
    private Context context;
    private TextView mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_historial);

        preferencias = getSharedPreferences("Conductor", this.MODE_PRIVATE);

        id_usuario=preferencias.getString("id"," ");
        id_sesion=preferencias.getString("id_sesion"," ");
        android_id=preferencias.getString("android_id"," ");


        btnConductor=findViewById(R.id.btnConductor);
        btnMapa=findViewById(R.id.btnMapa);
        mensaje=findViewById(R.id.mensaje);

        context = this;
        recyclerView =(RecyclerView) findViewById(R.id.TaxiPlusRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        listarank = new ArrayList<>();
        segundoPlanoHistorial=new SegundoPlanoHistorial();
        segundoPlanoHistorial.execute();

        btnConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conductor=new Intent(Historial.this,Conductor.class);
                startActivity(conductor);
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conductor=new Intent(Historial.this,MapsFuncDriver.class);
                startActivity(conductor);
            }
        });
    }


    private class SegundoPlanoHistorial extends AsyncTask<Void, Integer,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("primero" , "onPreExecute: " );
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("segundo" , "onPreExecute: " );
            pedir_historial_viajes();
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.e("tercero" , "onPreExecute: " );
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("cuarto" , "onPreExecute: " );
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.e("quinto" , "onPreExecute: " );
        }
    }
    public void pedir_historial_viajes()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"pedir_historial_viajes.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        String limpio=response.replace("\\","");
                        //Log.e("jsonObject:",""+limpio);

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(limpio);

                            for (int i =0;i<jsonArray.length();i++){
                                Log.e("jsonarrlimpio:", String.valueOf(jsonArray.get(i)));
                                JSONObject datos = jsonArray.getJSONObject(i);
                                Log.e("jsondatlimpio", String.valueOf(datos.get("id")));
                                listarank.add(new TaxiRecycler(String.valueOf(datos.get("id")),String.valueOf(datos.get("fecha_inicio")),String.valueOf(datos.get("fecha_termino")),String.valueOf(datos.get("empresa")),String.valueOf(datos.get("estado"))));
                            }
                            AdapterLista adapterLista = new AdapterLista(listarank);
                            recyclerView.setAdapter(adapterLista);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.e("jsonaraa:",""+jsonArray);
                        mensaje.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }



                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id_conductor",id_usuario);
                //map.put("usuario",strUsuario);0
                map.put("id_sesion",id_sesion);
                //map.put("ubicacion", strUbicacion);
                //map.put("contacto", strContacto);
                //map.put("ayuda", strAyuda);
                return map;
            }

        };
        requestQueue.add(request);
    }
}