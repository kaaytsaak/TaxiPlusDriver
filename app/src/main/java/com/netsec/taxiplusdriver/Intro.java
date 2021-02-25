package com.netsec.taxiplusdriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Intro extends AppCompatActivity {

    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro);

        preferencias = getSharedPreferences("Conductor",this.MODE_PRIVATE);
        editor = preferencias.edit();

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String sesion = preferencias.getString("id_sesion"," ");
                if (!sesion.trim().equals("")){
                    //Intent pantallaso = new Intent(Intro.this,MapsFuncDriver.class);
                    Intent pantallaso = new Intent(Intro.this,Login.class);
                    startActivity(pantallaso);
                }
                else{
                    Intent pantallaso = new Intent(Intro.this,Login.class);
                    startActivity(pantallaso);
                }
            }
        },2000);
    }
}