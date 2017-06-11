package com.joker.flowershop.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.joker.flowershop.R;
import com.joker.flowershop.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class HelpAndFeedbackActivity extends AppCompatActivity {

    @BindView(R.id.feedback_edit)
    EditText feedbackEdit;
    @BindView(R.id.submit_button)
    Button submitButton;

    private int userId;
    private String feedback = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_feedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        userId = getIntent().getIntExtra("user_id", 0);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedbackText = feedbackEdit.getText().toString();
                if (!TextUtils.isEmpty(feedbackText)) {
                    if (!feedback.equals(feedbackText)) {
                        feedback = feedbackText;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                submitFeedback(feedback);
                            }
                        }).start();
                    } else {
                        Toast.makeText(HelpAndFeedbackActivity.this,
                                "已经提交过了", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void submitFeedback(String feedback) {
        OkHttpUtils.post().url(Constants.getURL() + "/create_feedback")
                .addParams("user_id", String.valueOf(userId))
                .addParams("feedback", feedback).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response.equals("true")) {
                            Toast.makeText(HelpAndFeedbackActivity.this,
                                    "发送成功", Toast.LENGTH_LONG).show();
                            HelpAndFeedbackActivity.this.finish();
                        } else {
                            Toast.makeText(HelpAndFeedbackActivity.this,
                                    "发送失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
