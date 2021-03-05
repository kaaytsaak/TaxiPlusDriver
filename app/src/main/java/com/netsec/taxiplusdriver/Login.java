package com.netsec.taxiplusdriver;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity{

    private EditText telefono,pin;
    private TextView tvIniciando;
    private Button boton;
    private String intel, inpin, android_id, firecode;
    private enviarDatos enviarDatos;
    private boolean paso1,paso2;
    private static String SERVIDOR_CONTROLADOR = new Servidor().servidor;
    private int check=0;
    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);


        boton=findViewById(R.id.btnLogin);
        telefono=findViewById(R.id.telefono);
        pin=findViewById(R.id.pin);
        tvIniciando=findViewById(R.id.tvIniciando);

        android_id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        preferencias = getSharedPreferences("Conductor",this.MODE_PRIVATE);
        editor = preferencias.edit();
        firecode=preferencias.getString("firecode"," ");

        if (firecode.equals(" ")){
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                //Log.e("firebase", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            String token = task.getResult();
                            editor.putString("firecode",token);
                            editor.apply();
                        }
                    });
        }

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intel = telefono.getText().toString().trim();
                inpin = pin.getText().toString().trim();

                boton.setVisibility(View.GONE);
                tvIniciando.setVisibility(View.VISIBLE);
                tvIniciando.setText("Iniciando sesión ...");
                enviarDatos= new enviarDatos();
                enviarDatos.execute();
            }
        });
    }

    private class enviarDatos extends AsyncTask<Void, Integer,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("firecode", "es :"+firecode);
            enviarLogin();
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

    public void enviarLogin()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"acceso_conductor.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("EnviarRes",response);
                        if (response.equals("no existe")) {
                            boton.setVisibility(View.VISIBLE);
                            tvIniciando.setText("Datos incorrectos");
                        }
                        else
                        {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String strId = jsonObject.getString("id");
                                String strNombre = jsonObject.getString("nombre");
                                String strApellido = jsonObject.getString("apellido");
                                String strAndroid = jsonObject.getString("android_id");
                                String strGenero = jsonObject.getString("genero");
                                String strPassword= jsonObject.getString("password");
                                String strTelefono = jsonObject.getString("telefono");
                                String strFoto = jsonObject.getString("foto");
                                String strVehiculo = jsonObject.getString("marca_vehiculo");
                                String strPlacas = jsonObject.getString("placa_vehiculo");
                                String strColor = jsonObject.getString("color_vehiculo");
                                String strFirecode = jsonObject.getString("firecode");
                                String strSesion = jsonObject.getString("id_sesion");
                                String strFecha = jsonObject.getString("fecha_registro");
                                String strLat = jsonObject.getString("lat");
                                String strLong = jsonObject.getString("lng");
                                String strActivo = jsonObject.getString("activo");
                                String strEmail = jsonObject.getString("email");
                                String strZona = jsonObject.getString("zona");
                                String strDireccion = jsonObject.getString("direccion");
                                String strNumeroLicencia = jsonObject.getString("numero_licencia");
                                String strVencimientoLicencia = jsonObject.getString("vencimiento_licencia");

                                editor.putString("id",strId);
                                editor.putString("nombre",strNombre);
                                editor.putString("apellido",strApellido);
                                editor.putString("android_id",strAndroid);
                                editor.putString("genero",strGenero);
                                editor.putString("password",strPassword);
                                editor.putString("telefono",strTelefono);
                                editor.putString("foto",strFoto);
                                editor.putString("marca_vehiculo",strVehiculo);
                                editor.putString("placa_vehiculo",strPlacas);
                                editor.putString("color_vehiculo",strColor);
                                editor.putString("firecode",strFirecode);
                                editor.putString("id_sesion",strSesion);
                                editor.putString("fecha_registro",strFecha);
                                editor.putString("lat",strLat);
                                editor.putString("long",strLong);
                                editor.putString("activo",strActivo);
                                editor.putString("email",strEmail);
                                editor.putString("zona",strZona);
                                editor.putString("direccion",strDireccion);
                                editor.putString("numero_licencia",strNumeroLicencia);
                                editor.putString("vencimiento_licencia",strVencimientoLicencia);
                                editor.apply();

                                Intent intent = new Intent(Login.this, MapsFuncDriver.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                Log.e("errorRespuesta", String.valueOf(e));
                            }
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
                map.put("android_id",android_id);
                map.put("telefono",intel);
                map.put("password",inpin);
                map.put("firecode",firecode);

                return map;
            }
        };
        requestQueue.add(request);
    }

    /*private void enviarLoginJSON() {
        final String datosJSON = "{"+
                "\"id_conductor\":"+"\""+id+"\","+
                "\"telefono\":"+"\""+intel+"\","+
                "\"pin\":"+"\""+inpin+"\","+
                "\"firecode\":"+"\""+firecode+"\""+
                "}";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.3:8888/pruebaJSONVolley.php",
        StringRequest request = new StringRequest(Request.Method.POST, SERVIDOR_CONTROLADOR+"accesochofer",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("EnviarRes",response);
                        if(response.contains("success")){
                            Intent pantallaso = new Intent(Login.this,MapsFuncDriver.class);
                            startActivity(pantallaso);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EnviarError",""+error);
            }
        }) {
            @Override
            public String getBodyContentType(){return "application/json; charset=utf-8";}

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return datosJSON == null ? null : datosJSON.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("EnviarErrorEncoding","no soporta codificacion JSON");
                    return null;
                }
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                1500,//time out in 10second
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }*/
}