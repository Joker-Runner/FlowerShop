package com.joker.flowershop.adapter.recycler;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.bean.OrderBean;
import com.joker.flowershop.ui.order.OrderDetailActivity;
import com.joker.flowershop.ui.order.OrderStatusPopActivity;

import java.util.List;

/**
 * Created by joker on 5/12 0012.
 */
public class OrderAdapter extends BaseQuickAdapter<OrderBean, BaseViewHolder> {

    public OrderAdapter(@Nullable List<OrderBean> data) {
        super(R.layout.order_item, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final OrderBean item) {
        viewHolder.setText(R.id.title, item.getTitle())
                .setText(R.id.introduction, item.getIntroduction())
                .setText(R.id.price, "￥ " + item.getPrice())
                .addOnClickListener(R.id.order_status)
                .addOnClickListener(R.id.order_item);
        Uri uri = Uri.parse(item.getImageURL());
        Glide.with(mContext).load(uri).centerCrop().
                into((ImageView) viewHolder.getView(R.id.image));

        final FlowerBean flowerBean = new FlowerBean();
        flowerBean.setId(item.getFlowerId());
        flowerBean.setTitle(item.getTitle());
        flowerBean.setIntroduction(item.getIntroduction());
        flowerBean.setPrice(item.getPrice());
        flowerBean.setImageURL(item.getImageURL());

        viewHolder.getView(R.id.order_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", item.getOrder_status() + "");
                Intent intent = new Intent(mContext, OrderStatusPopActivity.class);
                intent.putExtra("order_status", item.getOrder_status());
                mContext.startActivity(intent);
            }
        });
        viewHolder.getView(R.id.order_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOrderDetail = new Intent(mContext, OrderDetailActivity.class);
                intentOrderDetail.putExtra("order", item);
                mContext.startActivity(intentOrderDetail);
            }
        });
    }
}