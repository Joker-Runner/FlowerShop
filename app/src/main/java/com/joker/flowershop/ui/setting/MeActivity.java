package com.joker.flowershop.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.CircleImageView;
import com.joker.flowershop.bean.UserBean;
import com.joker.flowershop.ui.CreateNewAddressActivity;
import com.joker.flowershop.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.setting_icon)
    LinearLayout settingIcon;
    @BindView(R.id.my_icon)
    CircleImageView myIcon;
    @BindView(R.id.setting_username)
    LinearLayout settingUsername;
    @BindView(R.id.my_username)
    TextView myUsername;
    @BindView(R.id.setting_email)
    LinearLayout settingEmail;
    @BindView(R.id.my_email)
    TextView myEmail;
    @BindView(R.id.setting_qr_code)
    LinearLayout settingQRCode;
    @BindView(R.id.setting_city)
    LinearLayout settingCity;
    @BindView(R.id.my_city)
    TextView myCity;
    @BindView(R.id.setting_address)
    LinearLayout settingAddress;
    @BindView(R.id.setting_password)
    LinearLayout settingPassword;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    final static String INTENT_USER_BEAN = "intent_user_bean";
    final static int REQUEST_CODE_SETTING_USERNAME = 0x000001;
    final static int REQUEST_CODE_SETTING_ICON = 0x000002;

    UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        editor = sharedPreferences.edit();

//        myIcon.setImageResource(R.drawable.icon_default);
//        myCity.setText(userBean.getCity());
        settingIcon.setOnClickListener(this);
        settingUsername.setOnClickListener(this);
        settingEmail.setOnClickListener(this);
        settingQRCode.setOnClickListener(this);
        settingCity.setOnClickListener(this);
        settingAddress.setOnClickListener(this);
        settingPassword.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    public void initView() {
        String userBeanJson;
        userBeanJson = sharedPreferences.getString(Constants.LOGGED_USER_JSON, "{}");
        Type type = new TypeToken<UserBean>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        userBean = gson.fromJson(userBeanJson, type);
        Glide.with(this).load(userBean.getIcon()).centerCrop().into(myIcon);
        myUsername.setText(userBean.getUsername());
        myEmail.setText(userBean.getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_icon:
                Intent intentIcon = new Intent(MeActivity.this, SettingIconActivity.class);
                intentIcon.putExtra(INTENT_USER_BEAN, userBean);
                startActivityForResult(intentIcon, REQUEST_CODE_SETTING_ICON);
                break;
            case R.id.setting_username:
                Intent intentUsername = new Intent(MeActivity.this, SettingUsernameActivity.class);
                intentUsername.putExtra(INTENT_USER_BEAN, userBean);
                startActivityForResult(intentUsername, REQUEST_CODE_SETTING_USERNAME);
                break;
            case R.id.setting_email:
                // Do Nothing !!!
                break;
            case R.id.setting_qr_code:
                Intent intentQRCode = new Intent(MeActivity.this, MyQRCodeActivity.class);
                intentQRCode.putExtra(INTENT_USER_BEAN, userBean);
                startActivity(intentQRCode);
                break;
            case R.id.setting_city:
                Intent intent = new Intent(MeActivity.this, CityPickerPopActivity.class);
                startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_CITY);
                break;
            case R.id.setting_address:
                Intent intentAddress = new Intent(MeActivity.this, CreateNewAddressActivity.class);
                startActivity(intentAddress);
                break;
            case R.id.setting_password:
                Intent intentPassword = new Intent(MeActivity.this, SettingPasswordActivity.class);
                intentPassword.putExtra(INTENT_USER_BEAN, userBean);
                startActivity(intentPassword);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING_USERNAME:
                if (resultCode == RESULT_OK) {
                    myUsername.setText(data.getStringExtra("username"));
                }
                break;
            case REQUEST_CODE_SETTING_ICON:
                if (resultCode == RESULT_OK) {
                    String iconURL = data.getStringExtra("icon");
//                    String iconURL = "http://123.206.201.169:8080/FlowerShop/icon/1496311795128.jpeg";
                    Glide.with(this).load(iconURL).into(myIcon);
                    userBean.setIcon(iconURL);
                    editor.putString(Constants.LOGGED_USER_JSON, new Gson().toJson(userBean)).commit();
                }
                break;
            case Constants.REQUEST_CODE_SELECT_CITY:
                if (resultCode == RESULT_OK) {
                    String selectedCity = data.getStringExtra("city");
                    String selectedCityCode = data.getStringExtra("city_code");
                    if (userBean != null) {
                        userBean.setCity(selectedCity);
                        userBean.setCityCode(selectedCityCode);
                        editor.putString(Constants.LOGGED_USER_JSON, new Gson().toJson(userBean)).commit();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpUtils.post().url(Constants.getURL() + "/setting_city")
                                        .addParams("user_id", String.valueOf(userBean.getId()))
                                        .addParams("city_code", userBean.getCityCode())
                                        .addParams("city", userBean.getCity())
                                        .build().execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {

                                    }
                                });
                            }
                        }).start();
                    }
                    // TODO: 6/1 0001 设置商店的城市 是否登录的城市选择
                    if (!selectedCity.equals("")) {
                        myCity.setText(selectedCity);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
