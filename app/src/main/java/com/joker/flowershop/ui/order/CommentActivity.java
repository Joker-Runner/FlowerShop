package com.joker.flowershop.ui.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.joker.flowershop.R;
import com.joker.flowershop.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.Call;

public class CommentActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.rating_bar)
    MaterialRatingBar ratingBar;
    @BindView(R.id.comment)
    EditText commentEdit;

    private int userId;
    private int flowerId;
    private String title;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", 0);
        flowerId = intent.getIntExtra("flower_id", 0);
        title = intent.getStringExtra("title");
        imageURL = intent.getStringExtra("image");

        titleView.setText(title);
        Glide.with(this).load(imageURL).centerCrop().into(imageView);
        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                Toast.makeText(CommentActivity.this, rating + "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void attemptComment() {
        final int rate = (int) (2 * ratingBar.getRating());
        final String comment = commentEdit.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定提交？");
        builder.setMessage(rate + "分\n" + comment);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OkHttpUtils.post().url(Constants.getURL() + "/create_comment")
                        .addParams("user_id", String.valueOf(userId))
                        .addParams("flower_id", String.valueOf(flowerId))
                        .addParams("rate", String.valueOf(rate))
                        .addParams("comment", comment).build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(CommentActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (response.equals("true")) {
                                    Toast.makeText(CommentActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    CommentActivity.this.finish();
                                } else {
                                    Toast.makeText(CommentActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "提交").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 1) {
            attemptComment();
        } else if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
