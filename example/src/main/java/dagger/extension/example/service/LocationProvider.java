package dagger.extension.example.service;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.AllowStubGeneration;
import dagger.extension.example.event.PermissionEvent;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class LocationProvider implements LocationListener
{

    public static final int MIN_LOCATION_UPDATE_INTERVAL = 5000;
    public static final int MIN_DISTANCE_IN_METERS = 1000;

    private LocationManager locationManager;
    private final PublishSubject<Location> container = PublishSubject.create();

    private boolean isActive = false;

    @AllowStubGeneration
    @Inject
    public LocationProvider(LocationManager locationManager)
    {
        this.locationManager = locationManager;
    }

    public Location lastLocation()
    {
        return getLastKnownLocation(getBestProvider());
    }

    private Location getLastKnownLocation(String bestProvider)
    {
        try
        {
            return locationManager.getLastKnownLocation(bestProvider);
        } catch (SecurityException e)
        {

        }
        return null;
    }

    private String getBestProvider()
    {
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        return locationManager.getBestProvider(criteria, false);
    }

    public void requestLocationUpdates()
    {
        if (isActive) {
            return;
        }
        isActive = true;
        locationManager.requestLocationUpdates(getBestProvider(), MIN_LOCATION_UPDATE_INTERVAL, MIN_DISTANCE_IN_METERS, this);
    }

    private void removeUpdates()
    {
        if (!isActive) {
            return;
        }
        isActive = false;
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        container.onNext(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        onLocationChanged(lastLocation());
    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    public void disposeIfNotObserved() {
        if (!container.hasObservers())
        {
            this.removeUpdates();
        }
    }

    public Observable<Location> onNewLocation() {
        return container;
    }


}