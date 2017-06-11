package com.joker.flowershop.ui.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.bean.ReceiverBean;
import com.joker.flowershop.ui.CreateNewAddressActivity;
import com.joker.flowershop.ui.FlowerDetailActivity;
import com.joker.flowershop.ui.MainActivity;
import com.joker.flowershop.utils.Constants;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private FlowerBean flowerBean;
    private ReceiverBean receiverBean;

    @BindView(R.id.receiver)
    LinearLayout receiverLayout;
    @BindView(R.id.addressee)
    TextView addresseeText;
    @BindView(R.id.telephone)
    TextView telephoneText;
    @BindView(R.id.address)
    TextView addressText;
    @BindView(R.id.remark)
    EditText remarkEdit;

    @BindView(R.id.flower)
    LinearLayout flowerLayout;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.introduction)
    TextView introduction;
    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.order)
    Button order;

    private String receiverJson;
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
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = this.getIntent();
        flowerBean = (FlowerBean) intent.getSerializableExtra("flower");

        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);

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
        receiverJson = sharedPreferences.getString(Constants.DEFAULT_RECEIVER, null);
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
        Glide.with(this).load(flowerBean.getImageURL()).centerCrop().into(image);
        totalPrice.setText(flowerBean.getPrice() + "");
    }

    public void attemptCreateOrder() {
        if (receiverJson == null) {
            Toast.makeText(CreateOrderActivity.this, "请填写收货地址", Toast.LENGTH_LONG).show();
        } else {
            areYouSureOrder();
        }
    }

    public void areYouSureOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateOrderActivity.this);
        builder.setTitle("确定要下单吗？");
        builder.setMessage("这里省去了付款的流程");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remark = remarkEdit.getText().toString();
                if (createOrderTask == null) {
                    createOrderTask = new CreateOrderTask(receiverBean.getAddressee(),
                            receiverBean.getTelephone(), receiverBean.getAddress(), remark,
                            flowerBean.getId(), sharedPreferences.getInt(Constants.LOGGED_USER_ID, 0),
                            Constants.WAITING_FOR_DELIVERY);
                    createOrderTask.execute((Void) null);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
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

        CreateOrderTask(String addressee, String telephone, String address,
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
