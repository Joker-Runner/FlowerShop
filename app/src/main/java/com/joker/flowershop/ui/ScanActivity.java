package com.joker.flowershop.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.joker.flowershop.R;

import cn.bingoogolapple.qrcode.core.QRCodeView;

public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate{

    QRCodeView qrCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("扫一扫");

        qrCodeView = (QRCodeView) findViewById(R.id.zxing_view);
        qrCodeView.setDelegate(this);
        qrCodeView.startCamera();
        qrCodeView.startSpot();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(ScanActivity.this,Scan1Activity.class);
//                startActivity(intent);
//            }
//        },2*1000);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(result);
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.setPositiveButton("继续扫码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                qrCodeView.startSpot();
            }
        });
        alertDialog.show();

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("打开相机出错");
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrCodeView.stopSpot();
        qrCodeView.stopCamera();
    }
}
