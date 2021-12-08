package com.yato.mobilem1;


import android.graphics.Bitmap;
import android.location.Location;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Marker {
    private String titre;
    private String offre;
    private Integer duration;
    private MarkerOptions options;
    private Bitmap img;
    Location location;

    public Marker(String titre, String offre, Integer duration, Location location, MapboxMap map, Bitmap img) {
        this.titre = titre;
        this.offre = offre;
        this.duration = duration;
        this.location = location;
        this.img = img;
        Date aujourdhui = new Date();

        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT);

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        this.options = new MarkerOptions()
                .position(latLng)
                .title(this.titre)
                .snippet("Offre : "+offre+ "\n" + "Date d'ajout : "+shortDateFormat.format(aujourdhui) +"\n"+"Temps disponible : "+duration+ "h");
    }

    public void addMarker(MapboxMap map, MarkerOptions opt) {
        map.addMarker(opt);

    }

    public MarkerOptions getOptions() {
        return options;
    }

    public Bitmap getImg() {
        return img;
    }
}
