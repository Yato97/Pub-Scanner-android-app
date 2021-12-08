package com.yato.mobilem1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.progressindicator.LinearProgressIndicator;
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
import com.ramotion.circlemenu.CircleMenuView;

import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, OnGestureListener, OnDoubleTapListener, SensorEventListener {
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngineProvider;
    private Location pos;

    private ProgressBar progressBar;

    private LinearLayout popUp;
    private Button submit;
    private Button photo;
    private EditText promoC;
    private EditText offreC;
    private EditText timeC;
    private Boolean click = false;
    private GestureDetector gestureDetector;

    private ArrayList<Marker> myMarkers = new ArrayList<Marker>();

    private Sensor mAccelerometer;
    private SensorManager manager;
    private boolean accelSupported;

    private CircleMenuView circleMenuView;
    private CardView cardV;

    private ImageView imageView;
    private LinearLayout scan;
    private Bitmap bitmap;
    private boolean isRuning = false;

    private double ax,ay;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        popUp = findViewById(R.id.popUp);
        submit = findViewById(R.id.submit);
        photo = findViewById(R.id.photo);
        promoC = findViewById(R.id.promoC);
        offreC = findViewById(R.id.offreC);
        timeC = findViewById(R.id.timeC);
        progressBar = findViewById(R.id.progressBar);
        circleMenuView = findViewById(R.id.circleMenu);
        imageView = (ImageView)findViewById(R.id.imageCapt);
        cardV = findViewById(R.id.cardV);
        scan = findViewById(R.id.scan);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(promoC.getText().toString().equals("") && offreC.getText().toString().equals("") && timeC.getText().toString().equals(""))) {
                    myMarkers.add(new Marker(promoC.getText().toString(), offreC.getText().toString(), Integer.parseInt(timeC.getText().toString()),pos,map,bitmap));
                    promoC.setText("");
                    offreC.setText("");
                    timeC.setText("");
                    imageView.setImageBitmap(null);
                    bitmap = null;
                    popUp.setVisibility(View.INVISIBLE);
                    cardV.setVisibility(View.INVISIBLE);
                }
            }
        });

        popUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        circleMenuView.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
                super.onMenuOpenAnimationStart(view);
            }

            @Override
            public void onMenuOpenAnimationEnd(@NonNull CircleMenuView view) {
                super.onMenuOpenAnimationEnd(view);
            }

            @Override
            public void onMenuCloseAnimationStart(@NonNull CircleMenuView view) {
                super.onMenuCloseAnimationStart(view);
            }

            @Override
            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView view) {
                super.onMenuCloseAnimationEnd(view);
            }

            @Override
            public void onButtonClickAnimationStart(@NonNull CircleMenuView view, int buttonIndex) {
                super.onButtonClickAnimationStart(view, buttonIndex);
            }

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
                super.onButtonClickAnimationEnd(view, buttonIndex);
                switch (buttonIndex) {
                    case 0:
                        if (!click) {
                            locationLayerPlugin.setCameraMode(CameraMode.TRACKING_COMPASS);
                        }
                        break;

                    case 1:
                        popUp.setVisibility(View.VISIBLE);
                        cardV.setVisibility(View.VISIBLE);
                        break;

                    case 2:
                        if (!click) {
                            displayPos();
                        }
                        else {
                            removePos();
                        }
                        break;
                }
            }

            @Override
            public boolean onButtonLongClick(@NonNull CircleMenuView view, int buttonIndex) {
                return super.onButtonLongClick(view, buttonIndex);
            }

            @Override
            public void onButtonLongClickAnimationStart(@NonNull CircleMenuView view, int buttonIndex) {
                super.onButtonLongClickAnimationStart(view, buttonIndex);
            }

            @Override
            public void onButtonLongClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
                super.onButtonLongClickAnimationEnd(view, buttonIndex);
            }
        });

        gestureDetector = new GestureDetector(this, this);


        manager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        mAccelerometer = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.CAMERA
            }, 100);
        }
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });


        mapView.setStyleUrl("mapbox://styles/yato97/ckwoy24741k0y15r0e1t9jdfq");
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();

        map.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.customdisplay, null);

                TextView titre = (TextView) v.findViewById(R.id.title);
                TextView snippset = (TextView) v.findViewById(R.id.snippet);
                ImageView imgDisplay = (ImageView) v.findViewById(R.id.imageDisplay);
                for (int i = 0; i < myMarkers.size(); i++) {
                    if (myMarkers.get(i).getOptions().getSnippet() == marker.getSnippet() && myMarkers.get(i).getOptions().getTitle() == marker.getTitle()) {
                        imgDisplay.setImageBitmap(myMarkers.get(i).getImg());
                        System.out.println("ATTENTION =========");
                    }
                }

                titre.setText(marker.getTitle());
                snippset.setText(marker.getSnippet());

                v.setPadding(0, 0, 0, 30);
                return v;
            }
        });
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
        manager.unregisterListener(this, mAccelerometer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        accelSupported = manager.registerListener(
                this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
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
        popUp.setVisibility(View.INVISIBLE);
        cardV.setVisibility(View.INVISIBLE);
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
        ValueAnimator animH = ValueAnimator.ofInt(scan.getMeasuredHeight(), +700);
        ValueAnimator animW = ValueAnimator.ofInt(scan.getMeasuredWidth(), +700);
        ValueAnimator animHEnd = ValueAnimator.ofInt(scan.getMeasuredHeight(), 0);
        ValueAnimator animWEnd = ValueAnimator.ofInt(scan.getMeasuredWidth(), 0);
        animH.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int valH = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = scan.getLayoutParams();
                layoutParams.height = valH;
                scan.setLayoutParams(layoutParams);
                if (scan.getHeight() < 699) {
                    isRuning = true;
                }
                else {
                    isRuning = false;
                    showMarker();
                }
                System.out.println(scan.getHeight()+"Bool = "+ isRuning);
            }
        });
        animW.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int valW = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = scan.getLayoutParams();
                layoutParams.width = valW;
                scan.setLayoutParams(layoutParams);

            }

        });
        animHEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int valW = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = scan.getLayoutParams();
                layoutParams.height = valW;
                scan.setLayoutParams(layoutParams);
            }

        });
        animWEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int valW = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = scan.getLayoutParams();
                layoutParams.width = valW;
                scan.setLayoutParams(layoutParams);
            }

        });
        if (!click) {
            animH.setDuration(1000);
            animH.start();
            animW.setDuration(1000);
            animW.start();

            animHEnd.setStartDelay(1000);
            animWEnd.setStartDelay(1000);
            animHEnd.start();
            animWEnd.start();
            ViewGroup.LayoutParams layoutParams = scan.getLayoutParams();

        }

    }

    private void showMarker() {
        if (!isRuning) {
            for (int i = 0; i < myMarkers.size(); i++) {
                if ((distFrom(pos.getLatitude(), pos.getLongitude(), myMarkers.get(i).location.getLatitude(), myMarkers.get(i).location.getLongitude()) < 0.043)) {
                    myMarkers.get(i).addMarker(map,myMarkers.get(i).getOptions());
                }
            }
        }
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION) {
            ax=event.values[0];
            ay=event.values[1];
            int e = (int)((ax*ay)/2);
            progressBar.setMax(50);

            progressBar.setProgress(e, true);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}