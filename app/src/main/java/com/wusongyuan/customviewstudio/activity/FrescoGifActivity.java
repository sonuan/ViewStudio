package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
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
        gif(R.drawable.gif_big);

    }

    private void gif(int resid) {
//        Uri uri = Uri.parse(url);
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resid))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                // other setters
                .build();
        mDraweeView.setController(controller);
    }

    @OnClick(R.id.btn_gif_small)
    public void small() {
        Log.i(TAG, "small: ");
        Uri uri = Uri.parse("asset:///gif_small.gif");
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                // other setters
                .build();
        mDraweeView.setController(controller);
    }

}
