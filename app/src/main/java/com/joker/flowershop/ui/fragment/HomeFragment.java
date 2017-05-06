package com.joker.flowershop.ui.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.FlowerListAdapter;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.ui.FlowerDetailActivity;
import com.joker.flowershop.utils.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HomeFragment 显示商品列表
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView flowerList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        flowerList = (ListView) view.findViewById(R.id.flower_list);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), android.R.color.holo_orange_light),
                ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright),
                ContextCompat.getColor(getContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(getContext(), android.R.color.holo_red_light));

        getActivity().setTitle("网上花店");

        initListView();
        return view;
    }

    @Override
    public void onRefresh() {
        initListView();
    }

    /**
     * 初始化刷新列表
     */
    public void initListView() {
        swipeRefreshLayout.setRefreshing(true);
        UserFlowerListTask userFlowerListTask = new UserFlowerListTask();
        userFlowerListTask.execute((Void) null);
        swipeRefreshLayout.setRefreshing(false);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: // 获取到Flower的列表，进行UI加载
                    final ArrayList<FlowerBean> flowerBeanArrayList = (ArrayList<FlowerBean>) msg.obj;
                    if (flowerBeanArrayList.size()!=0){
                        FlowerListAdapter flowerListAdapter = new FlowerListAdapter
                                (getContext(), flowerBeanArrayList, R.layout.flower_item);
                        flowerList.setAdapter(flowerListAdapter);
                        setListViewHeightBasedOnChildren(flowerList);
                        flowerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), FlowerDetailActivity.class);
                                intent.putExtra("flower",flowerBeanArrayList.get(position));
                                startActivity(intent);
                            }
                        });
                    }
                    break;
            }
        }
    };

    /**
     * 保持 ListView 在 ScrollView 中正常显示
     *
     * @param listView 想要现实的ListView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
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
                Request request = new Request.Builder()
                        .url(Constants.getURL() + "/flower_list").get().build();
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
                message.arg1 = 1;
                message.obj = flowerBeanArrayList;
                handler.handleMessage(message);
            }
        }
    }
}
