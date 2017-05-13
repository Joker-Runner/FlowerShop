package com.joker.flowershop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.ui.order.CreateOrderActivity;
import com.joker.flowershop.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlowerDetailActivity extends AppCompatActivity implements View.OnClickListener {

    FlowerBean flowerBean;
    ImageView image;
    TextView title;
    TextView price;
    TextView introduction;
    Button like;
    Button addCart;
    Button order;

    AddShoppingCartTask addShoppingCartTask;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences(Constants.INIT_SETTING_SHARED, MODE_APPEND);

        image = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        price = (TextView) findViewById(R.id.price);
        introduction = (TextView) findViewById(R.id.introduction);
        like = (Button) findViewById(R.id.like);
        addCart = (Button) findViewById(R.id.add_cart);
        order = (Button) findViewById(R.id.order);

        like.setOnClickListener(this);
        addCart.setOnClickListener(this);
        order.setOnClickListener(this);

        Intent intent = this.getIntent();
        flowerBean = (FlowerBean) intent.getSerializableExtra("flower");
        setTitle(flowerBean.getTitle());
        Uri uri = Uri.parse(flowerBean.getImageURL());
        Picasso.with(this).load(uri).resize(2000, 2000).centerCrop().into(image);
        title.setText(flowerBean.getTitle());
        price.setText("￥ " + flowerBean.getPrice());
        introduction.setText(flowerBean.getIntroduction());

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
        if (sharedPreferences.getBoolean(Constants.LOGGED_IN, false)) {
            switch (v.getId()) {
                case R.id.like:
                    break;
                case R.id.add_cart:
                    addShoppingCartTask = new AddShoppingCartTask(flowerBean.getId(), 1);
                    addShoppingCartTask.execute((Void) null);
                    break;
                case R.id.order:
                    Intent intent = new Intent(this,CreateOrderActivity.class);
                    intent.putExtra("flower",flowerBean);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private class AddShoppingCartTask extends AsyncTask<Void, Void, Boolean> {

        int flowerId;
        int userId;

        AddShoppingCartTask(int flowerId, int userId) {
            this.flowerId = flowerId;
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String responseData = null;
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("flower_id", flowerId + "").add("user_id", userId + "").build();
                Request request = new Request.Builder()
                        .url(Constants.getURL() + "/add_shopping_cart").post(requestBody).build();
                Response response = new OkHttpClient().newCall(request).execute();
                responseData = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseData != null && responseData.equals("true");
        }

        @Override
        protected void onPostExecute(Boolean success) {
            addShoppingCartTask = null;
            if (success) {
                Toast.makeText(FlowerDetailActivity.this, "成功加入购物车", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            addShoppingCartTask = null;
        }
    }
}
