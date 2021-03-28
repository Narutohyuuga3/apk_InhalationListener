package com.example.flopfl.inhalationlistener.Data_Inhalation;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;


//Einträge Inhalations-Database
@Entity(tableName="Inhalationen")
public class InhalationEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int Innehalten;
    private int Inhalation;
    private int Schuetteln;
    private int Akku;
    private int exhalation;
    private String updatedAt;

    @Ignore
    public InhalationEntry( int Innehalten, int Inhalation, int Schuetteln, int Akku, int exhalation, String updatedAt) {
        this.Innehalten = Innehalten;
        this.Inhalation = Inhalation;
        this.Schuetteln = Schuetteln;
        this.Akku = Akku;
        this.exhalation=exhalation;
        this.updatedAt = updatedAt;
    }

    public InhalationEntry(int id, int Innehalten, int Inhalation, int Schuetteln, int Akku, int exhalation, String updatedAt) {
        this.id = id;
        this.Innehalten = Innehalten;
        this.Inhalation= Inhalation;
        this.Schuetteln = Schuetteln;
        this.Akku = Akku;
        this.exhalation=exhalation;
        this.updatedAt = updatedAt;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInnehalten() {
        return Innehalten;
    }

    public void setInnehalten(int innehalten) {
        Innehalten = innehalten;
    }

    public int getInhalation() {
        return Inhalation;
    }

    public void setInhalation(int inhalation) {
        Inhalation = inhalation;
    }

    public int getSchuetteln() {
        return Schuetteln;
    }

    public void setSchuetteln(int schuetteln) {
        Schuetteln = schuetteln;
    }

    public int getAkku() {
        return Akku;
    }

    public void setAkku(int akku) {
        Akku = akku;
    }

    public int getExhalation() {
        return exhalation;
    }

    public void setExhalation(int exhalation) {
        this.exhalation = exhalation;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
