package com.joker.flowershop.adapter.recycler;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.ui.FlowerDetailActivity;
import com.joker.flowershop.ui.ShoppingCartActivity;

import java.util.List;

/**
 * Created by joker on 5/14 0014.
 */

public class ShoppingCartAdapter extends BaseQuickAdapter<FlowerBean, BaseViewHolder> {


    public ShoppingCartAdapter(@LayoutRes int layoutResId, @Nullable List<FlowerBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final FlowerBean flowerBean) {
        viewHolder.setText(R.id.title, flowerBean.getTitle())
                .setText(R.id.introduction, flowerBean.getIntroduction())
                .setText(R.id.price, flowerBean.getPrice() + "")
                .addOnClickListener(R.id.flower_item);
        Uri uri = Uri.parse(flowerBean.getImageURL());
        Glide.with(mContext).load(uri).centerCrop().
                into((ImageView) viewHolder.getView(R.id.image));
        viewHolder.getView(R.id.flower_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FlowerDetailActivity.class);
                intent.putExtra("flower", flowerBean);
                mContext.startActivity(intent);
            }
        });
        ((ToggleButton) viewHolder.getView(R.id.flower_checked)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Message message = new Message();
                    message.arg1 = ShoppingCartActivity.HANDLER_CHECKED_FLOWER_TAG;
                    message.obj = flowerBean;
                    ((ShoppingCartActivity) mContext).handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.arg1 = ShoppingCartActivity.HANDLER_UNCHECKED_FLOWER_TAG;
                    message.obj = flowerBean;
                    ((ShoppingCartActivity) mContext).handler.sendMessage(message);
                }
            }
        });
    }
}
