package com.joker.flowershop.ui;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.adapter.recycler.ShoppingCartAdapter;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.utils.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShoppingCartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView flowerList;
    private ShoppingCartAdapter shoppingCartAdapter;
    private UserFlowerListTask userFlowerListTask;

    private TextView totalPrice;
    private HashSet<FlowerBean> checkedFlowerBeanArrayList;

    private SharedPreferences sharedPreferences;

    private final static int HANDLER_SHOW_FLOWER_LIST_TAG = 1;
    public final static int HANDLER_CHECKED_FLOWER_TAG = 2;
    public final static int HANDLER_UNCHECKED_FLOWER_TAG = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        flowerList = (RecyclerView) findViewById(R.id.flower_list);
        totalPrice = (TextView) findViewById(R.id.total_price);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                ContextCompat.getColor(this, android.R.color.holo_blue_bright),
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                ContextCompat.getColor(this, android.R.color.holo_red_light));

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        checkedFlowerBeanArrayList = new HashSet<>();
        initRecyclerView();
        setRecyclerView();

    }

    @Override
    public void onRefresh() {
        setRecyclerView();
    }

    public void initRecyclerView() {
        flowerList.setLayoutManager(new LinearLayoutManager(ShoppingCartActivity.this));
        flowerList.addItemDecoration(new ResourceItemDivider(ShoppingCartActivity.this, R.drawable.divider));
    }

    /**
     * 初始化刷新列表
     */
    public void setRecyclerView() {
        swipeRefreshLayout.setRefreshing(true);
        userFlowerListTask = new UserFlowerListTask();
        userFlowerListTask.execute((Void) null);
        swipeRefreshLayout.setRefreshing(false);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case HANDLER_SHOW_FLOWER_LIST_TAG: // 获取到Flower的列表，进行UI加载
                    final ArrayList<FlowerBean> flowerBeanArrayList = (ArrayList<FlowerBean>) msg.obj;
                    if (flowerBeanArrayList.size() != 0) {
                        shoppingCartAdapter = new ShoppingCartAdapter
                                (R.layout.shopping_cart_flower_item, flowerBeanArrayList);
                        shoppingCartAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
                        flowerList.setAdapter(shoppingCartAdapter);
                        checkedFlowerBeanArrayList.clear();
                        totalPrice.setText("0");
                    }
                    break;
                case HANDLER_CHECKED_FLOWER_TAG: // 选中购物车商品
                    FlowerBean flowerBean = (FlowerBean) msg.obj;
                    checkedFlowerBeanArrayList.add(flowerBean);
                    double total = 0;
                    for (FlowerBean flower :
                            checkedFlowerBeanArrayList) {
                        total += flower.getPrice();
                    }
                    totalPrice.setText(String.valueOf(total));
                    break;
                case HANDLER_UNCHECKED_FLOWER_TAG: // 取消选中购物车商品
                    FlowerBean flowerBean1 = (FlowerBean) msg.obj;
                    checkedFlowerBeanArrayList.remove(flowerBean1);
                    double total1 = 0;
                    for (FlowerBean flower :
                            checkedFlowerBeanArrayList) {
                        total1 += flower.getPrice();
                    }
                    totalPrice.setText(String.valueOf(total1));
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

    /**
     * 用于加载列表的异步任务
     */
    private class UserFlowerListTask extends AsyncTask<Void, Void, ArrayList<FlowerBean>> {

        String flowerListJson;

        /**
         * 后台执行异步任务
         *
         * @param params 参数
         * @return 成功返回Json生成的商品列表List，否则返回 null
         */
        @Override
        protected ArrayList<FlowerBean> doInBackground(Void... params) {
            try {
                RequestBody requestBody = new FormBody.Builder().add("user_id",
                        String.valueOf(sharedPreferences.getInt(Constants.LOGGED_USER_ID, 0))).build();
                Request request = new Request.Builder().url(Constants.getURL()
                        + "/shopping_cart_list").post(requestBody).build();
                Response response = new OkHttpClient().newCall(request).execute();
                flowerListJson = response.body().string();
                Log.i("TAG", flowerListJson);
                Type type = new TypeToken<ArrayList<FlowerBean>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(flowerListJson, type);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        /**
         * 异步任务结束后的处理
         *
         * @param flowerBeanArrayList 商品列表 FlowerList
         */
        @Override
        protected void onPostExecute(ArrayList<FlowerBean> flowerBeanArrayList) {
            if (flowerBeanArrayList != null) {
                Message message = new Message();
                message.arg1 = HANDLER_SHOW_FLOWER_LIST_TAG;
                message.obj = flowerBeanArrayList;
                handler.handleMessage(message);
            }
        }
    }
}
