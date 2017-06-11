package com.joker.flowershop.ui.order;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.OrderAdapter;
import com.joker.flowershop.bean.OrderBean;
import com.joker.flowershop.utils.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {

    RecyclerView orderList;
    
    UserOrderListTask userOrderListTask;

    SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED,MODE_APPEND);
        orderList = (RecyclerView) findViewById(R.id.order_recycler_list);
        initListView();
    }

    /**
     * 初始化刷新列表
     */
    public void initListView() {
        userOrderListTask = new UserOrderListTask();
        userOrderListTask.execute((Void) null);
    }

    private void setOrderList(final ArrayList<OrderBean> orderBeanArrayList){
        orderList.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
        OrderAdapter orderAdapter = new OrderAdapter( orderBeanArrayList);
//        recyclerViewAdapter.setOnItemClickListener(new FlowerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Log.d("TAG", position + "");
//                Intent intent = new Intent(MainActivity.this, FlowerDetailActivity.class);
//                intent.putExtra("flower", flowerBeanArrayList.get(position));
//                startActivity(intent);
//            }
//        });
        orderList.setAdapter(orderAdapter);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: // 获取到Flower的列表，进行UI加载
                    final ArrayList<OrderBean> orderBeanArrayList = (ArrayList<OrderBean>) msg.obj;
                    if (orderBeanArrayList.size() != 0) {
                        setOrderList(orderBeanArrayList);
                    }
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
    private class UserOrderListTask extends AsyncTask<Void, Void, ArrayList<OrderBean>> {

        String orderListJson;

        /**
         * 后台执行异步任务
         *
         * @param params 参数
         * @return 成功返回Json生成的商品列表List，否则返回 null
         */
        @Override
        protected ArrayList<OrderBean> doInBackground(Void... params) {
            try {
                int userId = sharedPreferences.getInt(Constants.LOGGED_USER_ID,0);
                RequestBody requestBody = new FormBody.Builder().add("user_id", userId + "").build();
                Request request = new Request.Builder().url(Constants.getURL()
                        + "/order_list").post(requestBody).build();
                Response response = new OkHttpClient().newCall(request).execute();
                orderListJson = response.body().string();
                Log.i("TAG", orderListJson);
                Type type = new TypeToken<ArrayList<OrderBean>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(orderListJson, type);
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
         * @param orderBeanArrayList 商品列表 FlowerList
         */
        @Override
        protected void onPostExecute(ArrayList<OrderBean> orderBeanArrayList) {
            if (orderBeanArrayList != null) {
                Message message = new Message();
                message.arg1 = 1;
                message.obj = orderBeanArrayList;
                handler.handleMessage(message);
            }
        }
    }
}
