package com.wusongyuan.customviewstudio.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.base.BaseActivity;
import com.wusongyuan.customviewstudio.gift.FlyGiftRenderer;
import com.wusongyuan.customviewstudio.gift.base.LoadingGifDrawable;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class ShipGiftActivity extends BaseActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, ShipGiftActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.iv_gift)
    ImageView mIVGift;
    @BindView(R.id.iv_gift2)
    SimpleDraweeView mSimpleDraweeView;
    private LoadingGifDrawable mFlyGiftDrawable;


    @Override
    public int getContentView() {
        return R.layout.activity_ship_gift;
    }

    @Override
    public void initViews() {
    }


    @Override
    public void initDatas(Bundle savedInstanceState) {
        FlyGiftRenderer flyGiftRenderer = new FlyGiftRenderer(this);
        flyGiftRenderer.setDuration(2000);
        flyGiftRenderer.addRenderListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        try {
            mFlyGiftDrawable = new LoadingGifDrawable(getResources(), R.drawable.gif_car_run, flyGiftRenderer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        mIVGift.setImageDrawable(mFlyGiftDrawable);
        //        mFlyGiftDrawable.start();

        mSimpleDraweeView.setImageDrawable(mFlyGiftDrawable);
        mFlyGiftDrawable.start();
    }

    @OnClick(R.id.iv_gift)
    public void gift1() {
        mFlyGiftDrawable.start();
    }

    @OnClick(R.id.iv_gift2)
    public void gift2() {
        mFlyGiftDrawable.start();
    }

    @Override
    protected void onStop() {
        mFlyGiftDrawable.stop();
        super.onStop();
    }
}
