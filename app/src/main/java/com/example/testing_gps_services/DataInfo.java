package com.example.testing_gps_services;

public class DataInfo {
  private Double lat;
  private Double log;
  private String titre;
  private String desc;
  private  int idlient;


    public Double getLat() {
        return lat;
    }

    public Double getLog() {
        return log;
    }

    public String getTitre() {
        return titre;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLog(Double log) {
        this.log = log;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }



    public DataInfo(Double lat, Double log, String titre, String desc, int idCommande) {
        this.lat = lat;
        this.log = log;
        this.titre = titre;
        this.desc = desc;
        this.idlient = idCommande;
    }

    public int getIdclient() {
        return idlient;
    }

    public void setIdclient(int idlient) {
        this.idlient = idlient;
    }

    @Override
    public String toString() {
        return "DataInfo{" +
                "lat=" + lat +
                ", log=" + log +
                ", titre='" + titre + '\'' +
                ", desc='" + desc + '\'' +
                ", idCommande=" + idlient +
                '}';
    }
}
