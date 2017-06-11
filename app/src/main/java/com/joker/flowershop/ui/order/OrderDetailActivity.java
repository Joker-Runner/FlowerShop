package com.joker.flowershop.ui.order;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.bean.OrderBean;
import com.joker.flowershop.ui.FlowerDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActivity extends AppCompatActivity {

    @BindView(R.id.order_status_title)
    TextView orderStatusTitle;
    @BindView(R.id.addressee)
    TextView addressee;
    @BindView(R.id.telephone)
    TextView telephone;
    @BindView(R.id.address)
    TextView address;

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

    //    @BindView(R.id.remark_layout)
//    LinearLayout remarkLayout;
    @BindView(R.id.remark)
    TextView remark;

    @BindView(R.id.order_comment)
    Button orderComment;
    @BindView(R.id.order_status)
    Button orderStatus;

    private OrderBean orderBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        orderBean = (OrderBean) getIntent().getSerializableExtra("order");

        initView();
    }

    private void initView() {
//        orderStatusTitle.setText(orderBean.getOrder_status());// TODO: 6/1 0001
        addressee.setText(orderBean.getAddressee());
        telephone.setText(orderBean.getTelephone());
        address.setText(orderBean.getAddress());
        title.setText(orderBean.getTitle());
        introduction.setText(orderBean.getIntroduction());
        price.setText("￥ " + orderBean.getPrice());
        Glide.with(this).load(orderBean.getImageURL()).centerCrop().into(image);
        if (orderBean.getRemark() == null || orderBean.getRemark().equals("")) {
            remark.setVisibility(View.GONE);
        } else {
            remark.setText(orderBean.getRemark());
        }

        flowerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlowerBean flowerBean = new FlowerBean();
                flowerBean.setId(orderBean.getFlowerId());
                flowerBean.setTitle(orderBean.getTitle());
                flowerBean.setIntroduction(orderBean.getIntroduction());
                flowerBean.setPrice(orderBean.getPrice());
                flowerBean.setImageURL(orderBean.getImageURL());

                Intent intentFlower = new Intent(OrderDetailActivity.this, FlowerDetailActivity.class);
                intentFlower.putExtra("flower", flowerBean);
                startActivity(intentFlower);
            }
        });

        orderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOrderStatus = new Intent(OrderDetailActivity.this, OrderStatusPopActivity.class);
                intentOrderStatus.putExtra("order_status", orderBean.getOrder_status());
                startActivity(intentOrderStatus);
            }
        });

        orderComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComment = new Intent(OrderDetailActivity.this,CommentActivity.class);
                intentComment.putExtra("flower_id",orderBean.getFlowerId());
                intentComment.putExtra("user_id",orderBean.getUserId());
                intentComment.putExtra("image",orderBean.getImageURL());
                intentComment.putExtra("title",orderBean.getTitle());
                startActivity(intentComment);
            }
        });
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
}
