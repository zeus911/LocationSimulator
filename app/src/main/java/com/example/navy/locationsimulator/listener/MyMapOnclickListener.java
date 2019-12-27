package com.example.navy.locationsimulator.listener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.navy.locationsimulator.MainActivity;
import com.example.navy.locationsimulator.R;

/**
 * please add the description
 * <p>
 * <p>
 * Created by liuyf on 2019/12/25.
 */
public class MyMapOnclickListener implements BaiduMap.OnMapClickListener {
    private BaiduMap baiduMap;
    private MainActivity mainActivity;
    private static BitmapDescriptor objFlag = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);


    public MyMapOnclickListener(BaiduMap map,MainActivity main) {
        this.baiduMap = map;
        this.mainActivity = main;
    }
    @Override
    public void onMapClick(LatLng latLng) {
        mainActivity.setSelected_latlng(latLng);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(mapStatusUpdate);
        addFlagAtPoint(latLng);

    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    private void addFlagAtPoint(LatLng latLng) {
        MarkerOptions flagIcon = new MarkerOptions().position(latLng).icon(objFlag);
        baiduMap.clear();
        baiduMap.addOverlay(flagIcon);
    }
}
