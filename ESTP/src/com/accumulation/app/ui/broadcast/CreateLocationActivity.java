package com.accumulation.app.ui.broadcast;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.tool.debug.Logger;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

public class CreateLocationActivity extends BaseActivity {
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private FrameLayout mapFrame;
	private double x, y;
	private LatLng currentPt;
	private UiSettings mUiSettings;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_create_location;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "创建位置";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		mapFrame = (FrameLayout) findViewById(R.id.map);
		currentPt= new LatLng(x, y);
		mMapView = new MapView(this,
				new BaiduMapOptions().mapStatus(new MapStatus.Builder()
						.zoom(18f).target(currentPt).build()));
		mapFrame.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		mUiSettings.setZoomGesturesEnabled(false);
		mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {

			@Override
			public void onTouch(MotionEvent event) {

			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				currentPt = point;
				updateMapState();
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			public void onMapDoubleClick(LatLng point) {
				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			public void onMapStatusChangeStart(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChange(MapStatus status) {
				updateMapState();
			}
		});

	}

	private void updateMapState() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		x = intent.getDoubleExtra("x", 0);
		y = intent.getDoubleExtra("y", 0);
		Logger.e("--------------------" + x);
		Logger.e("--------------------" + y);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		if (mMapView != null) {
			mMapView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		if (mMapView != null) {
			mMapView.onDestroy();
		}
	}
}
