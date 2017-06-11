package com.joker.flowershop.adapter.recycler;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.CityBean;

import java.util.List;

/**
 * Created by joker on 5/20 0020.
 */

public class CityPickerAdapter extends BaseQuickAdapter<CityBean, BaseViewHolder> {

    public CityPickerAdapter(@Nullable List<CityBean> data) {
        super(R.layout.city_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CityBean item) {
        helper.setText(R.id.city_text,item.getCity());
    }

//    @Override
//    protected void convert(BaseViewHolder helper, String item) {
//        helper.setText(R.id.city_text, item);
//    }
}
