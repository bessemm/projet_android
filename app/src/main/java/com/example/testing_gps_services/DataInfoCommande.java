package com.example.testing_gps_services;

public class DataInfoCommande {
 private  String idCommande ;
    private String nom;
    private String prenom;
    private String type;
    private String dateLiv;
    private  String telephone ;
    private  String Adresse ;

    public String getAdresse() {
        return Adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setAdresse(String adresse) {
        Adresse = adresse;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(String idCommande) {
        this.idCommande = idCommande;
    }

    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public String getType() {
        return type;
    }

    public String getDateLiv() {
        return dateLiv;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }



    public void setType(String type  ) {
        this.type = type;
    }

    public void setDateLiv(String idCommande) {
        this.dateLiv = idCommande;
    }

public DataInfoCommande(){

}
    public DataInfoCommande( String nom, String prenom, String idCommande, String type) {
        this.idCommande = idCommande;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
    }



    public DataInfoCommande(String idCommande, String nom, String prenom, String type, String dateLiv, String telephone, String adresse) {
        this.idCommande = idCommande;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.dateLiv = dateLiv;
        this.telephone = telephone;
        Adresse = adresse;
    }

    public String toString() {
        return "DataInfo{" +
                "nom=" + nom +
                ", prenom=" + prenom +
                ", id='" + dateLiv + '\'' +
                ", type='" + type + '\'' +

                '}';
    }

}


