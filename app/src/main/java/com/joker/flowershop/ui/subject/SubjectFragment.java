package com.joker.flowershop.ui.subject;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.FlowerAdapter;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.ui.FlowerDetailActivity;
import com.joker.flowershop.utils.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectFragment extends Fragment {

    //    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView flowerList;

    public SubjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject, container, false);
        flowerList = (RecyclerView) view.findViewById(R.id.subject_view);
        initListView();
        return view;
    }

    /**
     * 初始化刷新列表
     */
    public void initListView() {
//        swipeRefreshLayout.setRefreshing(true);
        UserFlowerListTask userFlowerListTask = new UserFlowerListTask();
        userFlowerListTask.execute((Void) null);
//        swipeRefreshLayout.setRefreshing(false);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: // 获取到Flower的列表，进行UI加载
                    final ArrayList<FlowerBean> flowerBeanArrayList = (ArrayList<FlowerBean>) msg.obj;
                    if (flowerBeanArrayList.size() != 0) {
                        flowerList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        FlowerAdapter flowerAdapter = new FlowerAdapter(getContext(), flowerBeanArrayList);
                        flowerAdapter.setOnItemClickListener(new FlowerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Log.d("TAG",position+"");
                                Intent intent = new Intent(getContext(), FlowerDetailActivity.class);
                                intent.putExtra("flower", flowerBeanArrayList.get(position));
                                startActivity(intent);
                            }
                        });
                        flowerList.setAdapter(flowerAdapter);
                        flowerList.addItemDecoration(new ResourceItemDivider(getContext(),R.drawable.divider));
                    }
                    break;
            }
        }
    };

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
