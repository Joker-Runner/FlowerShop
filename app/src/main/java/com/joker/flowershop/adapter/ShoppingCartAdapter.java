package com.joker.flowershop.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.FlowerBean;
import com.joker.flowershop.ui.FlowerDetailActivity;

import java.util.List;

/**
 * Created by joker on 5/5 0005.
 */

public class ShoppingCartAdapter extends BaseAdapter {

    private int resource;
    private Context context;
    private List<FlowerBean> flowerBeanList;
    private LayoutInflater layoutInflater;

    private LinearLayout flowerItem;
    private ToggleButton flower_checked;
    private TextView title;
    private TextView introduction;
    private SimpleDraweeView image;
    private TextView price;


    public ShoppingCartAdapter(Context context, List<FlowerBean> flowerBeanList, int resource) {
        this.context = context;
        this.resource = resource;
        this.flowerBeanList = flowerBeanList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Fresco.initialize(context);
    }

    @Override
    public int getCount() {
        return flowerBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return flowerBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(resource, null);
        }
        final FlowerBean flowerBean = flowerBeanList.get(position);

        flowerItem = (LinearLayout) convertView.findViewById(R.id.flower_item);
        flower_checked = (ToggleButton) convertView.findViewById(R.id.flower_checked);
        title = (TextView) convertView.findViewById(R.id.title);
        introduction = (TextView) convertView.findViewById(R.id.introduction);
        price = (TextView) convertView.findViewById(R.id.price);
        image = (SimpleDraweeView) convertView.findViewById(R.id.image);

        flowerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FlowerDetailActivity.class);
                intent.putExtra("flower",flowerBean);
                context.startActivity(intent);
            }
        });

        flower_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        title.setText(flowerBean.getTitle());
        introduction.setText(flowerBean.getIntroduction());
        price.setText("￥ " + flowerBean.getPrice());
        Log.d("TAG", flowerBean.getImageURL());
        Uri uri = Uri.parse(flowerBean.getImageURL());
        image.setImageURI(uri);

        return convertView;
    }
}
