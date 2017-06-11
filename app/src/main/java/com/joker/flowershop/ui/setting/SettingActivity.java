package com.joker.flowershop.ui.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.joker.flowershop.R;
import com.joker.flowershop.ui.CreateNewAddressActivity;
import com.joker.flowershop.utils.Constants;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sign_out;
    private Button add_address;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sign_out = (Button) findViewById(R.id.sign_out);
        add_address = (Button) findViewById(R.id.add_address);
        sign_out.setOnClickListener(this);
        add_address.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        editor = sharedPreferences.edit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
            sign_out.setVisibility(View.VISIBLE);
        } else {
            sign_out.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("确定要退出当前账号吗？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.remove(Constants.LOGGED_IN).commit();
                        editor.remove(Constants.LOGGED_USER_ID).commit();
                        editor.remove(Constants.LOGGED_USER_JSON).commit();
                        sign_out.setVisibility(View.GONE);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.add_address:
                Intent intent = new Intent(this,CreateNewAddressActivity.class);
                startActivity(intent);
                break;
        }
    }
}
