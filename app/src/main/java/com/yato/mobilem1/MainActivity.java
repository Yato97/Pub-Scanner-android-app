package com.yato.mobilem1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, OnGestureListener, OnDoubleTapListener {
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngineProvider;
    private Location pos;

    private ImageButton navButton;
    private ImageButton formButton;

    private LinearLayout popUp;
    private Button submit;
    private Button photo;
    private EditText promoC;
    private EditText offreC;
    private EditText timeC;
    private ImageButton switch1;
    private Boolean click = false;
    private GestureDetector gestureDetector;

    private ArrayList<Marker> myMarkers = new ArrayList<Marker>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        navButton = findViewById(R.id.nav);
        formButton = findViewById(R.id.openForm);
        popUp = findViewById(R.id.popUp);
        submit = findViewById(R.id.submit);
        photo = findViewById(R.id.photo);
        promoC = findViewById(R.id.promoC);
        offreC = findViewById(R.id.offreC);
        timeC = findViewById(R.id.timeC);
        switch1 = findViewById(R.id.switch1);

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!click) {
                    locationLayerPlugin.setCameraMode(CameraMode.TRACKING_COMPASS);
                }
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!click) {
                    displayPos();
                }
                else {
                    removePos();
                }
            }
        });


        formButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.setVisibility(View.VISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(promoC.getText().toString().equals("") && offreC.getText().toString().equals("") && timeC.getText().toString().equals(""))) {
                    myMarkers.add(new Marker(promoC.getText().toString(), offreC.getText().toString(), Integer.parseInt(timeC.getText().toString()),pos,map));
                    promoC.setText("");
                    offreC.setText("");
                    timeC.setText("");
                    popUp.setVisibility(View.INVISIBLE);
                }
            }
        });

        gestureDetector = new GestureDetector(this, this);
        gestureDetector.setOnDoubleTapListener(this);

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        mapView.setStyleUrl("mapbox://styles/yato97/ckwoy24741k0y15r0e1t9jdfq");
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();

        LatLng pos1 = new LatLng(-20.910938644003053, 55.477425444743005);
        Location log1 = new Location("pos1");
        log1.setLatitude(pos1.getLatitude());
        log1.setLongitude(pos1.getLongitude());
        LatLng pos2 = new LatLng(-20.91141389499001, 55.47683988498744);
        Location log2 = new Location("pos2");
        log2.setLatitude(pos2.getLatitude());
        log2.setLongitude(pos2.getLongitude());
        LatLng pos3 = new LatLng(-20.911478422444137, 55.47752374351245);
        Location log3 = new Location("pos3");
        log3.setLatitude(pos3.getLatitude());
        log3.setLongitude(pos3.getLongitude());




        myMarkers.add(new Marker("AmericanDream", "Tee-shirt gun&roses -20%", 2, log1, map));
        myMarkers.add(new Marker("AdventureLand", "Casquette camel -3%", 4, log2, map));
        myMarkers.add(new Marker("Zara", "Pantalons guccy -5%", 1, log3, map));
    }


    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initLocation();
            initLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        locationEngineProvider = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        map.getUiSettings().setScrollGesturesEnabled(false);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            pos = lastLocation;
            setCamPos(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void displayPos() {
        locationLayerPlugin.setCameraMode(CameraMode.NONE);
        map.animateCamera(CameraUpdateFactory.tiltTo(0), 1000, new MapboxMap.CancelableCallback() {
            @Override
            public void onFinish() {
                CameraUpdate animateOut = CameraUpdateFactory.zoomTo(16);
                map.animateCamera(animateOut);
            }
            @Override
            public void onCancel() {
            }
        });
        click = true;
    }
    private  void removePos() {
        map.animateCamera(CameraUpdateFactory.zoomTo(19.0), new MapboxMap.CancelableCallback() {
            @Override
            public void onFinish() {
                CameraUpdate animateOut = CameraUpdateFactory.tiltTo(60.0);
                map.animateCamera(animateOut, 1000);
                click = false;
            }
            @Override
            public void onCancel() {
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void initLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING_COMPASS);
        locationLayerPlugin.setRenderMode(RenderMode.COMPASS);
    }

    private void setCamPos(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            pos = location;
            setCamPos(location);
        }
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist;
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        for (int i = 0; i < myMarkers.size(); i++) {
            if ((distFrom(pos.getLatitude(), pos.getLongitude(), myMarkers.get(i).location.getLatitude(), myMarkers.get(i).location.getLongitude()) < 0.043)) {
                myMarkers.get(i).addMarker(map,myMarkers.get(i).getOptions());
            }
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}