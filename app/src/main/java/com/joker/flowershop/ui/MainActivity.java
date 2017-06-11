package com.joker.flowershop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.joker.flowershop.adapter.recycler.MainRecyclerAdapter;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;
import com.joker.flowershop.adapter.recycler.WrapContentLinearLayoutManager;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.bean.UserBean;
import com.joker.flowershop.ui.order.OrderActivity;
import com.joker.flowershop.ui.qrcode.ScanActivity;
import com.joker.flowershop.ui.setting.CityPickerPopActivity;
import com.joker.flowershop.ui.setting.MeActivity;
import com.joker.flowershop.ui.setting.SettingActivity;
import com.joker.flowershop.ui.subject.SubjectActivity;
import com.joker.flowershop.utils.Constants;
import com.joker.flowershop.utils.msc.JsonParser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
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

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.flower_recycler_list)
    RecyclerView flowerList;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private ImageView nav_icon; // 头像
    private TextView userName; //用户名
    private TextView city;
    private LinearLayout selectCity;

    private MainRecyclerAdapter mainRecyclerAdapter;
    private ArrayList<FlowerBean> flowerBeanArrayList = new ArrayList<>();
    private int flowerListSize = 0;


    String userBeanJson;
    UserBean userBean;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private UserFlowerListTask userFlowerListTask = null;

    Uri noticeUri;


    /**
     * Handler标签
     */
    private final static int HANDLER_SHOW_FLOWER_LIST_TAG = 1;
    private final static int HANDLER_SPEECH_RESULT_TAG = 2;
    private final static int HANDLER_NOTICE_RESULT_TAG = 3;

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
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                ContextCompat.getColor(this, android.R.color.holo_blue_bright),
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                ContextCompat.getColor(this, android.R.color.holo_red_light));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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

        initMSC();
        setSearchView();
        initRecyclerView();
        resetRecyclerList();
        initHeaderView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
            userBeanJson = sharedPreferences.getString(Constants.LOGGED_USER_JSON, "{}");
            Type type = new TypeToken<UserBean>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            userBean = gson.fromJson(userBeanJson, type);
            if (userBean != null) {
                Glide.with(this).load(userBean.getIcon()).centerCrop().into(nav_icon);
                userName.setText(userBean.getUsername());
                city.setText(userBean.getCity());
            }
        } else {
            nav_icon.setImageResource(R.drawable.icon_none);
            userName.setText(getString(R.string.action_sign));
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        resetRecyclerList();
        swipeRefreshLayout.setRefreshing(false);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
//                Toast.makeText(MainActivity.this, "滑到最底部了，去加载更多吧！", Toast.LENGTH_SHORT).show();
                flowerListSize += 6;
                // TODO 添加列表
                if (userFlowerListTask == null) {
                    userFlowerListTask = new UserFlowerListTask(flowerListSize);
                    userFlowerListTask.execute((Void[]) null);
                }
            }
        }
    };


    private void initHeaderView() {
        OkHttpUtils.get().url(Constants.getURL() + "/get_notice").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                noticeUri = Uri.parse(response.toString());
                Message message = new Message();
                message.arg1 = HANDLER_NOTICE_RESULT_TAG;
                message.obj = noticeUri;
                handler.sendMessage(message);
            }
        });
    }


    private void initRecyclerView() {
        try {
            flowerList.setLayoutManager(new WrapContentLinearLayoutManager(MainActivity.this));
        } catch (Exception e) {
            Log.e("TAG", "LinearLayoutManager Exception");
        }
        flowerList.addItemDecoration(new ResourceItemDivider(this, R.drawable.divider));
        flowerList.addOnScrollListener(onScrollListener);

        mainRecyclerAdapter = new MainRecyclerAdapter(flowerBeanArrayList);
        mainRecyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d("TAG", position + "");
                Intent intent = new Intent(MainActivity.this, FlowerDetailActivity.class);
                intent.putExtra("flower", flowerBeanArrayList.get(position));
                startActivity(intent);
            }
        });
        flowerList.setAdapter(mainRecyclerAdapter);
    }

    private void resetRecyclerList() {
        setTitle(getString(R.string.app_name));
        flowerBeanArrayList.clear();
        flowerListSize = 0;
        if (userFlowerListTask == null) {
            userFlowerListTask = new UserFlowerListTask(0);
            userFlowerListTask.execute((Void) null);
        }
    }

    private View getHeaderView(int type, Uri uri, View.OnClickListener onClickListener) {
        View view = getLayoutInflater().inflate(R.layout.header_item, (ViewGroup) flowerList.getParent(), false);

        if (type == 1) {
            ImageView imageView = (ImageView) view.findViewById(R.id.header_image);

            if (uri != null) {
                Glide.with(this).load(uri).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.google);
            }
        }
        view.setOnClickListener(onClickListener);
        return view;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case HANDLER_SHOW_FLOWER_LIST_TAG: // 获取到Flower的列表，进行UI加载
                    flowerBeanArrayList.addAll((ArrayList<FlowerBean>) msg.obj);
                    mainRecyclerAdapter.notifyDataSetChanged();
                    break;
                case HANDLER_SPEECH_RESULT_TAG: // 语音识别成功
                    String result = (String) msg.obj;
                    if (!TextUtils.isEmpty(result)) {
                        searchView.setQuery(result, false);
                    }
                    break;
                case HANDLER_NOTICE_RESULT_TAG:
                    Uri noticeUri = (Uri) msg.obj;
                    mainRecyclerAdapter.addHeaderView(getHeaderView(1, noticeUri, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "HeaderView", Toast.LENGTH_SHORT).show();
                        }
                    }));
                    mainRecyclerAdapter.notifyItemRangeInserted(0, 1);
                    mainRecyclerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void setSearchView() {
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnVoiceClickListener(new MaterialSearchView.OnVoiceClickListener() {
            @Override
            public void onVoiceClick() {
                if (null == mIat) {
                    // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                    Toast.makeText(MainActivity.this, "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化", Toast.LENGTH_SHORT).show();
                    return;
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
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                intent.putExtra("query_keyword", query);
                startActivity(intent);
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
                if (resultCode == RESULT_OK && data.getBooleanExtra(Constants.LOGGED_IN_INTENT, false)) {
                    editor.putBoolean(Constants.LOGGED_IN, true).commit();
                    UserBean userBean = (UserBean) data.getSerializableExtra(Constants.LOGGED_USER_INTENT);
                    Log.d("TAG", userBean.getIcon());
                    Log.d("TAG", userBean.getUsername());
                    Glide.with(this).load(userBean.getIcon()).centerCrop().into(nav_icon);
                    userName.setText(userBean.getUsername());
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
                        city.setText(selectedCity);
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
                    case R.id.nav_subject:
                        // 点击后立即点返回会 NullPointerException
                        Intent intentSubject = new Intent(MainActivity.this, SubjectActivity.class);
                        startActivity(intentSubject);
                        break;
                    case R.id.nav_star:
                        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
                            Intent intentStar = new Intent(MainActivity.this, StarActivity.class);
                            startActivity(intentStar);
                        } else {
                            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intentLogin, Constants.REQUEST_CODE_LOGIN);
                        }
                        break;
                    case R.id.nav_shopping_car:
                        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
                            Intent intentShoppingCart = new Intent(MainActivity.this, ShoppingCartActivity.class);
                            startActivity(intentShoppingCart);
                        } else {
                            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intentLogin, Constants.REQUEST_CODE_LOGIN);
                        }
                        break;
                    case R.id.nav_order:
                        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
                            Intent intentOrder = new Intent(MainActivity.this, OrderActivity.class);
                            startActivity(intentOrder);
                        } else {
                            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intentLogin, Constants.REQUEST_CODE_LOGIN);
                        }
                        break;
                    case R.id.nav_scan:
                        Intent intentScan = new Intent(MainActivity.this, ScanActivity.class);
                        startActivity(intentScan);
                        break;
                    case R.id.nav_settings:
                        Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intentSetting);
                        break;
                    case R.id.nav_help_feedback:
                        Intent intentHelp = new Intent(MainActivity.this,HelpAndFeedbackActivity.class);
                        intentHelp.putExtra("user_id",userBean.getId());
                        startActivity(intentHelp);
                        break;
                    case R.id.nav_share:
                        Intent intentShare;
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
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
                            Intent intent = new Intent(MainActivity.this, MeActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
                        }
                    }
                }, 200);
                break;
            case R.id.select_city:
                Intent intent = new Intent(MainActivity.this, CityPickerPopActivity.class);
                startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_CITY);
//                Toast.makeText(MainActivity.this, "切换城市", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
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
     * 用于加载列表的异步任务
     */
    private class UserFlowerListTask extends AsyncTask<Void, Void, ArrayList<FlowerBean>> {

        private String flowerListJson;
        private int startNumber; // 请求的编号（startNumber——startNumber+6)

        UserFlowerListTask(int startNumber) {
            this.startNumber = startNumber;
        }

        /**
         * 后台执行异步任务
         *
         * @param params 参数
         * @return 成功返回Json生成的商品列表List，否则返回 null
         */
        @Override
        protected ArrayList<FlowerBean> doInBackground(Void... params) {
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("start_number", startNumber + "").build();
                Request request = new Request.Builder()
                        .url(Constants.getURL() + "/flower_list").post(requestBody).build();
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
            userFlowerListTask = null;
            if (flowerBeanArrayList != null) {
                Message message = new Message();
                message.arg1 = HANDLER_SHOW_FLOWER_LIST_TAG;
                message.obj = flowerBeanArrayList;
                handler.handleMessage(message);
            }
        }
    }
}
