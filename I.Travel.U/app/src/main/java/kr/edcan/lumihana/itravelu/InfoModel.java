package kr.edcan.lumihana.itravelu;

/**
 * Created by kimok_000 on 2016-10-30.
 */
public class InfoModel  {
    private double lat, lon;
    private String about;
    private String hours;
    private String photo;
    private String type;

    public InfoModel() {
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
}
