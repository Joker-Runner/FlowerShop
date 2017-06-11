package com.joker.flowershop.adapter.recycler;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;

import java.util.List;

/**
 * Created by joker on 5/19 0019.
 */
public class SearchResultAdapter extends BaseQuickAdapter<FlowerBean, BaseViewHolder> {

    public SearchResultAdapter(@Nullable List<FlowerBean> data) {
        super(R.layout.flower_item, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, FlowerBean flowerBean) {
        viewHolder.setText(R.id.title, flowerBean.getTitle())
                .setText(R.id.introduction, flowerBean.getIntroduction())
                .setText(R.id.price, flowerBean.getPrice() + "");
        Uri uri = Uri.parse(flowerBean.getImageURL());
        Glide.with(mContext).load(uri).centerCrop().
                into((ImageView) viewHolder.getView(R.id.image));
    }
}
