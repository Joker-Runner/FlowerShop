package com.joker.flowershop.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.FlowerAdapter;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.adapter.recycler.SearchResultAdapter;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.utils.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchResultActivity extends AppCompatActivity {

    private static final int HANDLER_SHOW_SEARCH_LIST_TAG = 1;

    private RecyclerView searchResultRecycler;
    private SearchResultAdapter searchResultAdapter;
    private ArrayList<FlowerBean> searchResultArrayList;

    private String queryKeyword;
    private SearchFlowerTask searchFlowerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();
        queryKeyword = getIntent().getStringExtra("query_keyword");
        setTitle(queryKeyword+"...");
        searchFlowerTask = new SearchFlowerTask(queryKeyword);
        searchFlowerTask.execute();

    }

    private void initRecyclerView() {
        searchResultRecycler = (RecyclerView) findViewById(R.id.search_result_recycler_view);
        searchResultRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchResultRecycler.addItemDecoration(new ResourceItemDivider(this, R.drawable.divider));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case HANDLER_SHOW_SEARCH_LIST_TAG:
                    searchResultArrayList = (ArrayList<FlowerBean>) msg.obj;
                    searchResultAdapter = new SearchResultAdapter(searchResultArrayList);
                    searchResultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent intent = new Intent(SearchResultActivity.this, FlowerDetailActivity.class);
                            intent.putExtra("flower", searchResultArrayList.get(position));
                            startActivity(intent);
                        }
                    });
                    searchResultRecycler.setAdapter(searchResultAdapter);
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
     * 搜索商品
     */
    private class SearchFlowerTask extends AsyncTask<Void, Void, ArrayList<FlowerBean>> {

        private String keyword;

        SearchFlowerTask(String keyword) {
            this.keyword = keyword;
        }

        @Override
        protected ArrayList<FlowerBean> doInBackground(Void... params) {
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("keyword", keyword).build();
                Request request = new Request.Builder()
                        .url(Constants.getURL() + "/search_flowers").post(requestBody).build();
                Response response = new OkHttpClient().newCall(request).execute();
                String flowerListJson = response.body().string();
                Log.i("TAG", flowerListJson);
                Type type = new TypeToken<ArrayList<FlowerBean>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(flowerListJson, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<FlowerBean> flowerBeanArrayList) {
            searchFlowerTask = null;
            if (flowerBeanArrayList != null) {
                Message message = new Message();
                message.arg1 = HANDLER_SHOW_SEARCH_LIST_TAG;
                message.obj = flowerBeanArrayList;
                handler.handleMessage(message);
            }
        }

        @Override
        protected void onCancelled() {
            searchFlowerTask = null;
        }
    }

}
