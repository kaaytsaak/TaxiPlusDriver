package com.netsec.taxiplusdriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FireInstance extends FirebaseMessagingService {

    private String cookieSesion, cookieAndroid, cookieId, tokenFirecode;
    private static final String SERVIDOR_CONTROLADOR= new Servidor().servidor;
    private Context context;
    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;

    @Override
    public void onNewToken(String tokenStr) {
        super.onNewToken(tokenStr);
        tokenFirecode = tokenStr;
        Log.e("tokenFirecode",tokenFirecode);
        preferencias = getSharedPreferences("Conductor",this.MODE_PRIVATE);
        editor = preferencias.edit();
        cookieSesion=preferencias.getString("id_sesion"," ");
        cookieAndroid=preferencias.getString("android_id"," ");
        context=this;

        if (!cookieSesion.equals(" "))
        {
            actualizarToken();
        }
    }

    private void actualizarToken(){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SERVIDOR_CONTROLADOR+"actualizar_firecode.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("",response);
                        if (response.equals("actualizado"))
                        {
                            editor.putString("firecode",tokenFirecode);
                            editor.apply();
                        }
                        if (response.equals("noactualizo"))
                        {
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
                map.put("id_sesion", cookieSesion);
                map.put("android_id", cookieAndroid);
                map.put("firecode", tokenFirecode);

                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}