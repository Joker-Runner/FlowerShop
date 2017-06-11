package com.joker.flowershop.ui.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.CityPickerAdapter;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.bean.CityBean;
import com.joker.flowershop.utils.Constants;
import com.joker.flowershop.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class CityPickerPopActivity extends Activity {

    @BindView(R.id.jump_to)
    LinearLayout jump;
    @BindView(R.id.choice_city_close)
    ImageView closeActivity;
    @BindView(R.id.city_search_view)
    EditText citySearch;
    @BindView(R.id.clear_search)
    ImageView clearSearch;
    @BindView(R.id.location_button)
    LinearLayout locationButton;
    @BindView(R.id.location_city)
    TextView locationCity;
    @BindView(R.id.city_recycler_list)
    RecyclerView cityRecyclerList;

    @BindView(R.id.default_city_list)
    LinearLayout defaultCityLayout;
    @BindView(R.id.search_result_recycler_view)
    RecyclerView searchResultCityRecyclerList;

    private LinearLayoutManager linearLayoutManager;

    private ArrayList<CityBean> cityBeanList = new ArrayList<>();
    private ArrayList<CityBean> searchResultCityBeanList = new ArrayList<>();

    private Thread thread = null;

    private CityBean cityBean;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;


    // 从服务器请求城市列表
    private static final int MSG_REQUEST_CITY_LIST = 0x0034;

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

        mHandler.sendEmptyMessage(MSG_REQUEST_CITY_LIST);
//        mHandler.sendEmptyMessage(MSG_SHOW_CITY_LIST);

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }
        });

        citySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                defaultCityLayout.setVisibility(View.VISIBLE);
                searchResultCityRecyclerList.setVisibility(View.GONE);
                if (TextUtils.isEmpty(s)) {
                    clearSearch.setVisibility(View.GONE);
                } else {
                    clearSearch.setVisibility(View.VISIBLE);
                    // TODO: 6/1 0001 城市查询 搜索
                }
            }
        });

        citySearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String queryText = citySearch.getText().toString();
                if (!queryText.equals("")) {
                    onSubmitQuery(queryText);
                }
                return true;
            }
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                citySearch.setText("");
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
        linearLayoutManager = new LinearLayoutManager(CityPickerPopActivity.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        cityRecyclerList.setLayoutManager(linearLayoutManager);
        cityRecyclerList.addItemDecoration(new ResourceItemDivider(CityPickerPopActivity.this, R.drawable.divider));
        // 搜索结果部分
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(CityPickerPopActivity.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        searchResultCityRecyclerList.setLayoutManager(linearLayoutManager1);
        searchResultCityRecyclerList.addItemDecoration
                (new ResourceItemDivider(CityPickerPopActivity.this, R.drawable.divider));
    }

    private void setRecyclerAdapter() {
        CityPickerAdapter cityPickerAdapter = new CityPickerAdapter(cityBeanList);
//        cityPickerAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        cityPickerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                citySelected(cityBeanList.get(position));
            }
        });
        cityRecyclerList.setAdapter(cityPickerAdapter);
    }

    private void onSubmitQuery(String queryText) {
        //TODO 查询
        searchResultCityBeanList.clear();
        for (CityBean city : cityBeanList) {
            if (city.getCity().contains(queryText))
                searchResultCityBeanList.add(city);
        }
        CityPickerAdapter cityPickerAdapter = new CityPickerAdapter(searchResultCityBeanList);
        cityPickerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                citySelected(searchResultCityBeanList.get(position));
            }
        });
        searchResultCityRecyclerList.setAdapter(cityPickerAdapter);
        defaultCityLayout.setVisibility(View.GONE);
        searchResultCityRecyclerList.setVisibility(View.VISIBLE);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REQUEST_CITY_LIST:
                    if (thread == null) {// 如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                requestCityList(); // 开始请求数据
                            }
                        });
                        thread.start();
                    } else if (IS_JSON_INITIALIZED) {
                        setRecyclerAdapter();
                    }
                    break;
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
            cityBean = new CityBean();
            cityBean.setCity(Utils.extractLocation(aMapLocation.getCity()));
            cityBean.setPr(aMapLocation.getProvince());
            cityBean.setCode(aMapLocation.getCityCode());

            locationCity.setText(cityBean.getCity());
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    citySelected(cityBean);
                }
            });
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
        mOption.setHttpTimeOut(30 * 1000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setMockEnable(false); //设置是否允许模拟位置,默认为false，不允许模拟位置
        return mOption;
    }

    public void citySelected(final CityBean cityBean) {

        Intent intent = new Intent();
        intent.putExtra("city", cityBean.getCity());
        intent.putExtra("city_code", cityBean.getCode());
        CityPickerPopActivity.this.setResult(RESULT_OK, intent);
        CityPickerPopActivity.this.finish();
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

    private void requestCityList() {
        OkHttpUtils.get().url(Constants.getURL() + "/get_city")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
            }

            @Override
            public void onResponse(String response, int id) {
                Type type = new TypeToken<ArrayList<CityBean>>() {
                }.getType();
                cityBeanList = new GsonBuilder().create().fromJson(response, type);
                mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
            }
        });
    }


    /**
     * 解析数据
     */
    private void initJsonData() {
        String JsonData = null;//获取assets目录下的json文件数据
        try {
            JsonData = new Utils().getJson(this, "city.json");
            Type type = new TypeToken<ArrayList<CityBean>>() {
            }.getType();
            cityBeanList = new GsonBuilder().create().fromJson(JsonData, type);
            mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
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
