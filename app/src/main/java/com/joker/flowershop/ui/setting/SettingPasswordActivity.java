package com.joker.flowershop.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SettingPasswordActivity extends AppCompatActivity {

    @BindView(R.id.old_password_edit)
    EditText oldPasswordEdit;
    @BindView(R.id.password_edit)
    EditText passwordEdit;
    @BindView(R.id.password_confirm_edit)
    EditText passwordConfirmEdit;

    private UserBean userBean;

    private SettingPasswordAsyncTask settingPasswordAsyncTask = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Handler标签
     */
    private final static int HANDLER_SETTING_PASSWORD_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        userBean = (UserBean) intent.getSerializableExtra(MeActivity.INTENT_USER_BEAN);
    }

    public void attemptSaveUsername() {
        String oldPassword = oldPasswordEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String passwordConfirm = passwordConfirmEdit.getText().toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
        } else if (!oldPassword.equals(userBean.getPassWord())) {
            Toast.makeText(this, "旧密码不正确", Toast.LENGTH_SHORT).show();
        } else if (password.equals(passwordConfirm)) {
            if (settingPasswordAsyncTask == null) {
                settingPasswordAsyncTask = new SettingPasswordAsyncTask(userBean.getId(), password);
                settingPasswordAsyncTask.execute((Void[]) null);
            }
        } else {
            Toast.makeText(this, "两次输入不相同", Toast.LENGTH_SHORT).show();
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SETTING_PASSWORD_TAG:
                    if (msg.arg1 == 1) {
                        Toast.makeText(SettingPasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        String password = passwordEdit.getText().toString();
                        userBean.setPassWord(password);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        String userBeanJson = gsonBuilder.create().toJson(userBean);
                        editor.putString(Constants.LOGGED_USER_JSON, userBeanJson).commit();
//                        Intent intent = new Intent();
//                        SettingPasswordActivity.this.setResult(RESULT_OK, intent);
                        SettingPasswordActivity.this.finish();
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


    class SettingPasswordAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private int userId;
        private String password;

        private Response response;
        private String responseData;

        public SettingPasswordAsyncTask(int userId, String password) {
            this.userId = userId;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("id", String.valueOf(userId))
                        .add("password", password)
//                    .add("email", userBean.getEmail())
//                        .add("password", userBean.getPassWord())
//                    .add("icon",userBean.getIcon())
                        .build();
                Request request = new Request.Builder()
                        .url(Constants.getURL() + "/setting_password")
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
            settingPasswordAsyncTask = null;
            if (success) {
                Message message = new Message();
                message.what = HANDLER_SETTING_PASSWORD_TAG;
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        }

        @Override
        protected void onCancelled() {
            settingPasswordAsyncTask = null;
        }
    }
}
