package com.joker.flowershop.ui.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.CityPickerAdapter;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.bean.ProvinceBean;
import com.joker.flowershop.utils.GetJsonDataUtil;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityPickerPopActivity extends Activity {

    @BindView(R.id.jump_to)
    LinearLayout jump;
    @BindView(R.id.choice_city_close)
    ImageView closeActivity;
    //    @BindView(R.id.city_search_view)
//    SearchView citySearchView;
    @BindView(R.id.city_search_view)
    EditText citySearch;
    @BindView(R.id.the_choice_city)
    TextView theChoiceCity;
    @BindView(R.id.city_recycler_list)
    RecyclerView cityRecyclerList;

    private ArrayList<String> cityList;

    private Thread thread = null;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private static final int MSG_SHOW_CITY_LIST = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private static boolean IS_JSON_INITIALIZED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker_pop);
        ButterKnife.bind(this);
        initRecyclerView();
        initLocation();

        mHandler.sendEmptyMessage(MSG_SHOW_CITY_LIST);

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityRecyclerList.scrollToPosition(1);
                Toast.makeText(CityPickerPopActivity.this, "jump", Toast.LENGTH_SHORT).show();
            }
        });

        citySearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery();
                return true;
            }
        });

        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPickerPopActivity.this.finish();
                CityPickerPopActivity.this.overridePendingTransition(0, R.anim.pop_translate_down);
            }
        });
    }

    private void initRecyclerView() {
        cityRecyclerList.setLayoutManager(new LinearLayoutManager(CityPickerPopActivity.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        cityRecyclerList.addItemDecoration(new ResourceItemDivider(CityPickerPopActivity.this, R.drawable.divider));
    }

    private void setRecyclerAdapter() {
        CityPickerAdapter cityPickerAdapter = new CityPickerAdapter(cityList);
//        cityPickerAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        cityPickerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(CityPickerPopActivity.this, cityList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        cityRecyclerList.setAdapter(cityPickerAdapter);
    }

    private void onSubmitQuery() {
        //TODO 查询
        Toast.makeText(this, "查询" + citySearch.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_CITY_LIST:
                    if (thread == null) {// 如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initJsonData(); // 开始解析数据
                            }
                        });
                        thread.start();
                    } else if (IS_JSON_INITIALIZED) {
                        setRecyclerAdapter();
                    }
                    break;
                case MSG_LOAD_SUCCESS: // 解析数据成功
                    IS_JSON_INITIALIZED = true;
                    setRecyclerAdapter();
                    break;
                case MSG_LOAD_FAILED: // 解析数据失败
                    IS_JSON_INITIALIZED = false;
                    Toast.makeText(CityPickerPopActivity.this, "解析失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            theChoiceCity.setText(aMapLocation.getCity());
        }
    };

    public void initLocation() {
        locationClient = new AMapLocationClient(this);
        locationOption = getDefaultOption();
        locationClient.setLocationOption(locationOption);
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setMockEnable(false); //设置是否允许模拟位置,默认为false，不允许模拟位置
        return mOption;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        this.overridePendingTransition(0, R.anim.pop_translate_down);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stopLocation();
        locationClient.onDestroy();
    }

    /**
     * 解析数据
     */
    private void initJsonData() {
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<ProvinceBean> provinceBean = parseData(JsonData);//用Gson 转成实体
        cityList = new ArrayList<>();//该省的城市列表（第二级）

        for (int i = 0; i < provinceBean.size(); i++) {//遍历省份
            for (int c = 0; c < provinceBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = provinceBean.get(i).getCityList().get(c).getName();
                cityList.add(CityName);//添加城市
            }
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    /**
     * Gson 解析
     *
     * @param result
     * @return
     */
    public ArrayList<ProvinceBean> parseData(String result) {
        ArrayList<ProvinceBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                ProvinceBean entity = gson.fromJson(data.optJSONObject(i).toString(), ProvinceBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
//                v.setFocusable(true); //这里不需要是因为下面一句代码会同时实现这个功能
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }
}
