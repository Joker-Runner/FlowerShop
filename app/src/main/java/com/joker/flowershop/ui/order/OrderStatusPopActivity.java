package com.joker.flowershop.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joker.flowershop.R;

public class OrderStatusPopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_pop);
        ListView listView = (ListView) findViewById(R.id.order_status_list);
        TextView textView = (TextView) findViewById(R.id.order_status_text);

        Intent intent = getIntent();
        int orderStatus = intent.getIntExtra("order_status",-1);
        textView.setText("订单状态 "+String.valueOf(orderStatus));


        //使用arrayAdapter
        String[] strings = {"Hello", "World", "Android", "Hello", "World", "Android",
                "Hello", "World", "Android", "Hello", "World", "Android"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
//        overridePendingTransition(R.anim.translate_in,R.anim.scale_out);
        return true;
    }
}
