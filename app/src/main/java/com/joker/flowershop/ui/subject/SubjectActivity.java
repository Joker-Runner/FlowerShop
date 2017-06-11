package com.joker.flowershop.ui.subject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.FragmentAdapter;
import com.joker.flowershop.bean.CateGoryBean;
import com.joker.flowershop.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;

public class SubjectActivity extends AppCompatActivity {

    private final static int HANDLER_GET_CATEGORY_TAG = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getCategoryList();
//        setViewPager();
    }

    protected void getCategoryList() {
        OkHttpUtils.get().url(Constants.getURL() + "/get_category").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Type type = new TypeToken<ArrayList<CateGoryBean>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                ArrayList categoryList = gson.fromJson(response, type);
                Message message = new Message();
                message.arg1 = HANDLER_GET_CATEGORY_TAG;
                message.obj = categoryList;
                handler.sendMessage(message);
            }
        });
    }


    protected void setViewPager(ArrayList<CateGoryBean> cateGoryBeanArrayList) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        for (CateGoryBean categorybean : cateGoryBeanArrayList) {
            SubjectFragment subjectFragment = new SubjectFragment();
            subjectFragment.setCategory(categorybean.getId());
            adapter.addFragment(subjectFragment,categorybean.getName());
        }
//        adapter.addFragment(new SubjectFragment(), "首页");
//        adapter.addFragment(new SubjectFragment(), "首页");
//        adapter.addFragment(new SubjectFragment(), "首页");
//        adapter.addFragment(new SubjectFragment(), "首页");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case HANDLER_GET_CATEGORY_TAG:
                    ArrayList<CateGoryBean> cateGoryBeanList = (ArrayList<CateGoryBean>) msg.obj;
                    setViewPager(cateGoryBeanList);
                    break;
            }
        }
    };

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
