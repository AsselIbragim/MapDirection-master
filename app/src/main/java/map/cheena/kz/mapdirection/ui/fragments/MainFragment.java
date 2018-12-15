package map.cheena.kz.mapdirection.ui.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import map.cheena.kz.mapdirection.R;
import map.cheena.kz.mapdirection.entities.Object;
import map.cheena.kz.mapdirection.utils.Constants;
import map.cheena.kz.mapdirection.utils.Utility;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback, DirectionCallback {
    public static final String TAG = Constants.MAINFRAGMENT;
    public static final int REQUEST_GPS_PERMISSION = 123;

    public MapView mapView;
    private GoogleMap map;
    private Marker mPositionMarker, objectMarker;
    private LinearLayout selectView;

    private int drawerCounter = 0;
    private LatLng myLocation, destination;
    private Object selectedObject;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        mapView = view.findViewById(R.id.fm_mapview);
        selectView = view.findViewById(R.id.fm_select_view);

        selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                ObjectsFragment objectsFragment = new ObjectsFragment();
                objectsFragment.setTargetFragment(MainFragment.this, 1);
                fragmentTransaction.replace(R.id.main_frame, objectsFragment, ObjectsFragment.TAG);
                fragmentTransaction.addToBackStack(ObjectsFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        if (Utility.checkGPSPermission(getContext())){
            mapView.getMapAsync(this);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(Utility.checkGPSPermission(getContext())){
            this.map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    drawMarker(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });
        }else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }
    }


    private void drawMarker(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (drawerCounter == 0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 15);
            map.animateCamera(cameraUpdate);
        }

        if (mPositionMarker != null) {
            mPositionMarker.remove();
        }
        mPositionMarker = map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location))
                .title("Вы тут!"));

        drawerCounter += 1;
    }

    private void drawDirection(Object object){
        map.clear();
        destination = new LatLng(object.getLat(), object.getLongt());
        mPositionMarker = map.addMarker(new MarkerOptions()
                .position(destination)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                .title(object.getName()));

        sendRequest(myLocation, destination);
    }

    private void sendRequest(LatLng origin, LatLng destination) {
        GoogleDirection.withServerKey(Constants.GOOGLE_API_KEY)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(false)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.ic_my_location)))
                    .position(myLocation));

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.ic_location)))
                    .position(destination));


            Route route = direction.getRouteList().get(0);
            map.addPolyline(DirectionConverter.createPolyline(getContext(), route.getLegList().get(0).getDirectionPoint(), 7, getResources().getColor(R.color.colorPrimary)));

            setCameraWithCoordinationBounds(direction.getRouteList().get(0));
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1){
                selectedObject = data.getParcelableExtra("object");
                drawDirection(selectedObject);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_GPS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapView.getMapAsync(this);
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
                }
                return;
            }
            default:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
                break;
        }
    }


    private Bitmap setIcon(int src){
        Bitmap icon = BitmapFactory.decodeResource(Objects.requireNonNull(getContext()).getResources(), src);
        return Bitmap.createScaledBitmap(icon, 125, 100, false);
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}
