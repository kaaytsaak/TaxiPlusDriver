package com.netsec.taxiplusdriver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

public class NotificacionPush extends FirebaseMessagingService{

    private String ID_CANAL;
    private int ID_NOTIFICACION,ID_PRINCIPAL;
    private String de, datos, cuerpo, titulo,idFinal,contadorIDS,uriNoticia;
    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;
    private int checkTitulo,checkayuda,checkOfrecido,checkRechzado,checkAceptado,checkCalif,checkSos,checkEnteradoSOS,checkNoticia;
    private Intent destino;
    private CharSequence nombre;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        preferencias = getSharedPreferences("Viaje",this.MODE_PRIVATE);
        editor=preferencias.edit();
        contadorIDS=preferencias.getString("contadorIDS","1");

        ID_CANAL="NOTIFICACIONES";
        ID_NOTIFICACION=99;
        ID_PRINCIPAL=99;

        nombre="Notificaciones";
        checkNoticia=0;

        Log.d(TAG, "de: " + remoteMessage.getFrom());
        de= remoteMessage.getFrom();

        // Check if message contains a data payload.
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Mensaje: " + remoteMessage.getData());
            datos=remoteMessage.getData().toString();
            //Log.e("datosPush",datos);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Cuerpo mensaje: " + remoteMessage.getNotification().getBody());
            cuerpo=remoteMessage.getNotification().getBody();
            titulo=remoteMessage.getNotification().getTitle();
        }
        crearCanal();

        crearNotificacion();
    }

    private void crearCanal()
    {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            NotificationChannel canal = new NotificationChannel(ID_CANAL, nombre, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(canal);
        }
    }

    private void crearNotificacion()
    {
        Log.e("fireCrear","creada");
        destino=new Intent(this,MapsFuncDriver.class);

        if (cuerpo.contains(" ID: ")){
            String[] partesSolicitud = cuerpo.split(" ID: ");
            Log.e("firePartes",partesSolicitud[1]);
            destino.putExtra("id_viaje",partesSolicitud[1]);
            editor.putString("id",partesSolicitud[1]);

            if (cuerpo.contains("USUARIO")){
                editor.putString("estado","solicitado_usuario");
            }else{
                editor.putString("estado","solicitado");
            }
            editor.apply();
        }

        destino.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, destino, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ID_CANAL);
        builder.setContentTitle(titulo);
        builder.setContentText(cuerpo);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        //builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(cuerpo));
        builder.setAutoCancel(false);
        //builder.setGroup("GROUP_"+ID_CANAL);
        //builder.setGroupSummary(true);
        builder.setChannelId(ID_CANAL);
        builder.setContentIntent(pendingIntent);
        builder.setColor(Color.TRANSPARENT);
        builder.setSmallIcon(R.drawable.icono_usuario);
        builder.setVibrate(new long[]{1000,6000,1000,6000,1000});

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        //notificationManagerCompat.notify(ID_PRINCIPAL,builder2.build());
        //int valorDado = (int) Math.floor(Math.random()*1000+200);
        notificationManagerCompat.notify(0,builder.build());

        destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(destino);
    }


}