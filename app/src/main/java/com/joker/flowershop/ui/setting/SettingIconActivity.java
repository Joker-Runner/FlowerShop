package com.joker.flowershop.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.joker.flowershop.R;
import com.joker.flowershop.bean.UserBean;
import com.joker.flowershop.utils.Constants;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SettingIconActivity extends TakePhotoActivity {

    @BindView(R.id.setting_icon_layout)
    RelativeLayout layout;
    @BindView(R.id.my_icon)
    ImageView myIcon;
    @BindView(R.id.setting_icon)
    Button settingIcon;

    UserBean userBean;

    private static final int REQUEST_CODE_CHOOSE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_icon);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        userBean = (UserBean) intent.getSerializableExtra(MeActivity.INTENT_USER_BEAN);
        Glide.with(this).load(userBean.getIcon()).into(myIcon);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResult = new Intent();
                intentResult.putExtra("icon",userBean.getIcon());
                SettingIconActivity.this.setResult(RESULT_OK,intentResult);
                SettingIconActivity.this.finish();
                SettingIconActivity.this.overridePendingTransition(0, R.anim.pop_alpha_out);
            }
        });
        settingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
//                intent1.setType("image/*");
//                intent1.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent1,9);

//                Matisse.from(SettingIconActivity.this)
//                        .choose(MimeType.ofImage(), false)
//                        .countable(true)
////                        .capture(true)
////                        .captureStrategy(new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
//                        .maxSelectable(1)
////                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
////                        .gridExpectedSize(120)
////                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
////                        .thumbnailScale(0.85f)
//                        .imageEngine(new GlideEngine())
//                        .forResult(REQUEST_CODE_CHOOSE);

                TakePhoto takePhoto = getTakePhoto();
                configTakePhotoOption(takePhoto);
                configCompress(takePhoto);
                takePhoto.onPickMultipleWithCrop(1, getCropOptions());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Glide.with(this).load(Matisse.obtainPathResult(data).get(0)).into(myIcon);
        }
    }

    @Override
    public void takeSuccess(TResult result) {
        final String filePath = result.getImage().getCompressPath();
        final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Glide.with(this).load(filePath).into(myIcon);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils.post().url(Constants.getURL() + "/setting_icon")
                        .addFile("icon", fileName, new File(filePath))
                        .addParams("user_id", String.valueOf(userBean.getId()))
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(SettingIconActivity.this,"出错",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response != null && !response.equals("") && !response.equals("false")) {
                            userBean.setIcon(response);
                            Glide.with(SettingIconActivity.this).load(response).into(myIcon);
                        }
                        Toast.makeText(SettingIconActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        takePhoto.setTakePhotoOptions(builder.create());

    }

    private void configCompress(TakePhoto takePhoto) {
        takePhoto.onEnableCompress(null, false);
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(102400)
                .setMaxPixel(800)
                .enableReserveRaw(true)
                .create();
        takePhoto.onEnableCompress(config, false);
    }

    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(800).setAspectY(800);
        builder.setWithOwnCrop(false);
        return builder.create();
    }

//    private FunctionOptions getFunctionOptions() {
//        FunctionOptions options = new FunctionOptions.Builder()
//                .setType(FunctionConfig.TYPE_IMAGE) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
//                .setCropMode(FunctionConfig.CROP_MODEL_1_1) // 裁剪模式 默认、1:1、3:4、3:2、16:9
//                .setCompress(true) //是否压缩
//                .setEnablePixelCompress(false) //是否启用像素压缩
//                .setEnableQualityCompress(false) //是否启质量压缩
////                .setMaxSelectNum(1) // 可选择图片的数量
////                .setMinSelectNum()// 图片或视频最低选择数量，默认代表无限制
//                .setSelectMode(FunctionConfig.MODE_SINGLE) // 单选 or 多选 FunctionConfig.MODE_SINGLE FunctionConfig.MODE_MULTIPLE
//                .setVideoS(0)// 查询多少秒内的视频 单位:秒
//                .setShowCamera(true) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
//                .setEnablePreview(true) // 是否打开预览选项
//                .setEnableCrop(true) // 是否打开剪切选项
//                .setCircularCut(true)// 是否采用圆形裁剪
//                .setPreviewVideo(false) // 是否预览视频(播放) mode or 多选有效
////                .setCheckedBoxDrawable() // 选择图片样式
////                .setRecordVideoDefinition() // 视频清晰度
////                .setRecordVideoSecond() // 视频秒数
////                .setCustomQQ_theme()// 可自定义QQ数字风格，不传就默认是蓝色风格
////                .setGif()// 是否显示gif图片，默认不显示
////                .setCropW() // cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
////                .setCropH() // cropH-->裁剪高度 值不能小于100 如果值大于图片原始宽高 将返回原图大小
////                .setMaxB() // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb左右
//                .setPreviewColor(R.color.colorWhite) //预览字体颜色
//                .setCompleteColor() //已完成字体颜色
//                .setPreviewTopBgColor()//预览图片标题背景色
//                .setPreviewBottomBgColor() //预览底部背景色
//                .setBottomBgColor() //图片列表底部背景色
//                .setGrade() // 压缩档次 默认三档
//                .setCheckNumMode() //QQ选择风格
//                .setCompressQuality() // 图片裁剪质量,默认无损
//                .setImageSpanCount() // 每行个数
//                .setSelectMedia() // 已选图片，传入在次进去可选中，不能传入网络图片
//                .setCompressFlag() // 1 系统自带压缩 2 luban压缩
//                .setCompressW() // 压缩宽 如果值大于图片原始宽高无效
//                .setCompressH() // 压缩高 如果值大于图片原始宽高无效
//                .setThemeStyle() // 设置主题样式
//                .setPicture_title_color() // 设置标题字体颜色
//                .setPicture_right_color() // 设置标题右边字体颜色
//                .setLeftBackDrawable() // 设置返回键图标
//                .setStatusBar() // 设置状态栏颜色，默认是和标题栏一致
//                .setImmersive(false)// 是否改变状态栏字体颜色(黑色)
//                .setNumComplete(false) // 0/9 完成  样式
//                .setClickVideo()// 点击声音
//                .create();
//    }

}
