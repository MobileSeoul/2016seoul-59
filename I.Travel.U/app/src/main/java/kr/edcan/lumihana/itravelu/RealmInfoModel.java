package kr.edcan.lumihana.itravelu;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by kimok_000 on 2016-10-30.
 */
@RealmClass
public class RealmInfoModel extends RealmObject implements RealmModel {
    @PrimaryKey
    private String name;
    private String tag;

    private double lat, lon;
    private String about;
    private String hours;
    private String photo;
    private String type;
    private double distanceFromMe;

    public RealmInfoModel() {
    }

    public RealmInfoModel(String name, String tag, InfoModel infoModel) {
        this.name = name;
        this.tag = tag;
        this.lat = infoModel.getLat();
        this.lon = infoModel.getLong();
        this.about = infoModel.getAbout();
        this.hours = infoModel.getHours();
        this.photo = infoModel.getPhoto();
        this.type = infoModel.getType();
    }

    public double getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(double distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public double getLong() {
        return lon;
    }

    public String getAbout() {
        return about;
    }

    public String getHours() {
        return hours;
    }

    public String getPhoto() {
        return photo;
    }

    public String getType() {
        return type;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLong(double lon) {
        this.lon = lon;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setType(String type) {
        this.type = type;
    }
}
