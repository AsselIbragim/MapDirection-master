package map.cheena.kz.mapdirection.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Object implements Parcelable{
    private int id;
    private String name;
    private String desc;
    private String img;
    private double lat;
    private double longt;

    public Object() {
    }

    protected Object(Parcel in) {
        id = in.readInt();
        name = in.readString();
        desc = in.readString();
        img = in.readString();
        lat = in.readDouble();
        longt = in.readDouble();
    }

    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public Object createFromParcel(Parcel in) {
            return new Object(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongt() {
        return longt;
    }

    public void setLongt(double longt) {
        this.longt = longt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(img);
        dest.writeDouble(longt);
        dest.writeDouble(lat);
    }
}
