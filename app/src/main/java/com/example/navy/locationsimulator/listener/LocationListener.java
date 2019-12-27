package com.example.navy.locationsimulator.listener;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.navy.locationsimulator.MainActivity;

/**
 * please add the description
 * <p>
 * <p>
 * Created by liuyf on 2019/12/25.
 */
public class LocationListener extends BDAbstractLocationListener {
    private BaiduMap baiduMap;
    private MapView mapView;
    private MainActivity mainActivity;
    private boolean isFirstTimeInit = true;


    public LocationListener(BaiduMap map, MapView view,MainActivity mm) {
        this.mainActivity = mm;
        this.baiduMap = map;
        this.mapView = view;
    }
    @Override
    public void onReceiveLocation(BDLocation location) {
        //mapView 销毁后不在处理新接收的位置
        if (location == null || mapView == null ||null == location.getCity()){
            return;
        }
        mainActivity.setCity(location.getCity());
        mainActivity.setCurrent_latlng(new LatLng(location.getLatitude(), location.getLongitude()));
        if (isFirstTimeInit) {
            MapStatusUpdate mapstatusupdate = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));
            baiduMap.animateMapStatus(mapstatusupdate);
            isFirstTimeInit = false;

        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);
    }
}
