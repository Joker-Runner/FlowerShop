package com.joker.flowershop.adapter.recycler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.bean.OrderBean;
import com.joker.flowershop.ui.FlowerDetailActivity;
import com.joker.flowershop.ui.order.OrderStatusDialogFragment;
import com.joker.flowershop.ui.order.OrderStatusPopActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 * Created by joker on 5/12 0012.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    List<OrderBean> orderBeanList;

//    private OnItemClickListener mOnItemClickListener = null;

    public OrderAdapter(Context context, List<OrderBean> orderBeanList) {
        this.context = context;
        this.orderBeanList = orderBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
//        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        final OrderBean orderBean = orderBeanList.get(position);
        final FlowerBean flowerBean = new FlowerBean();
        flowerBean.setId(orderBean.getFlowerId());
        flowerBean.setTitle(orderBean.getTitle());
        flowerBean.setIntroduction(orderBean.getIntroduction());
        flowerBean.setPrice(orderBean.getPrice());
        flowerBean.setImageURL(orderBean.getImageURL());
        holder.title.setText(orderBean.getTitle());
        holder.introduction.setText(orderBean.getIntroduction());
        holder.price.setText("￥ " + orderBean.getPrice());
        Uri uri = Uri.parse(orderBean.getImageURL());
        Picasso.with(context).load(uri).resize(150, 150).centerCrop()
                .placeholder(R.drawable.loading).into(holder.image);
        holder.orderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", orderBean.getOrder_status() + "");
                Intent intent = new Intent(context, OrderStatusPopActivity.class);
                intent.putExtra("order_status",orderBean.getOrder_status());
                context.startActivity(intent);
//                OrderStatusDialogFragment orderStatusDialogFragment = new OrderStatusDialogFragment();
//
//                orderStatusDialogFragment.show(((AppCompatActivity)context)
//                        .getSupportFragmentManager(),"orderDialog");
            }
        });
        holder.flowerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FlowerDetailActivity.class);
                intent.putExtra("flower", flowerBean);
                context.startActivity(intent);
            }
        });

        holder.orderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView introduction;
        private ImageView image;
        private TextView price;
        private Button orderDetail;
        private Button orderStatus;

        private LinearLayout flowerItem;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            introduction = (TextView) view.findViewById(R.id.introduction);
            price = (TextView) view.findViewById(R.id.price);
            image = (ImageView) view.findViewById(R.id.image);
            orderDetail = (Button) view.findViewById(R.id.order_detail);
            orderStatus = (Button) view.findViewById(R.id.order_status);
            flowerItem = (LinearLayout) view.findViewById(R.id.flower_item);
        }
    }
}
