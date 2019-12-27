package com.example.navy.locationsimulator.services;

 import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
 import android.widget.Toast;


/**
 * please add the description
 * <p>
 * <p>
 * Created by liuyf on 2019/12/27.
 */
public class MyQuickSettingTileService extends TileService {

    /* Icon inactive_icon =  Icon.createWithResource(getApplicationContext(), R.drawable.qs_inactive_icon);
    Icon active_icon = Icon.createWithResource(getApplicationContext(), R.drawable.qs_active_icon);*/

    @Override
    public void onClick() {
        Toast.makeText(MyQuickSettingTileService.this, "111", Toast.LENGTH_SHORT).show();
        if (getQsTile().getState() == Tile.STATE_ACTIVE) {
            getQsTile().setState(Tile.STATE_INACTIVE);// 更改成非活跃状态
        }else{
            getQsTile().setState(Tile.STATE_ACTIVE);// 更改成非活跃状态

        }
        getQsTile().updateTile();

    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }



    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }



}
