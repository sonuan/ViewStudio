package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class FrescoGifActivity extends BaseActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, FrescoGifActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.simpledraweeview)
    SimpleDraweeView mDraweeView;

    @Override
    public int getContentView() {
        return R.layout.activity_fresco_gif;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initDatas(Bundle savedInstanceState) {

    }

    @OnClick(R.id.btn_gif_big)
    public void big() {
        Log.i(TAG, "big: ");
        //        gif("http://ww1.sinaimg.cn/large/7a9c7569jw1e7yloj6aneg2074074wpz.gif");
        gif(R.drawable.gif_falali);

    }

    private void gif(int resid) {
        Uri uri = Uri.parse("res:///" + resid);
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
//                mDraweeView.setLayoutParams(new FrameLayout.LayoutParams(imageInfo.getWidth(), imageInfo.getHeight()));
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .setControllerListener(controllerListener)
                // other setters
                .build();
        mDraweeView.setController(controller);
    }

    @OnClick(R.id.btn_gif_small)
    public void small() {
        Log.i(TAG, "small: ");
        Uri uri = Uri.parse("asset:///gif_falali.gif");
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                // other setters
                .build();
        mDraweeView.setController(controller);
    }

}
