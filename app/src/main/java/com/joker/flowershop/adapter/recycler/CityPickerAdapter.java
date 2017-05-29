package com.joker.flowershop.adapter.recycler;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.joker.flowershop.R;

import java.util.List;

/**
 * Created by joker on 5/20 0020.
 */

public class CityPickerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public CityPickerAdapter(@Nullable List<String> data) {
        super(R.layout.city_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.city_text, item);
    }
}
