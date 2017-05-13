package com.joker.flowershop.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.ProvinceBean;
import com.joker.flowershop.bean.ReceiverBean;
import com.joker.flowershop.utils.Constants;
import com.joker.flowershop.utils.GetJsonDataUtil;

import org.json.JSONArray;

import java.util.ArrayList;

import static com.joker.flowershop.R.id.address_edit;

public class CreateNewAddressActivity extends AppCompatActivity {

    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_SHOW_PICKER = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private static boolean IS_JSON_INITIALIZED = false;

    private EditText addresseeEdit;
    private EditText telephoneEdit;
    private EditText cityEdit;
    private EditText addressEdit;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_address);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        editor = sharedPreferences.edit();
        addresseeEdit = (EditText) findViewById(R.id.addressee_edit);
        telephoneEdit = (EditText) findViewById(R.id.telephone_edit);
        cityEdit = (EditText) findViewById(R.id.city_edit);
        addressEdit = (EditText) findViewById(address_edit);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

//        cityEdit.setInputType(InputType.TYPE_NULL);
        cityEdit.setFocusable(false);
        cityEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(cityEdit.getWindowToken(), 0);
                // 开始解析数据,并弹出城市选择框
                mHandler.sendEmptyMessage(MSG_SHOW_PICKER);
//                cityEdit.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
            }
        });
    }

    public void attemptSaveAddress() {
        if (TextUtils.isEmpty(addresseeEdit.getText())
                || TextUtils.isEmpty(telephoneEdit.getText())
                || TextUtils.isEmpty(cityEdit.getText())
                || TextUtils.isEmpty(addressEdit.getText())) {
            Toast.makeText(this, "请填写完整后再保存", Toast.LENGTH_LONG).show();
        } else {
            ReceiverBean receiverBean = new ReceiverBean(addresseeEdit.getText().toString(),
                    telephoneEdit.getText().toString(),
                    cityEdit.getText().toString() + " " + addressEdit.getText().toString());
            String receiverJson = new Gson().toJson(receiverBean);
            if (editor.putString(Constants.DEFAULT_RECEIVER, receiverJson).commit()) {
                finish();
            } else {
                Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 1) {
            attemptSaveAddress();
        } else if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_PICKER:
                    if (thread == null) {// 如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initJsonData(); // 开始解析数据
                            }
                        });
                        thread.start();
                    } else if (IS_JSON_INITIALIZED) {
                        showPickerView();
                    }
                    break;
                case MSG_LOAD_SUCCESS: // 解析数据成功
                    IS_JSON_INITIALIZED = true;
                    showPickerView();
                    break;
                case MSG_LOAD_FAILED: // 解析数据失败
                    IS_JSON_INITIALIZED = false;
                    break;
            }
        }
    };

    private void showPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder
                (this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //返回的分别是三个级别的选中位置
                        String tx = options1Items.get(options1).getPickerViewText() +
                                " " + options2Items.get(options1).get(options2) + " " +
                                options3Items.get(options1).get(options2).get(options3);
                        cityEdit.setText(tx); // 显示选择的城市
                    }
                }).setTitleText("城市选择")
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(true)// default is true
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<ProvinceBean> provinceBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = provinceBean;

        for (int i = 0; i < provinceBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < provinceBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = provinceBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (provinceBean.get(i).getCityList().get(c).getArea() == null
                        || provinceBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < provinceBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = provinceBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }


    public ArrayList<ProvinceBean> parseData(String result) {//Gson 解析
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
}
