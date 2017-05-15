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

import com.bumptech.glide.Glide;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.StarBean;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * 收藏夹Adapter
 * Created by joker on 5/14 0014.
 */

public class StarAdapter extends SwipeMenuAdapter<StarAdapter.DefaultViewHolder> {

    private Context context;
    private List<StarBean> starBeanList;

    private OnItemClickListener mOnItemClickListener;

    public StarAdapter(Context context, List<StarBean> starBeanList) {
        this.context = context;
        this.starBeanList = starBeanList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return starBeanList == null ? 0 : starBeanList.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.flower_item, parent, false);
    }

    @Override
    public StarAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StarAdapter.DefaultViewHolder holder, int position) {
        holder.itemView.setTag(position);
        StarBean starBean = starBeanList.get(position);
        holder.title.setText(starBean.getTitle());
        holder.introduction.setText(starBean.getIntroduction());
        holder.price.setText("￥ " + starBean.getPrice());
        Log.d("TAG", starBean.getImageURL());
        Uri uri = Uri.parse(starBean.getImageURL());
        Glide.with(context).load(uri).centerCrop()
                .placeholder(R.drawable.loading).into(holder.image);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemClickListener mOnItemClickListener;
        private TextView title;
        private TextView introduction;
        private ImageView image;
        private TextView price;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.title);
            introduction = (TextView) itemView.findViewById(R.id.introduction);
            price = (TextView) itemView.findViewById(R.id.price);
            image = (ImageView) itemView.findViewById(R.id.image);

        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
