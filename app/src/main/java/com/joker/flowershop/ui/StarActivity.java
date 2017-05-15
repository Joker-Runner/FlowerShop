package com.joker.flowershop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.adapter.recycler.StarAdapter;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.bean.StarBean;
import com.joker.flowershop.utils.Constants;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StarActivity extends AppCompatActivity {

    private SwipeMenuRecyclerView starRecyclerView;
    private StarAdapter starAdapter;
    private ArrayList<StarBean> starBeanArrayList;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        starRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.star_recycler);
        initRecyclerView();
        setRecyclerView();
    }

    public void initRecyclerView() {
        starRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        starRecyclerView.addItemDecoration(new ResourceItemDivider(this, R.drawable.divider));
    }

    public void setRecyclerView() {
        UserFlowerListTask userFlowerListTask = new UserFlowerListTask();
        userFlowerListTask.execute((Void) null);
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
//            int width = getResources().getDimensionPixelSize(R.dimen.item_height);
            int width = 150;
            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            { // 添加右侧的，如果不添加，则右侧不会出现菜单。
                SwipeMenuItem deleteItem = new SwipeMenuItem(StarActivity.this)
                        .setBackgroundDrawable(R.color.colorAccent)
//                        .setImage(R.drawable.ic_delete_white_24dp)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView
         *                        #RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            // 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {// 删除按钮被点击。
                UserRemoveFlowerTask userRemoveFlowerTask =
                        new UserRemoveFlowerTask(starBeanArrayList.get(adapterPosition).getId(), adapterPosition);
                userRemoveFlowerTask.execute((Void[]) null);
            }
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: // 获取到Flower的列表，进行UI加载
                    starBeanArrayList = (ArrayList<StarBean>) msg.obj;
                    if (starBeanArrayList.size() != 0) {
                        starRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
                        starRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
                        starAdapter = new StarAdapter(StarActivity.this, starBeanArrayList);
                        starAdapter.setOnItemClickListener(new StarAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Log.d("TAG", position + "");
                                StarBean starBean = starBeanArrayList.get(position);
                                FlowerBean flowerBean = new FlowerBean();
                                {
                                    flowerBean.setId(starBean.getFlowerId());
                                    flowerBean.setTitle(starBean.getTitle());
                                    flowerBean.setIntroduction(starBean.getIntroduction());
                                    flowerBean.setPrice(starBean.getPrice());
                                    flowerBean.setImageURL(starBean.getImageURL());
                                }
                                Intent intent = new Intent(StarActivity.this, FlowerDetailActivity.class);
                                intent.putExtra("flower", flowerBean);
                                startActivity(intent);
                            }
                        });
                        starRecyclerView.setAdapter(starAdapter);
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
    private class UserFlowerListTask extends AsyncTask<Void, Void, ArrayList<StarBean>> {

        String starListJson;

        /**
         * 后台执行异步任务
         *
         * @param params 参数
         * @return 成功返回Json生成的商品列表List，否则返回 null
         */
        @Override
        protected ArrayList<StarBean> doInBackground(Void... params) {
            try {
                RequestBody requestBody = new FormBody.Builder().add("user_id",
                        String.valueOf(sharedPreferences.getInt(Constants.LOGGED_USER_ID, 0))).build();
                Request request = new Request.Builder().url(Constants.getURL()
                        + "/star_list").post(requestBody).build();
                Response response = new OkHttpClient().newCall(request).execute();
                starListJson = response.body().string();
                Log.i("TAG", starListJson);
                Type type = new TypeToken<ArrayList<StarBean>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(starListJson, type);
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
         * @param starBeanArrayList 商品列表 FlowerList
         */
        @Override
        protected void onPostExecute(ArrayList<StarBean> starBeanArrayList) {
            if (starBeanArrayList != null) {
                Message message = new Message();
                message.arg1 = 1;
                message.obj = starBeanArrayList;
                handler.handleMessage(message);
            }
        }
    }

    /**
     * 用于加载列表的异步任务
     */
    private class UserRemoveFlowerTask extends AsyncTask<Void, Void, Boolean> {

        int starId;
        int adapterPosition;

        UserRemoveFlowerTask(int starId, int adapterPosition) {
            this.starId = starId;
            this.adapterPosition = adapterPosition;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                RequestBody requestBody = new FormBody.Builder().add("star_id",
                        String.valueOf(starId)).build();
                Request request = new Request.Builder().url(Constants.getURL()
                        + "/remove_star").post(requestBody).build();
                Response response = new OkHttpClient().newCall(request).execute();
                return response.body().string().equals("true");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
//                MyCustomToast.show(StarActivity.this,"移除成功",MyCustomToast.LENGTH_SHORT);
                Snackbar.make(starRecyclerView, "移除成功", Snackbar.LENGTH_SHORT).show();
                starBeanArrayList.remove(adapterPosition);
                starAdapter.notifyItemRemoved(adapterPosition);
            }
        }
    }
}
