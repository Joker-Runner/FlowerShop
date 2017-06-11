package com.joker.flowershop.ui.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.joker.flowershop.R;
import com.joker.flowershop.bean.UserBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class MyQRCodeActivity extends AppCompatActivity {

    @BindView(R.id.my_qr_code)
    ImageView myQRCode;

    private UserBean userBean;

    private GenerateQRCodeTask generateQRCodeTask = null;

    /**
     * Handler标签
     */
    private final static int HANDLER_GENERATE_QR_CODE_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        userBean = (UserBean) intent.getSerializableExtra(MeActivity.INTENT_USER_BEAN);

        if (generateQRCodeTask == null) {
            generateQRCodeTask = new GenerateQRCodeTask(userBean.getEmail());
            generateQRCodeTask.execute((Void[]) null);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_GENERATE_QR_CODE_TAG:
                    Bitmap qrCode = (Bitmap) msg.obj;
                    myQRCode.setImageBitmap(qrCode);
                    // Glide 不支持Bitmap
//                    Glide.with(MyQRCodeActivity.this).load(qrCode).into(myQRCode);
                    break;
            }
        }
    };

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

    class GenerateQRCodeTask extends AsyncTask<Void, Void, Bitmap> {

        String userMessage;

        public GenerateQRCodeTask(String userMessage) {
            this.userMessage = userMessage;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap logoBitmap = BitmapFactory.decodeResource
                    (MyQRCodeActivity.this.getResources(), R.drawable.logo);
            return QRCodeEncoder.syncEncodeQRCode(userMessage,
                    BGAQRCodeUtil.dp2px(MyQRCodeActivity.this, 150),
                    Color.parseColor("#000000"), logoBitmap);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            generateQRCodeTask = null;
            if (bitmap != null) {
                Message message = new Message();
                message.what = HANDLER_GENERATE_QR_CODE_TAG;
                message.obj = bitmap;
                handler.sendMessage(message);
            } else {
                Toast.makeText(MyQRCodeActivity.this, "生成二维码失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
