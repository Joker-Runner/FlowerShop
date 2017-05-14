package com.joker.flowershop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.FlowerAdapter;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.ui.order.OrderActivity;
import com.joker.flowershop.ui.qrcode.ScanActivity;
import com.joker.flowershop.ui.subject.SubjectActivity;
import com.joker.flowershop.utils.Constants;
import com.joker.flowershop.utils.msc.JsonParser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView flowerList;
    private MaterialSearchView searchView;

    private DrawerLayout drawer;
    private ImageView nav_icon; // 头像
    private TextView userName; //用户名
    private TextView city;
    private LinearLayout selectCity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private UserFlowerListTask userFlowerListTask = null;
    private SearchFlowerTask searchFlowerTask = null;

    /**
     * Handler标签
     */
    private final static int HANDLER_SHOW_FLOWER_LIST_TAG = 1;
    private final static int HANDLER_SPEECH_RESULT_TAG = 2;


    /**
     * 讯飞语音识别MSC
     */
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.MainActivityTheme_NoActionBar);
        this.getWindow().setBackgroundDrawableResource(R.drawable.main_activity_bg);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        flowerList = (RecyclerView) findViewById(R.id.flower_recycler_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                ContextCompat.getColor(this, android.R.color.holo_blue_bright),
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                ContextCompat.getColor(this, android.R.color.holo_red_light));

        setSearchView();
        initMSC();
        initRecyclerList();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        userName = (TextView) headerView.findViewById(R.id.nav_user_name);
        nav_icon = (ImageView) headerView.findViewById(R.id.nav_icon);
        city = (TextView) headerView.findViewById(R.id.city);
        selectCity = (LinearLayout) headerView.findViewById(R.id.select_city);
        nav_icon.setOnClickListener(this);
        selectCity.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        editor = sharedPreferences.edit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
            nav_icon.setImageResource(R.drawable.icon_default);
            userName.setText("Joker_Runner");
        } else {
            nav_icon.setImageResource(R.drawable.icon_none);
            userName.setText(getString(R.string.action_sign));
        }
    }

    @Override
    public void onRefresh() {
        initRecyclerList();
    }

    private void initRecyclerList() {
        setTitle(getString(R.string.app_name));
        swipeRefreshLayout.setRefreshing(true);
        userFlowerListTask = new UserFlowerListTask();
        userFlowerListTask.execute((Void) null);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setFlowerList(final ArrayList<FlowerBean> flowerBeanArrayList) {
        flowerList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        FlowerAdapter flowerAdapter = new FlowerAdapter(MainActivity.this, flowerBeanArrayList);
        flowerAdapter.setOnItemClickListener(new FlowerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("TAG", position + "");
                Intent intent = new Intent(MainActivity.this, FlowerDetailActivity.class);
                intent.putExtra("flower", flowerBeanArrayList.get(position));
                startActivity(intent);
            }
        });
        flowerList.setAdapter(flowerAdapter);
        flowerList.addItemDecoration(new ResourceItemDivider(this, R.drawable.divider));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case HANDLER_SHOW_FLOWER_LIST_TAG: // 获取到Flower的列表，进行UI加载
                    final ArrayList<FlowerBean> flowerBeanArrayList = (ArrayList<FlowerBean>) msg.obj;
                        setFlowerList(flowerBeanArrayList);
                    break;
                case HANDLER_SPEECH_RESULT_TAG: // 语音识别成功
                    String result = (String) msg.obj;
                    if (!TextUtils.isEmpty(result)) {
                        searchView.setQuery(result, false);
                    }
                    break;
            }
        }
    };

    private void setSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnVoiceClickListener(new MaterialSearchView.OnVoiceClickListener() {
            @Override
            public boolean onVoiceClick() {
                if (null == mIat) {
                    // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                    Toast.makeText(MainActivity.this,"创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化",Toast.LENGTH_SHORT).show();
                    return false;
                }

                // 移动数据分析，收集开始听写事件
                FlowerCollector.onEvent(MainActivity.this, "iat_recognize");

//                mResultText.setText(null);// 清空显示内容
                mIatResults.clear();
                // 设置参数
                setParameter();
                // 显示听写对话框
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFlowerTask = new SearchFlowerTask(query);
                searchFlowerTask.execute((Void) null);
                MainActivity.this.setTitle(query + " ...");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_LOGIN:
                if (resultCode == RESULT_OK && data.getBooleanExtra(Constants.LOGGED_IN, false)) {
                    nav_icon.setImageResource(R.drawable.icon_default);
                    userName.setText("Joker_Runner");
                    editor.putBoolean(Constants.LOGGED_IN, true).commit();
                }
                break;
            case MaterialSearchView.REQUEST_VOICE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && matches.size() > 0) {
                        String searchWrd = matches.get(0);
                        if (!TextUtils.isEmpty(searchWrd)) {
                            searchView.setQuery(searchWrd, false);
                        }
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (id) {
//                    case R.id.nav_home:
////                        HomeFragment homeFragment = new HomeFragment();
////                        setMainContent(homeFragment);
//                        break;
                    case R.id.nav_subject:
                        // 点击后立即点返回会 NullPointerException
                        Intent intentSubject = new Intent(MainActivity.this, SubjectActivity.class);
                        startActivity(intentSubject);
                        break;
                    case R.id.nav_star:
                        break;
                    case R.id.nav_shopping_car:
                        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
                            Intent intentShoppingCart = new Intent(MainActivity.this, ShoppingCartActivity.class);
                            startActivity(intentShoppingCart);
                        } else {
                            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intentLogin, 0);
                        }
                        break;
                    case R.id.nav_order:
                        Intent intentOrder = new Intent(MainActivity.this, OrderActivity.class);
                        startActivity(intentOrder);
                        break;
                    case R.id.nav_scan:
                        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_settings:
                        Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_help_feedback:
                        break;
                    case R.id.nav_share:
                        break;
                    default:
                        break;
                }
            }
        }, 200);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_icon:
                if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
                    Toast.makeText(MainActivity.this, "已经登录", Toast.LENGTH_LONG).show();
                } else {
                    drawer.closeDrawer(GravityCompat.START);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
                        }
                    }, 200);
                }
                break;
            case R.id.select_city:
                Toast.makeText(MainActivity.this, "切换城市", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    /**
     * 讯飞语音识别
     */
    public void initMSC() {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = com.iflytek.cloud.SpeechRecognizer.createRecognizer(this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(this, mInitListener);
    }

    /**
     * 初始化监听器
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(MainActivity.this,"初始化失败，错误码：" + code,Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        String result = resultBuffer.toString();

        Message message = new Message();
        message.arg1 = HANDLER_SPEECH_RESULT_TAG;
        message.obj = result;
        handler.sendMessage(message);
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            Toast.makeText(MainActivity.this,error.getPlainDescription(true),Toast.LENGTH_SHORT).show();
        }

    };

    /**
     * 参数设置
     */
    public void setParameter() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
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
                message.arg1 = HANDLER_SHOW_FLOWER_LIST_TAG;
                message.obj = flowerBeanArrayList;
                handler.handleMessage(message);
            }
        }

        @Override
        protected void onCancelled() {
            searchFlowerTask = null;
        }
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
                message.arg1 = HANDLER_SHOW_FLOWER_LIST_TAG;
                message.obj = flowerBeanArrayList;
                handler.handleMessage(message);
            }
        }
    }
}
