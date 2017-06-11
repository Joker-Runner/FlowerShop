package com.joker.flowershop.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.joker.flowershop.R;
import com.joker.flowershop.adapter.recycler.ResourceItemDivider;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderStatusPopActivity extends Activity {

    @BindView(R.id.order_status_list)
    RecyclerView orderStatusList;
    @BindView(R.id.order_status_text)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_pop);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        int orderStatus = intent.getIntExtra("order_status", -1);
        title.setText("订单状态 " + String.valueOf(orderStatus));


        orderStatusList.setLayoutManager(new LinearLayoutManager(this));
        orderStatusList.addItemDecoration(new ResourceItemDivider(this, R.drawable.divider));

        //使用arrayAdapter
        String[] strings = {"从杭州分拣中心发出", "到达杭州分拣中心", "从杭州下沙发货","商品已出库","卖家发货"};
        OrderStatusPopRecyclerAdapter orderStatusPopRecyclerAdapter
                = new OrderStatusPopRecyclerAdapter(Arrays.asList(strings));
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, strings);
//        listView.setAdapter(arrayAdapter);
        orderStatusList.setAdapter(orderStatusPopRecyclerAdapter);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        this.overridePendingTransition(0, R.anim.pop_alpha_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        this.overridePendingTransition(0, R.anim.pop_alpha_out);
    }

    class OrderStatusPopRecyclerAdapter extends BaseQuickAdapter<String,BaseViewHolder>{


        public OrderStatusPopRecyclerAdapter( @Nullable List<String> data) {
            super(R.layout.order_status_item, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.order_status_item,item);
        }
    }
}
