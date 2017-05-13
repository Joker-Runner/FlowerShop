package com.joker.flowershop.adapter.recycler;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 * Created by joker on 5/12 0012.
 */

public class FlowerAdapter extends RecyclerView.Adapter<FlowerAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private List<FlowerBean> flowerBeanList;

    private OnItemClickListener mOnItemClickListener = null;

    public FlowerAdapter(Context context, List<FlowerBean> flowerBeanList) {
        this.context = context;
        this.flowerBeanList = flowerBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flower_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        FlowerBean flowerBean = flowerBeanList.get(position);
        holder.title.setText(flowerBean.getTitle());
        holder.introduction.setText(flowerBean.getIntroduction());
        holder.price.setText("￥ " + flowerBean.getPrice());
        Log.d("TAG", flowerBean.getImageURL());
        Uri uri = Uri.parse(flowerBean.getImageURL());
        Picasso.with(context).load(uri).resize(150, 150).centerCrop()
                .placeholder(R.drawable.loading).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return flowerBeanList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        private TextView title;
        private TextView introduction;
        private ImageView image;
        private TextView price;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.title);
            introduction = (TextView) view.findViewById(R.id.introduction);
            price = (TextView) view.findViewById(R.id.price);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
