package com.joker.flowershop.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.UserBean;
import com.joker.flowershop.utils.Constants;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingUsernameActivity extends AppCompatActivity {

    @BindView(R.id.username_edit)
    EditText usernameEdit;

    UserBean userBean;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    SettingUsernameAsyncTask settingUsernameAsyncTask = null;

    /**
     * Handler标签
     */
    private final static int HANDLER_SETTING_USERNAME_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_username);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        userBean = (UserBean) intent.getSerializableExtra(MeActivity.INTENT_USER_BEAN);
        usernameEdit.setText(userBean.getUsername());
    }

    public void attemptSaveUsername() {
        String username = usernameEdit.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if (settingUsernameAsyncTask == null) {
                settingUsernameAsyncTask = new SettingUsernameAsyncTask(userBean.getId(), username);
                settingUsernameAsyncTask.execute((Void[]) null);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SETTING_USERNAME_TAG:
                    if (msg.arg1 == 1) {
                        Toast.makeText(SettingUsernameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        String username = usernameEdit.getText().toString();
                        userBean.setUsername(username);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        String userBeanJson = gsonBuilder.create().toJson(userBean);
                        editor.putString(Constants.LOGGED_USER_JSON, userBeanJson).commit();
                        Intent intent = new Intent();
                        intent.putExtra("username", username);
                        SettingUsernameActivity.this.setResult(RESULT_OK, intent);
                        SettingUsernameActivity.this.finish();
                    }

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 1) {
            attemptSaveUsername();
        } else if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    class SettingUsernameAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private int userId;
        private String username;

        private Response response;
        private String responseData;

        public SettingUsernameAsyncTask(int userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("id", String.valueOf(userId))
                        .add("username", username)
                        .build();
                Request request = new Request.Builder()
                        .url(Constants.getURL() + "/setting_username")
                        .post(requestBody).build();
                response = new OkHttpClient().newCall(request).execute();
                responseData = response.body().string();
                return responseData.equals("true");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            settingUsernameAsyncTask = null;
            if (success) {
                Message message = new Message();
                message.what = HANDLER_SETTING_USERNAME_TAG;
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        }

        @Override
        protected void onCancelled() {
            settingUsernameAsyncTask = null;
        }
    }
}
