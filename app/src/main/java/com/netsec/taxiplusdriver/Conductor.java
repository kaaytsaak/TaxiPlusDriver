package com.netsec.taxiplusdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Conductor extends AppCompatActivity {

    private TextView nombre,telefono,placas,modelo,registro,btnCerrar,mensaje;
    private SharedPreferences preferencias,preferenciasViaje;
    private String strNombre,strTelefono,strPlacas,strMarca,strColor,strRegistro,strId,strSesion,strRutaImg;
    private ConstraintLayout alerta;
    private boolean verAlerta=false;
    private Button btnCancelar, btnOk;
    private LinearLayout capaCerrar;
    private ImageView btnViaje,btnMapa;
    private static String SERVIDOR_CONTROLADOR = new Servidor().servidor;
    private SegundoPlano segundoPlano;
    private ImageView imagenPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_conductor);

        preferencias = getSharedPreferences("Conductor",this.MODE_PRIVATE);
        preferenciasViaje = getSharedPreferences("Viaje",this.MODE_PRIVATE);

        nombre=findViewById(R.id.nombre);
        telefono=findViewById(R.id.telefono);
        placas=findViewById(R.id.placas);
        modelo=findViewById(R.id.modelo);
        registro=findViewById(R.id.registro);
        btnCerrar=findViewById(R.id.btnCerrar);
        mensaje=findViewById(R.id.mensaje);
        alerta=findViewById(R.id.alerta);
        btnCancelar=findViewById(R.id.btnCancelar);
        btnOk=findViewById(R.id.btnOk);
        capaCerrar=findViewById(R.id.capaCerrar);
        imagenPerfil=findViewById(R.id.imagen_perfil);

        btnViaje=findViewById(R.id.btnViaje);
        btnMapa=findViewById(R.id.btnMapa);

        strId=preferencias.getString("id"," ");
        strSesion=preferencias.getString("id_sesion"," ");
        strNombre=preferencias.getString("nombre"," ");
        strTelefono=preferencias.getString("telefono"," ");
        strPlacas=preferencias.getString("placa_vehiculo"," ");
        strMarca=preferencias.getString("marca_vehiculo"," ");
        strRegistro=preferencias.getString("fecha_registro"," ");
        strRutaImg=preferencias.getString("foto"," ");

        nombre.setText(strNombre);
        telefono.setText(strTelefono);
        placas.setText(strPlacas);
        modelo.setText(strMarca);
        registro.setText(strRegistro);
        Log.e("ruta",strRutaImg);

        Picasso.get()
                .load(SERVIDOR_CONTROLADOR+strRutaImg)
                .placeholder(R.drawable.ilustracion)
                .resize(160, 160) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                .into(imagenPerfil);

        

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verAlerta==false){
                    alerta.setVisibility(View.VISIBLE);
                    verAlerta=true;
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verAlerta==true){
                    alerta.setVisibility(View.GONE);
                    verAlerta=false;
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verAlerta==true){
                    capaCerrar.setVisibility(View.GONE);
                    mensaje.setVisibility(View.VISIBLE);
                    segundoPlano=new SegundoPlano();
                    segundoPlano.execute();
                }
            }
        });

        btnViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Conductor.this,Historial.class);
                startActivity(intent);
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Conductor.this,MapsFuncDriver.class);
                startActivity(intent);
            }
        });
    }

    private class SegundoPlano extends AsyncTask<Void, Integer,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            cerrar_sesion();
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

    public void cerrar_sesion()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"cerrar_sesion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("respuestaCerrar",response);
                        if (response.equals("success")){
                            preferencias.edit().clear().apply();
                            preferenciasViaje.edit().clear().apply();
                            Intent intent = new Intent(Conductor.this,Login.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> map = new HashMap<>();
                map.put("id_conductor",strId);
                map.put("id_sesion",strSesion);

                return map;
            }
        };
        requestQueue.add(request);
    }
}