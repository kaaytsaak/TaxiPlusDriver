package com.netsec.taxiplusdriver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterLista extends RecyclerView.Adapter<AdapterLista.ViewHolderRecycler>
{

    private ArrayList<TaxiRecycler> rankingrecycler;
    ViewHolderRecycler viewHolderRecycler;
    private  RecyclerView recyclerView;
    private Context context;
    private String id_viaje,empresa,estado,fecha,termino;

    public AdapterLista(ArrayList<TaxiRecycler> rankingrecycler)
    {
        this.rankingrecycler=rankingrecycler;
    }
    @Override
    public ViewHolderRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista,parent,false);
        context=parent.getContext();
        return new ViewHolderRecycler(vista);
    }

    @Override
    public void onBindViewHolder(final ViewHolderRecycler holder,final int position){
        viewHolderRecycler=holder;
        id_viaje= rankingrecycler.get(position).getId_viaje();
        fecha= rankingrecycler.get(position).getFecha();
        empresa= rankingrecycler.get(position).getEmpresa();
        estado= rankingrecycler.get(position).getEstado();
        termino= rankingrecycler.get(position).getTermino();


        holder.id_travel.setText("ID: "+id_viaje);
        holder.date.setText("Fecha: "+fecha);
        holder.corp.setText("Empresa: "+empresa);
        holder.state.setText("Estado: "+estado);

        if (!termino.trim().equals("")){
            holder.termino.setText("Termino: "+termino);
        }
        else{
            holder.cajaTermino.setVisibility(View.GONE);
        }

        //Uri uri = Uri.parse(photo);
        // holder.photo.setImageURI(uri);
    }
    @Override
    public int getItemCount(){
        return rankingrecycler.size();

    }
    public class ViewHolderRecycler extends RecyclerView.ViewHolder {


        TextView id_travel,date,corp,state,termino;
        LinearLayout cajaTermino;

        public ViewHolderRecycler(View itemView) {
            super(itemView);
            id_travel=(TextView) itemView.findViewById(R.id.id_viaje);
            date=(TextView)itemView.findViewById(R.id.fecha);
            corp=(TextView) itemView.findViewById(R.id.empresa);
            state=(TextView)itemView.findViewById(R.id.estado);
            termino=(TextView)itemView.findViewById(R.id.termino);
            cajaTermino=(LinearLayout) itemView.findViewById(R.id.cajaTermino);

        }
    }
}