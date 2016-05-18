package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.widget.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class RefreshListViewActivity extends AppCompatActivity implements RefreshListView.OnRefreshListener,
        RefreshListView.OnLoadMoreListener {

    private RefreshListView mRefreshListView;
    private List<String> mDatas;
    private ArrayAdapter<String> mAdapter;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, RefreshListViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_list_view);
        mRefreshListView = (RefreshListView) findViewById(R.id.refreshlistview);
        mRefreshListView.setOnRefreshListener(this);
        mRefreshListView.setOnLoadMoreListener(this);
        mDatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDatas.add("下拉刷新" + i);
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R
                .id.text1, mDatas);
        mRefreshListView.setAdapter(mAdapter);
        setLoadMore();
    }

    int refreshCount = 0;

    @Override
    public void onRefresh() {
        count = 0;
        mDatas.clear();
        mAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshCount % 2 == 0) {
                    mRefreshListView.onRefreshSuccess();
                } else {
                    mRefreshListView.onRefreshFailure();
                }
                refreshCount++;
                for (int i = 0; i < 20; i++) {
                    mDatas.add("上拉刷新" + i);
                }
                mAdapter.notifyDataSetChanged();
                setLoadMore();
            }
        }, 2000);
    }

    int count = 0;

    @Override
    public void onLoadMore() {
        count = count + 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (count > 3) {
                    mRefreshListView.onLoadMoreFailure();
                } else {
                    mRefreshListView.onLoadMoreSuccess();
                    for (int i = 0; i < 20; i++) {
                        mDatas.add("加载更多" + mDatas.size());
                    }
                    mAdapter.notifyDataSetChanged();
                    setLoadMore();
                }
            }
        }, 2000);
    }

    private void setLoadMore() {
        if (mDatas.size() < 100) {
            mRefreshListView.setLoadMoreable(true);
        } else {
            mRefreshListView.setLoadMoreable(false);
        }
    }
}
