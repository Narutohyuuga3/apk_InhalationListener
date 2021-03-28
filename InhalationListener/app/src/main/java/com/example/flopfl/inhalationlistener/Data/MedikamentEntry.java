package com.example.flopfl.inhalationlistener.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;


//Einträge der Medikamente-Database
@Entity(tableName="Medikamente")
public class MedikamentEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private boolean notification;
    private String time1;
    private String time2;
    private String updatedAt;
    private String updatedAt2;
    private String updatedAt3;
    private String deviceName;
    private int tag;
    private boolean morning;
    private boolean evening;


    @Ignore
    public MedikamentEntry(String description, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, boolean notification, String time1, String time2, String updatedAt, String updatedAt2, String updatedAt3, String deviceName, int tag, boolean morning, boolean evening) {
        this.description = description;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.notification=notification;
        this.time1 = time1;
        this.time2 = time2;
        this.updatedAt = updatedAt;
        this.updatedAt2 = updatedAt2;
        this.updatedAt3 = updatedAt3;
        this.deviceName = deviceName;
        this.tag = tag;
        this.morning = morning;
        this.evening = evening;
    }

    public MedikamentEntry(int id, String description, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, boolean notification, String time1, String time2, String updatedAt, String updatedAt2, String updatedAt3, String deviceName, int tag, boolean morning, boolean evening) {
        this.id = id;
        this.description = description;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.notification=notification;
        this.time1 = time1;
        this.time2 = time2;
        this.updatedAt = updatedAt;
        this.updatedAt2 = updatedAt2;
        this.updatedAt3 = updatedAt3;
        this.deviceName = deviceName;
        this.tag = tag;
        this.morning = morning;
        this.evening = evening;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt2() {
        return updatedAt2;
    }

    public void setUpdatedAt2(String updatedAt2) {
        this.updatedAt2 = updatedAt2;
    }

    public String getUpdatedAt3() {
        return updatedAt3;
    }

    public void setUpdatedAt3(String updatedAt3) {
        this.updatedAt3 = updatedAt3;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isEvening() {
        return evening;
    }

    public void setEvening(boolean evening) {
        this.evening = evening;
    }
}