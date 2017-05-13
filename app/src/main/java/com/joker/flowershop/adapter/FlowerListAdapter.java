//package com.joker.flowershop.adapter;
//
//import android.content.Context;
//import android.net.Uri;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.joker.flowershop.R;
//import com.joker.flowershop.bean.FlowerBean;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
///**
// * 商品列表的适配器 Adapter
// * Created by joker on 5/5 0005.
// */
//public class FlowerListAdapter extends BaseAdapter {
//
//    private int resource;
//    private Context context;
//    private List<FlowerBean> flowerBeanList;
//    private LayoutInflater layoutInflater;
//
//    private TextView title;
//    private TextView introduction;
//    private ImageView image;
//    private TextView price;
//
//
//    public FlowerListAdapter(Context context, List<FlowerBean> flowerBeanList, int resource) {
//        this.context = context;
//        this.resource = resource;
//        this.flowerBeanList = flowerBeanList;
//        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return flowerBeanList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return flowerBeanList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(resource, null);
//        }
//        FlowerBean flowerBean = flowerBeanList.get(position);
//
//        title = (TextView) convertView.findViewById(R.id.title);
//        introduction = (TextView) convertView.findViewById(R.id.introduction);
//        price = (TextView) convertView.findViewById(R.id.price);
//        image = (ImageView) convertView.findViewById(R.id.image);
//
//        title.setText(flowerBean.getTitle());
//        introduction.setText(flowerBean.getIntroduction());
//        price.setText("￥ " + flowerBean.getPrice());
//        Log.d("TAG", flowerBean.getImageURL());
//        Uri uri = Uri.parse(flowerBean.getImageURL());
//        Picasso.with(context).load(uri).resize(150,150).centerCrop().placeholder(R.drawable.loading).into(image);
//
//        return convertView;
//    }
//}
