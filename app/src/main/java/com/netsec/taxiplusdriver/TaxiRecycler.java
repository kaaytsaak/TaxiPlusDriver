package com.netsec.taxiplusdriver;


import androidx.appcompat.app.AppCompatActivity;

public class TaxiRecycler extends AppCompatActivity {

    private String id_viaje,empresa,fecha,estado,termino;

    public TaxiRecycler(String id_travel, String date, String termino,String corp,String state)
    {

        this.id_viaje=id_travel;
        this.fecha=date;
        this.empresa=corp;
        this.estado=state;
        this.termino=termino;
    }


    public String getId_viaje() {
        return id_viaje;
    }
    public String getFecha() {
        return fecha;
    }
    public String getEmpresa() {
        return empresa;
    }
    public String getEstado() {
        return estado;
    }
    public String getTermino() {return termino;}

    public void setId_viaje(String id_viaje) {
        this.id_viaje = id_viaje;
    }
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setTermino(String termino) { this.termino = termino; }
}