package com.netsec.taxiplusdriver;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MapsFuncDriver extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PERMISO_LOCATION = 123;
    private double latitud = 0.0;
    private double longitud = 0.0;
    private double latUpdate = 0.0;
    private double longUpdate = 0.0;
    private String direccion = null;
    private SharedPreferences sharedPreferences,preferencias,datosViaje;
    private SharedPreferences.Editor editorViaje,editorConductor;
    private Context context;
    private LatLng latLong;
    private Marker marker;
    private int markZoom = 0;
    private Activity activity;
    private SegundoPlano segundoPlano;
    private SegundoPlanoViajeId segundoPlanoViajeId;
    private static final String SERVIDOR_CONTROLADOR = new Servidor().servidor;
    private String id_sesion,android_id,solicitud_viaje,id_usuario,estado_viaje,tiempo_actualizacion,ver_ruta,id_viaje,pasajero1,pasajero2,pasajero3,pasajero4;
    private ImageView btnConductor,btnHistorial;
    private LinearLayout portadaEspera,capaCerrar;
    private ConstraintLayout capaSolicitud,capaDestino,capaLlegada,capaViaje,alerta,alertaPasajero;
    private TextView tvEmpresa, tvRuta,labelRuta,mensaje,mensajePasajeros;
    private Button btnAceptar, btnRechazar,btnCancelar,btnSeguir,btnCancelarViaje,btnCancelarDos,btnCancelarTres,btnComenzarViaje,btnCompletado1,btnCompletado2,btnCompletado3,btnCompletado4,btnSiPAsajero,btnNoPasajero;
    private SegundoPlanoParametros  segundoPlanoParametros;
    private LocationManager locationManager;
    private String strRuta, strLat, strLong, strEmpresa, strEstado;
    private SegundoPlanoCancelar segundoPlanoCancelar;
    private int completado = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /////// Requerimientos para pantalla completa
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        solicitud_viaje=intent.getStringExtra("id_viaje");
        //Log.e("id_solicitud","  -- "+solicitud_viaje);
        activity =this;
        // Creamos espacio en memoria para shared preferences
        sharedPreferences = getSharedPreferences("DatosConductor", context.MODE_PRIVATE);
        preferencias = getSharedPreferences("Conductor", context.MODE_PRIVATE);
        datosViaje = getSharedPreferences("Viaje", context.MODE_PRIVATE);
        editorViaje=datosViaje.edit();
        editorConductor=preferencias.edit();

        estado_viaje=preferencias.getString("estado","inactivo");

        tiempo_actualizacion=datosViaje.getString("tiempo_actualizacion","20000");
        ver_ruta=datosViaje.getString("ver_ruta","1");

        id_usuario=preferencias.getString("id"," ");
        id_sesion=preferencias.getString("id_sesion"," ");
        android_id=preferencias.getString("android_id"," ");

        segundoPlanoParametros=new SegundoPlanoParametros();
        segundoPlanoParametros.execute();

        btnConductor=findViewById(R.id.btnConductor);
        btnHistorial=findViewById(R.id.btnHistorial);


        capaSolicitud=findViewById(R.id.capaSolicitud);
        portadaEspera=findViewById(R.id.portadaEspera);
        capaDestino=findViewById(R.id.capaDestino);
        capaLlegada=findViewById(R.id.capaLlegada);
        capaViaje=findViewById(R.id.capaViaje);
        alerta=findViewById(R.id.alerta);
        alertaPasajero=findViewById(R.id.alertaPasajero);

        tvEmpresa=findViewById(R.id.puntoPartida);
        tvRuta=findViewById(R.id.ruta);
        labelRuta=findViewById(R.id.labelRuta);

        btnAceptar=findViewById(R.id.btnAceptar);
        btnRechazar=findViewById(R.id.btnRechazar);
        btnCancelar=findViewById(R.id.btnCancelar);
        btnSeguir=findViewById(R.id.btnSeguir);
        btnCancelarViaje=findViewById(R.id.btnCancelarViaje);
        btnCancelarDos=findViewById(R.id.btnCancelarDos);
        btnCancelarTres=findViewById(R.id.btnCancelarTres);
        btnComenzarViaje=findViewById(R.id.btnComenzarViaje);

        btnSiPAsajero=findViewById(R.id.btnSiPasajero);
        btnNoPasajero=findViewById(R.id.btnNoPasajero);

        btnCompletado1=findViewById(R.id.btnCompletado1);
        btnCompletado2=findViewById(R.id.btnCompletado2);
        btnCompletado3=findViewById(R.id.btnCompletado3);
        btnCompletado4=findViewById(R.id.btnCompletado4);

        mensaje=findViewById(R.id.mensaje);
        mensajePasajeros=findViewById(R.id.mensaje);
        mensajePasajeros=findViewById(R.id.mensajePasajero);
        capaCerrar=findViewById(R.id.capaCerrar);

        //Creamos soporte para fragmento de mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Pedimos permisos
        final int permisoLocacion = ContextCompat.checkSelfPermission(MapsFuncDriver.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permisoLocacion!= PackageManager.PERMISSION_GRANTED)
        {
            solicitarPermisoLocation();
        }

        btnConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conductor=new Intent(MapsFuncDriver.this,Conductor.class);
                startActivity(conductor);
            }
        });

        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conductor=new Intent(MapsFuncDriver.this,Historial.class);
                startActivity(conductor);
            }
        });


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conductor=new Intent(MapsFuncDriver.this,Viaje.class);
                startActivity(conductor);
                editorViaje.putString("estado","aceptado");
                editorViaje.apply();
            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conductor=new Intent(MapsFuncDriver.this,Viaje.class);
                startActivity(conductor);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.setVisibility(View.VISIBLE);
            }
        });

        btnCancelarDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.setVisibility(View.VISIBLE);
            }
        });

        btnCancelarTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.setVisibility(View.VISIBLE);
            }
        });

        btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.setVisibility(View.GONE);
            }
        });

        btnCancelarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capaCerrar.setVisibility(View.GONE);
                mensaje.setVisibility(View.VISIBLE);
                segundoPlanoCancelar=new SegundoPlanoCancelar();
                segundoPlanoCancelar.execute();
            }
        });

        btnComenzarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorViaje.putString("estado","iniciado").apply();

                portadaEspera.setVisibility(View.GONE);
                capaSolicitud.setVisibility(View.GONE);
                capaDestino.setVisibility(View.GONE);
                capaLlegada.setVisibility(View.GONE);
                capaViaje.setVisibility(View.VISIBLE);
                String strPasajeros = datosViaje.getString("nombre_pasajeros"," ");
                String[] pedazos = strPasajeros.split(" //\\*// ");

                for (int i =0;i < pedazos.length;i++){
                    if (pedazos[i]!=null){
                        if(!pedazos[i].trim().equals("")){
                            if(i==0){
                                btnCompletado1.setVisibility(View.VISIBLE);
                                btnCompletado1.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                            }
                            if(i==1){
                                btnCompletado2.setVisibility(View.VISIBLE);
                                btnCompletado2.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                            }
                            if(i==2){
                                btnCompletado3.setVisibility(View.VISIBLE);
                                btnCompletado3.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                            }
                            if(i==3){
                                btnCompletado4.setVisibility(View.VISIBLE);
                                btnCompletado4.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                            }
                        }
                    }
                }

            }
        });

        btnCompletado1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //capaLlegada.setVisibility(View.GONE);
                completado=1;
                String valorPasajero = btnCompletado1.getText().toString().trim();
                alertaPasajero.setVisibility(View.VISIBLE);
                mensajePasajeros.setText(valorPasajero);
            }
        });

        btnCompletado2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //capaLlegada.setVisibility(View.GONE);
                completado=2;
                String valorPasajero = btnCompletado2.getText().toString().trim();
                alertaPasajero.setVisibility(View.VISIBLE);
                mensajePasajeros.setText(valorPasajero);
            }
        });

        btnCompletado3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //capaLlegada.setVisibility(View.GONE);
                completado=3;
                String valorPasajero = btnCompletado3.getText().toString().trim();
                alertaPasajero.setVisibility(View.VISIBLE);
                mensajePasajeros.setText(valorPasajero);
            }
        });

        btnCompletado4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //capaLlegada.setVisibility(View.GONE);
                completado=4;
                String valorPasajero = btnCompletado4.getText().toString().trim();
                alertaPasajero.setVisibility(View.VISIBLE);
                mensajePasajeros.setText(valorPasajero);
            }
        });

        btnSiPAsajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String valorBtn = "";
                alertaPasajero.setVisibility(View.GONE);
                if (completado==1){
                    btnCompletado1.setVisibility(View.GONE);
                    valorBtn = btnCompletado1.getText().toString().trim();
                }
                if (completado==2){
                    btnCompletado2.setVisibility(View.GONE);
                    valorBtn = btnCompletado2.getText().toString().trim();
                }
                if (completado==3){
                    btnCompletado3.setVisibility(View.GONE);
                    valorBtn = btnCompletado3.getText().toString().trim();
                }
                if (completado==4){
                    btnCompletado4.setVisibility(View.GONE);
                    valorBtn = btnCompletado4.getText().toString().trim();
                }

                valorBtn=valorBtn.replace("COMPLETAR EL VIAJE DE","");
                String strPasajeros = datosViaje.getString("nombre_pasajeros"," ");
                strPasajeros=strPasajeros.toUpperCase().trim();
                valorBtn=valorBtn.toUpperCase();

                Log.e("nombres","-- "+valorBtn.trim());

                if(strPasajeros.contains(valorBtn.trim())){
                    strPasajeros=strPasajeros.replace(valorBtn.toUpperCase().trim()+" //*//","");
                }

                Log.e("nombres","-- "+strPasajeros);

                if(strPasajeros.trim().equals(""))
                {
                    strPasajeros=" ";
                    datosViaje.edit().putString("nombre_pasajeros",strPasajeros).apply();
                    Intent intent2 =new Intent(MapsFuncDriver.this,Concluido.class);
                    startActivity(intent2);
                }
                else{
                    datosViaje.edit().putString("nombre_pasajeros",strPasajeros.trim()).apply();
                }
            }
        });

        btnNoPasajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaPasajero.setVisibility(View.GONE);
            }
        });
    }

    /**
     IMPORTANTE !!!!!!!!!!!!!!!!!!!!ESTA FUNCION SE EJECUTA CUANDO EL MAPA SE CARGA!!!!!!
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        checarPermisos();
        miLatLong();

        LatLng enfermeraLL = new LatLng(latLong.latitude-0.0021,latLong.longitude);
        Marker marcadorEnfermera = mMap.addMarker(new MarkerOptions()
                .position(enfermeraLL)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("ubicacion_cov", 70, 70))));

        LatLng enfermeraLL2 = new LatLng(latLong.latitude-0.0031,latLong.longitude);
        Marker marcadorEnfermera2 = mMap.addMarker(new MarkerOptions()
                .position(enfermeraLL2)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("ubicacion_cov", 70, 70))));

        LatLng enfermeraLL3 = new LatLng(latLong.latitude-0.0031,latLong.longitude-0.0003);
        Marker marcadorEnfermera3 = mMap.addMarker(new MarkerOptions()
                .position(enfermeraLL3)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("ubicacion_cov", 70, 70))));



        marcadorEnfermera.setZIndex(0);
        marcadorEnfermera2.setZIndex(0);
        marcadorEnfermera3.setZIndex(0);

        if (solicitud_viaje!=null){
            portadaEspera.setVisibility(View.GONE);
            capaSolicitud.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(),"idViaje: "+solicitud_viaje,Toast.LENGTH_LONG).show();
            segundoPlano = new SegundoPlano();
            segundoPlano.execute();
            segundoPlanoViajeId=new SegundoPlanoViajeId();
            segundoPlanoViajeId.execute();
        }
        else{
            id_viaje = datosViaje.getString("id"," ");
            strRuta = datosViaje.getString("ruta"," ");
            strLat = datosViaje.getString("lat"," ");
            strLong = datosViaje.getString("lng"," ");
            strEmpresa = datosViaje.getString("empresa"," ");
            strEstado = datosViaje.getString("estado"," ");
            String strPasajeros = datosViaje.getString("nombre_pasajeros"," ");

            //Log.e("id_Vije",id_viaje);

            if (!strRuta.equals(" ")){
                Log.e("strEstado",strEstado);
                if (strEstado.equals("solicitado")){
                    portadaEspera.setVisibility(View.GONE);
                    capaSolicitud.setVisibility(View.VISIBLE);

                    tvEmpresa.setText(strEmpresa);
                    tvRuta.setText(strRuta);
                }
                if (strEstado.equals("destino")){
                    portadaEspera.setVisibility(View.GONE);
                    capaSolicitud.setVisibility(View.GONE);
                    capaDestino.setVisibility(View.VISIBLE);
                }
                if (strEstado.equals("iniciado")){
                    portadaEspera.setVisibility(View.GONE);
                    capaSolicitud.setVisibility(View.GONE);
                    capaDestino.setVisibility(View.GONE);
                    capaLlegada.setVisibility(View.GONE);
                    capaViaje.setVisibility(View.VISIBLE);
                    Log.e("","");
                    if (strPasajeros.contains("//*//")){
                        String[] pedazos = strPasajeros.split(" //\\*// ");
                        for (int i =0;i < pedazos.length;i++){
                            if (pedazos[i]!=null){
                                if(!pedazos[i].trim().equals("")){
                                    if(i==0){
                                        btnCompletado1.setVisibility(View.VISIBLE);
                                        btnCompletado1.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                                    }
                                    if(i==1){
                                        btnCompletado2.setVisibility(View.VISIBLE);
                                        btnCompletado2.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                                    }
                                    if(i==2){
                                        btnCompletado3.setVisibility(View.VISIBLE);
                                        btnCompletado3.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                                    }
                                    if(i==3){
                                        btnCompletado4.setVisibility(View.VISIBLE);
                                        btnCompletado4.setText("COMPLETAR EL VIAJE DE "+pedazos[i].trim().toUpperCase());
                                    }
                                }
                            }
                        }
                    }
                    else{
                        if(strPasajeros.trim().equals("")){
                            Intent intent=new Intent(MapsFuncDriver.this,Concluido.class);
                            startActivity(intent);
                        }
                    }
                }

                LatLng punto = new LatLng(Double.parseDouble(strLat),Double.parseDouble(strLong));
                mMap.addMarker(new MarkerOptions()
                        .position(punto)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("icono_ubicacion", 70, 70))));
                CameraUpdate lugarInicio = CameraUpdateFactory.newLatLngZoom(punto, 18);
                mMap.animateCamera(lugarInicio);
                miLatLong();
            }
        }
    }


    public void checarPermisos()
    {
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Intent irPermisos=new Intent(MapsFuncDriver.this,ActivarPermisos.class);
            startActivity(irPermisos);
        }
    }

    private void solicitarPermisoLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISO_LOCATION);
    }

    private void miLatLong(){

        /** SE PIDEN PERMISOS DE LOCACIÓN PARA*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /** MANAGER DE LOCACIONES DE ANDROID*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Long.parseLong(tiempo_actualizacion), 0, locationListener);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            agregarMarcadorUbicacion(latitud, longitud);
            direccion = darDireccion(this, latitud, longitud);

            //Toast.makeText(getApplicationContext(),"direccion: "+direccion,Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(),"lat: "+latitud+"long:"+longitud,Toast.LENGTH_LONG).show();

            Context context = this;
            SharedPreferences preferencias = getSharedPreferences("DatosConductor", context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("latitud", String.valueOf(latitud));
            editor.putString("longitud", String.valueOf(longitud));
            editor.putString("direccion", "" + direccion);
            editor.apply();

            if (latitud!=0){
                segundoPlano = new SegundoPlano();
                segundoPlano.execute();
            }
        }
    }


    //Escucha de ubicación
    LocationListener locationListener = new LocationListener() {

        //Cuando la ubicación cambia
        @Override
        public void onLocationChanged(Location location) {

            actualizarUbicacion(location);
            Location locationA = new Location("Primera");

            latUpdate = location.getLatitude();
            longUpdate = location.getLongitude();

            locationA.setLatitude(latUpdate);
            locationA.setLongitude(longUpdate);

            Location locationB = new Location("point B");

            String latitudX = sharedPreferences.getString("latitud", "no");
            String longitudX = sharedPreferences.getString("longitud", "no");

            if (!latitudX.equals("no") && !longitudX.equals("no")) {
                locationB.setLatitude(Double.parseDouble(latitudX));
                locationB.setLongitude(Double.parseDouble(longitudX));
                float distance = locationA.distanceTo(locationB);
                if (distance >= 3)//era 10 antes
                {
                    mMap.clear();
                    miLatLong();
                }
                strLat = datosViaje.getString("lat"," ");
                strLong = datosViaje.getString("lng"," ");
                strEmpresa = datosViaje.getString("empresa"," ");
                strEstado = datosViaje.getString("estado"," ");

                if (strEstado.equals("destino")){
                    Location locationC = new Location("point B");
                    locationC.setLatitude(Double.parseDouble(strLat));
                    locationC.setLongitude(Double.parseDouble(strLong));

                    float distanciaDestino = locationC.distanceTo(locationA);
                    Log.e("distancia",""+distanciaDestino);
                    if (distanciaDestino < 70)//distancia en metros a punto de partida
                    {
                        //Log.e("distancia","es menor a 1");
                        //if ()
                        capaDestino.setVisibility(View.GONE);
                        capaLlegada.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void agregarMarcadorUbicacion(double latitud, double longitud) {

        latLong = new LatLng(latitud, longitud);
        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLong)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("globo_1", 90, 90))));
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLong, 18);
        mMap.animateCamera(miUbicacion);



        marker.setZIndex(0);
    }

    public String darDireccion(Context ctx, double darLat, double darLong) {
        String fullDireccion = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> direcciones = geocoder.getFromLocation(darLat, darLong, 1);
            if (direcciones.size() > 0) {
                Address direccion = direcciones.get(0);
                fullDireccion = direccion.getAddressLine(0);
                String ciudad = direccion.getLocality();
                String pueblo = direccion.getCountryName();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fullDireccion;
    }

    private class SegundoPlano extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            enviarCoordenadas();
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

    public void enviarCoordenadas()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"rastreo_conductor.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("RastreoRes",response);
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
                map.put("id_sesion",id_sesion);
                map.put("lat", String.valueOf(latitud));
                map.put("long", String.valueOf(longitud));
                map.put("estado", estado_viaje);

                return map;
            }
        };
        requestQueue.add(request);
    }

    private class SegundoPlanoViajeId extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            enviarIdViaje();
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

    public void enviarIdViaje()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"pedir_datos_viaje.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ResPedir",response);
                        String limpio=response.replace("\\","");
                        //Log.e("ResPedirLimpio",limpio);
                        try {
                            JSONObject jsonObject = new JSONObject(limpio);
                            String strId = jsonObject.getString("id");
                            String strIdCaseta = jsonObject.getString("id_caseta");
                            String strRuta = jsonObject.getString("ruta");
                            String strNumPasajeros = jsonObject.getString("numero_pasajeros");
                            String strNombres = jsonObject.getString("nombre_pasajeros");
                            String strLat = jsonObject.getString("lat");
                            String strLong = jsonObject.getString("lng");
                            String strEmpresa = jsonObject.getString("empresa");

                            //Log.e("ResNombres",strNombres);

                            editorViaje.putString("id",strId);
                            editorViaje.putString("id_caseta",strIdCaseta);
                            editorViaje.putString("ruta",strRuta);
                            editorViaje.putString("numero_pasajeros",strNumPasajeros);
                            editorViaje.putString("nombre_pasajeros",strNombres);
                            editorViaje.putString("lat",strLat);
                            editorViaje.putString("lng",strLong);
                            editorViaje.putString("empresa",strEmpresa);
                            editorViaje.putString("estado","solicitado");
                            editorViaje.apply();

                            tvEmpresa.setText(strEmpresa);
                            tvRuta.setText(strRuta);

                            LatLng punto = new LatLng(Double.parseDouble(strLat),Double.parseDouble(strLong));
                            mMap.addMarker(new MarkerOptions()
                                    .position(punto)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("icono_ubicacion", 70, 70))));
                            CameraUpdate lugarInicio = CameraUpdateFactory.newLatLngZoom(punto, 18);
                            mMap.animateCamera(lugarInicio);



                        } catch (JSONException e) {
                            Log.e("errorRespuesta", String.valueOf(e));
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
                map.put("id_sesion",id_sesion);
                map.put("id_usuario",id_usuario);
                map.put("id_viaje", solicitud_viaje);

                return map;
            }
        };
        requestQueue.add(request);
    }

    private class SegundoPlanoParametros extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            pedirParametros();
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

    public void pedirParametros()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"pedir_parametros.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ResPedirParam",response);
                        //Log.e("ResPedirLimpio",limpio);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String strTiempo = jsonObject.getString("tiempo_actualizacion");
                            String strActivarRuta = jsonObject.getString("ruta");

                            //Log.e("ResNombres",strNombres);

                            editorViaje.putString("tiempo_actualizacion",strTiempo);
                            editorViaje.putString("ver_ruta",strActivarRuta);
                            editorViaje.apply();
                            if (strActivarRuta.equals("0")){
                                tvRuta.setVisibility(View.GONE);
                                labelRuta.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            Log.e("errorRespuesta", String.valueOf(e));
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
                return map;
            }
        };
        requestQueue.add(request);
    }

    private class SegundoPlanoCancelar extends AsyncTask<Void, Integer,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            cancelar_viaje();
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

    public void cancelar_viaje()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"cancelar_viaje.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("respuestaCancelar",response);
                        datosViaje.edit().clear().apply();
                        if (response.equals("success")){
                            Intent intent = new Intent(MapsFuncDriver.this,MapsFuncDriver.class);
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
                map.put("android_id",android_id);
                map.put("id_sesion",id_sesion);
                map.put("id_viaje",id_viaje);

                return map;
            }
        };
        requestQueue.add(request);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISO_LOCATION : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent reiniciar=new Intent(MapsFuncDriver.this,MapsFuncDriver.class);
                    startActivity(reiniciar);
                } else {
                    checarPermisos();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //FUNCIÓN PARA ADAPTAR IMAGENES DE MAPA (CARRO Y PUNTO DE UBICACIÓN)

    public Bitmap resizeBitmap(String drawableName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }
}