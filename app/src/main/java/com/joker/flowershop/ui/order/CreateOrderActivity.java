package com.joker.flowershop.ui.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.bean.ReceiverBean;
import com.joker.flowershop.ui.CreateNewAddressActivity;
import com.joker.flowershop.ui.FlowerDetailActivity;
import com.joker.flowershop.ui.MainActivity;
import com.joker.flowershop.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private FlowerBean flowerBean;
    private ReceiverBean receiverBean;

    private LinearLayout receiverLayout;
    private TextView addresseeText;
    private TextView telephoneText;
    private TextView addressText;
    private EditText remarkEdit;

    private LinearLayout flowerLayout;
    private ImageView image;
    private TextView title;
    private TextView introduction;
    private TextView price;

    private TextView totalPrice;
    private Button order;

    private String addressee;
    private String telephone;
    private String address;
    private String remark;

    private CreateOrderTask createOrderTask = null;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = this.getIntent();
        flowerBean = (FlowerBean) intent.getSerializableExtra("flower");

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);

        receiverLayout = (LinearLayout) findViewById(R.id.receiver);
        addresseeText = (TextView) findViewById(R.id.addressee);
        telephoneText = (TextView) findViewById(R.id.telephone);
        addressText = (TextView) findViewById(R.id.address);
        remarkEdit = (EditText) findViewById(R.id.remark);

        flowerLayout = (LinearLayout) findViewById(R.id.flower);
        image = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        introduction = (TextView) findViewById(R.id.introduction);
        price = (TextView) findViewById(R.id.price);

        totalPrice= (TextView) findViewById(R.id.total_price);
        order = (Button) findViewById(R.id.order);

        receiverLayout.setOnClickListener(this);
        flowerLayout.setOnClickListener(this);
        order.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    public void initView() {
        String receiverJson = sharedPreferences.getString(Constants.DEFAULT_RECEIVER, null);
        if (receiverJson != null) {
            receiverBean = new Gson().fromJson(receiverJson, ReceiverBean.class);
            addresseeText.setText(receiverBean.getAddressee());
            telephoneText.setText(receiverBean.getTelephone());
            addressText.setText(receiverBean.getAddress());
        } else {
            addresseeText.setText("请添加收货人");
        }
        title.setText(flowerBean.getTitle());
        introduction.setText(flowerBean.getIntroduction());
        price.setText("￥" + flowerBean.getPrice());
        Picasso.with(this).load(flowerBean.getImageURL()).resize(150, 150).centerCrop().into(image);
        totalPrice.setText(flowerBean.getPrice()+"");
    }

    public void attemptCreateOrder() {
        remark = remarkEdit.getText().toString();
        if (createOrderTask == null) {
            createOrderTask = new CreateOrderTask(receiverBean.getAddressee(),
                    receiverBean.getTelephone(), receiverBean.getAddress(), remark,
                    flowerBean.getId(), sharedPreferences.getInt(Constants.LOGGED_USER_ID, 0),
                    Constants.WAITING_FOR_DELIVERY);
            createOrderTask.execute((Void) null);
        }
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.receiver:
                Intent intentNewAddress = new Intent(this, CreateNewAddressActivity.class);
                startActivity(intentNewAddress);
                break;
            case R.id.flower:
                Intent intentFlowerDetail = new Intent(CreateOrderActivity.this, FlowerDetailActivity.class);
                intentFlowerDetail.putExtra("flower", flowerBean);
                startActivity(intentFlowerDetail);
                break;
            case R.id.order:
                attemptCreateOrder();
                break;
        }
    }

    private class CreateOrderTask extends AsyncTask<Void, Void, Boolean> {

        private String addressee;
        private String telephone;
        private String address;
        private String remark;
        private int flowerId;
        private int userId;
        private int order_status;

        public CreateOrderTask(String addressee, String telephone, String address,
                               String remark, int flowerId, int userId, int order_status) {
            this.addressee = addressee;
            this.telephone = telephone;
            this.address = address;
            this.remark = remark;
            this.flowerId = flowerId;
            this.userId = userId;
            this.order_status = order_status;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String responseData = null;
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("flower_id", flowerId + "").add("user_id", userId + "")
                        .add("addressee", addressee).add("telephone", telephone)
                        .add("address", address).add("remark", remark)
                        .add("order_status", order_status + "").build();
                Request request = new Request.Builder()
                        .url(Constants.getURL() + "/create_order").post(requestBody).build();
                Response response = new OkHttpClient().newCall(request).execute();
                responseData = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseData != null && responseData.equals("true");
        }

        @Override
        protected void onPostExecute(Boolean success) {
            createOrderTask = null;
            if (success) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateOrderActivity.this);
                builder.setTitle("下单成功！");
                builder.setCancelable(true);
                builder.setPositiveButton("继续浏览", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreateOrderActivity.this.finish();
                    }
                });
                builder.setNegativeButton("完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CreateOrderActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.create();
                builder.show();
            }
        }

        @Override
        protected void onCancelled() {
            createOrderTask = null;
        }
    }
}
