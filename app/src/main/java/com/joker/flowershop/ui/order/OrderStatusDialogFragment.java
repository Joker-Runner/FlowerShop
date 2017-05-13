package com.joker.flowershop.ui.order;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joker.flowershop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderStatusDialogFragment extends DialogFragment {


    public OrderStatusDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_status_dialog, null);

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.pop_activity_bg);
//        getDialog().getWindow().setTitle("订单状态");
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDialog().getWindow().setLayout((int)(displayMetrics.widthPixels*0.6),(int)(displayMetrics.heightPixels*0.6));

        ListView listView = (ListView) view.findViewById(R.id.order_status_list);
        TextView textView = (TextView) view.findViewById(R.id.order_status_text);

        //使用arrayAdapter
        String[] strings = {"Hello", "World", "Android", "Hello", "World", "Android",
                "Hello", "World", "Android", "Hello", "World", "Android"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(arrayAdapter);
        return view;
    }

}
