package com.example.navy.locationsimulator.services;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * please add the description
 * <p>
 * <p>
 * Created by liuyf on 2019/12/20.
 */
public class MockLocationService extends Service {
    public static final String INPUT_KEY = "INPUT_KEY";
    public static LocationManager LOCATION_MANAGER;

    private HandlerThread mockHandlerThread;
    private Handler mockHandler;
    private double latitude, longitude;  //维度  经度
    public final String[] testProviderNameList = new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        removeAllTestProvider();
        setTestGPSProvider();
        setTestNetWorkProvider();

        mockHandlerThread = new HandlerThread("mockThread");
        mockHandlerThread.start();


        mockHandler = new Handler(mockHandlerThread.getLooper()) {
            private boolean swith = false;

            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0: {   //stop
                        swith = false;
                        break;
                    }
                    case 1: {  //start
                        swith = true;
                        break;
                    }
                    case 2: {  //others
                        break;
                    }
                }
                try {
                    while (swith) {
                        Thread.sleep(100);
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        setTestGpsLocation();
                        setTestNetworkLocation();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle inputBundle = intent.getBundleExtra(INPUT_KEY);
        latitude = inputBundle.getDouble("LATITUDE");
        longitude = inputBundle.getDouble("LONGITUDE");
        Log.i("location_str", "传入的坐标为:" + latitude + "," + longitude);
        mockHandler.sendEmptyMessage(1);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mockHandlerThread.interrupt();
        mockHandler.sendEmptyMessage(0);
        mockHandlerThread.quit();
        removeAllTestProvider();

        super.onDestroy();
    }

    private void setTestGPSProvider() {
        LocationProvider provider = LOCATION_MANAGER.getProvider(LocationManager.GPS_PROVIDER);
        try {
            LOCATION_MANAGER.addTestProvider(LocationManager.GPS_PROVIDER, false, true, true,
                    false, true, true, true, 0, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!LOCATION_MANAGER.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                LOCATION_MANAGER.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        LOCATION_MANAGER.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null,
                System.currentTimeMillis());
    }

    private void setTestNetWorkProvider() {
        String providerStr = LocationManager.NETWORK_PROVIDER;
        try {
            LOCATION_MANAGER.addTestProvider(providerStr, false, false,
                    false, false, false, false,
                    false, 1, Criteria.ACCURACY_FINE);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (!LOCATION_MANAGER.isProviderEnabled(providerStr)) {
            try {
                LOCATION_MANAGER.setTestProviderEnabled(providerStr, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //LOCATION_MANAGER.setTestProviderStatus(LocationManager.NETWORK_PROVIDER, LocationProvider.AVAILABLE, null,System.currentTimeMillis());

    }

    private void removeAllTestProvider() {
        for (String providerName : testProviderNameList) {
            try {
                if (LOCATION_MANAGER.isProviderEnabled(providerName) || (null != LOCATION_MANAGER.getProvider(providerName))) {
                    LOCATION_MANAGER.removeTestProvider(providerName);
                }
            } catch (Exception e) {
                Log.e("remove provider " + providerName + " 失败 :", e.getMessage());
            }
        }

    }

    private void setTestGpsLocation() {

        String providerStr = LocationManager.GPS_PROVIDER;
        try {
            LOCATION_MANAGER.setTestProviderLocation(providerStr, generateFakeLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTestNetworkLocation() {
        String providerStr = LocationManager.NETWORK_PROVIDER;
        try {
            LOCATION_MANAGER.setTestProviderLocation(providerStr, generateFakeLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Location generateFakeLocation() {
        Location fakeLocation = new Location("gps");
        fakeLocation.setAccuracy(2.0F);
        fakeLocation.setAltitude(55.0D);
        fakeLocation.setBearing(1.0F);
        Bundle bundle = new Bundle();
        bundle.putInt("satellites", 7);
        fakeLocation.setExtras(bundle);
        fakeLocation.setLatitude(latitude);
        fakeLocation.setLongitude(longitude);
        fakeLocation.setTime(System.currentTimeMillis());
        fakeLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        return fakeLocation;
    }


}
