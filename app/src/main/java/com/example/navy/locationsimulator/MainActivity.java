package com.example.navy.locationsimulator;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.example.navy.locationsimulator.listener.MyMapOnclickListener;
import com.example.navy.locationsimulator.listener.MyOnGetSuggestionResultListener;
import com.example.navy.locationsimulator.listener.MyOnQueryTextListener;
import com.example.navy.locationsimulator.services.MockLocationService;
import com.example.navy.locationsimulator.util.LocationUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private boolean IS_MOCK_SERVICE_START = false;

    private FloatingActionButton beginMock, stopMock, moveToLocation;

    private String city = "北京";
    private LatLng selected_latlng;   //被模拟的定位
    private LatLng current_latlng;       //用户当前实际位置

    //百度地图相关
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient locationClient;


    //搜索相关
    private SearchView searchView;
    private ListView search_result;
    private PoiSearch poiSearch;
    private SuggestionSearch suggestionSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        initViews(); //初始化相关控件

        //初始化搜索相关
        initBaiDuMap();
        initPoiSearch();
        initSuggestionSearch();

        //初始化监听器
        setListeners();


    }


    //判断手机是否选择了使用当前应用为模拟位置应用
    public boolean isMockSwithOn(LocationManager locationManager) {

        boolean canMockPosition = false;
        try {
            String providerStr = LocationManager.GPS_PROVIDER;

            try {
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Log.e("remove test provider failed ", e.getMessage());
            }
            LocationProvider provider = locationManager.getProvider(providerStr);

            if (provider != null) {
                try {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } catch (SecurityException e) {
                    return false;
                }
            } else {
                locationManager.addTestProvider(
                        providerStr
                        , true, true, false, false, true, true, true
                        , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
            }
            locationManager.setTestProviderEnabled(providerStr, true);
            locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

            // 模拟位置可用
            canMockPosition = true;
            locationManager.setTestProviderEnabled(providerStr, false);
            locationManager.removeTestProvider(providerStr);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        return canMockPosition;

    }


    private void checkPermission() {
        MockLocationService.LOCATION_MANAGER = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> ungrantedPermisionList = new ArrayList<String>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                ungrantedPermisionList.add(permission);
            }
        }
        if (!ungrantedPermisionList.isEmpty()) {
            requestPermissions(ungrantedPermisionList.toArray(new String[ungrantedPermisionList.size()]), 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(MainActivity.this, "请同意使用所有权限,否则无法正常使用...", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    private void initBaiDuMap() {

        mapView = findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        locationClient = new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setIsNeedAddress(true);
        option.setScanSpan(1001);
        locationClient.setLocOption(option);
        com.example.navy.locationsimulator.listener.LocationListener locationListener = new com.example.navy.locationsimulator.listener.LocationListener(baiduMap, mapView, this);
        locationClient.registerLocationListener(locationListener);
        locationClient.start();
        baiduMap.setOnMapClickListener(new MyMapOnclickListener(baiduMap, this));


    }


    private void initPoiSearch() {
        poiSearch = PoiSearch.newInstance();
        //poiSearch.setOnGetPoiSearchResultListener(new MyOnGetPoiSearchResultListener());
    }


    private void setListeners() {
        suggestionSearch.setOnGetSuggestionResultListener(new MyOnGetSuggestionResultListener(baiduMap, search_result, MainActivity.this));
        searchView.setOnQueryTextListener(new MyOnQueryTextListener(searchView, MainActivity.this, suggestionSearch, poiSearch));

        beginMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGpsOpened()) {
                    showGpsDialog();
                } else {
                    if (!isMockSwithOn(MockLocationService.LOCATION_MANAGER)) {
                        Toast.makeText(MainActivity.this, "请打开开发者模式，并将此应用设置为模拟定位应用", Toast.LENGTH_SHORT).show();
                    } else {
                        if (selected_latlng == null) {
                            Toast.makeText(MainActivity.this, "请先在地图上选择目标地点", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "开始模拟位置", Toast.LENGTH_SHORT).show();
                            Intent startMockServiceIntent = new Intent(MainActivity.this, MockLocationService.class);
                            Bundle bundle = new Bundle();
                            double[] loc = LocationUtil.gcj02_To_Gps84(selected_latlng.latitude, selected_latlng.longitude);
                            bundle.putDouble("LATITUDE", loc[0]);
                            bundle.putDouble("LONGITUDE", loc[1]);
                            startMockServiceIntent.putExtra(MockLocationService.INPUT_KEY, bundle);
                            startService(startMockServiceIntent);
                            beginMock.setVisibility(View.INVISIBLE);
                            stopMock.setVisibility(View.VISIBLE);
                            IS_MOCK_SERVICE_START = true;
                        }
                    }
                }

            }
        });

        stopMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_MOCK_SERVICE_START) {
                    Intent stopMockServiceIntent = new Intent(MainActivity.this, MockLocationService.class);
                    stopService(stopMockServiceIntent);
                    Toast.makeText(MainActivity.this, "停止位置模拟", Toast.LENGTH_SHORT).show();
                    IS_MOCK_SERVICE_START = false;
                    baiduMap.clear();
                    beginMock.setVisibility(View.VISIBLE);
                    stopMock.setVisibility(View.INVISIBLE);
                    setSelected_latlng(null);
                    baiduMap.clear();
                }

            }
        });


        moveToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_latlng != null) {
                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(current_latlng);
                    baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(20).build()));
                    baiduMap.animateMapStatus(mapStatusUpdate);

                }
            }
        });

        search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lng = ((TextView) view.findViewById(R.id.poi_longitude)).getText().toString();
                String lat = ((TextView) view.findViewById(R.id.poi_latitude)).getText().toString();
                LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                setSelected_latlng(latLng);
                search_result.setVisibility(View.GONE);
                searchView.clearFocus();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                baiduMap.animateMapStatus(mapStatusUpdate);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(20).build()));
            }
        });

    }

    private void initViews() {
        beginMock = findViewById(R.id.begin_button);
        stopMock = findViewById(R.id.stop_button);
        moveToLocation = findViewById(R.id.move_to_location);
        search_result = findViewById(R.id.search_result);
        searchView = findViewById(R.id.search_view);
        moveToLocation = findViewById(R.id.move_to_location);
    }

    private void initSuggestionSearch() {
        suggestionSearch = SuggestionSearch.newInstance();
    }


    //判断GPS是否打开
    private boolean isGpsOpened() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showGpsDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Tips")//这里是表头的内容
                .setMessage("是否开启GPS定位服务?")//这里是中间显示的具体信息
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 0);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .show();
    }

    public void setSelected_latlng(LatLng selected_latlng) {
        this.selected_latlng = selected_latlng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCurrent_latlng(LatLng current_latlng) {
        this.current_latlng = current_latlng;
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        poiSearch.destroy();
        suggestionSearch.destroy();

        String providerName = LocationManager.GPS_PROVIDER;
        try {
            if (MockLocationService.LOCATION_MANAGER.isProviderEnabled(providerName) || (null != MockLocationService.LOCATION_MANAGER.getProvider(providerName))) {
                MockLocationService.LOCATION_MANAGER.removeTestProvider(providerName);
            }
        } catch (Exception e) {
            Log.e("remove provider " + providerName + " 失败 :", e.getMessage());
        }

    }


}
