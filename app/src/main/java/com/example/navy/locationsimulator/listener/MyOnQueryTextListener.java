package com.example.navy.locationsimulator.listener;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;

import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.navy.locationsimulator.MainActivity;

/**
 * please add the description
 * <p>
 * <p>
 * Created by liuyf on 2019/12/26.
 */
public class MyOnQueryTextListener implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private MainActivity context;
    private SuggestionSearch suggestionSearch;
    private PoiSearch poiSearch;

    public MyOnQueryTextListener(SearchView s, MainActivity c,SuggestionSearch suggest,PoiSearch p) {
        this.searchView = s;
        this.context = c;
        this.suggestionSearch = suggest;
        this.poiSearch = p;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        suggestionSearch.requestSuggestion(new SuggestionSearchOption().keyword(query).city(context.getCity()));


        //隐藏键盘
        if (searchView != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //do nothing
        return true;
    }
}
