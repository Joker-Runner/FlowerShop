package com.joker.flowershop.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;

public class FlowerDetailActivity extends AppCompatActivity implements View.OnClickListener{

    FlowerBean flowerBean;
    SimpleDraweeView image;
    TextView title;
    TextView price;
    TextView introduction;
    Button like;
    Button addCart;
    Button order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Fresco.initialize(this);

        image = (SimpleDraweeView) findViewById(R.id.image);
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
        image.setImageURI(uri);
        title.setText(flowerBean.getTitle());
        price.setText("￥ "+flowerBean.getPrice());
        introduction.setText(flowerBean.getIntroduction());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        switch (v.getId()){
            case R.id.like:
                break;
            case R.id.add_cart:
                Toast.makeText(this,"点击了加入购物车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.order:
                break;
            default:
                break;
        }
    }
}
