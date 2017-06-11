//package com.joker.flowershop.ui.setting;
//
//import android.os.AsyncTask;
//
//import com.joker.flowershop.bean.UserBean;
//import com.joker.flowershop.utils.Constants;
//
//import java.io.IOException;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
///**
// * 更改昵称、密码
// * Created by joker on 5/20 0020.
// */
//
//class UserSettingAsyncTask extends AsyncTask<Void, Void, Boolean> {
//
//    private UserBean userBean;
//
//    private Response response;
//    private String responseData;
//
//    public UserSettingAsyncTask(UserBean userBean) {
//        this.userBean = userBean;
//    }
//
//    @Override
//    protected Boolean doInBackground(Void... params) {
//        try {
//            RequestBody requestBody = new FormBody.Builder()
//                    .add("id", String.valueOf(userBean.getId()))
//                    .add("username", userBean.getUsername())
////                    .add("email", userBean.getEmail())
//                    .add("password", userBean.getPassWord())
////                    .add("icon",userBean.getIcon())
//                    .build();
//            Request request = new Request.Builder()
//                    .url(Constants.getURL() + "/user_bean_setting")
//                    .post(requestBody).build();
//            response = new OkHttpClient().newCall(request).execute();
//            responseData = response.body().string();
//            return responseData.equals("true");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    protected void onPostExecute(Boolean success) {
//        if (success){
//
//        }
//    }
//
//    @Override
//    protected void onCancelled() {
//        super.onCancelled();
//    }
//}
