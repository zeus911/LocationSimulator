package com.example.navy.locationsimulator.listener;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.example.navy.locationsimulator.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * please add the description
 * <p>
 * <p>
 * Created by liuyf on 2019/12/26.
 */
public class MyOnGetSuggestionResultListener implements OnGetSuggestionResultListener {
    private BaiduMap baiduMap;
    private ListView searchResultList;
    private Context context;

    public MyOnGetSuggestionResultListener(BaiduMap bdmap, ListView sr,Context co) {
        this.baiduMap = bdmap;
        this.searchResultList = sr;
        this.context = co;
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            //未找到相关结果
        }
        //获取在线建议检索结果
        else {
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            int retCnt = suggestionResult.getAllSuggestions().size();
            for (int i = 0; i < retCnt; i++) {
                if (suggestionResult.getAllSuggestions().get(i).pt == null) {
                    continue;
                }
                Map<String, Object> poiItem = new HashMap<String, Object>();
                poiItem.put("key_name", suggestionResult.getAllSuggestions().get(i).key);
                poiItem.put("key_addr", suggestionResult.getAllSuggestions().get(i).city + " " + suggestionResult.getAllSuggestions().get(i).district);
                poiItem.put("key_lng", "" + suggestionResult.getAllSuggestions().get(i).pt.longitude);
                poiItem.put("key_lat", "" + suggestionResult.getAllSuggestions().get(i).pt.latitude);
                data.add(poiItem);
            }
            SimpleAdapter adp = new SimpleAdapter(
                    context,
                    data,
                    R.layout.poi_layout,
                    new String[]{"key_name", "key_addr", "key_lng", "key_lat"},// 与下面数组元素要一一对应
                    new int[]{R.id.poi_name, R.id.poi_addr, R.id.poi_longitude, R.id.poi_latitude});
            searchResultList.setAdapter(adp);
            searchResultList.setVisibility(View.VISIBLE);
        }

    }
}
