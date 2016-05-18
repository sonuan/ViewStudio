package com.wusongyuan.customviewstudio.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.wusongyuan.customviewstudio.OnItemClickListener;
import com.wusongyuan.customviewstudio.R;


public class HomeActivity extends AppCompatActivity implements OnItemClickListener {

    RecyclerView mRecyclerView;
    MyRecyclerViewAdapter mAdapter;

    String[] title = {"通讯录列表字母索引View", "仿支付宝sdk支付结果View", "ListView下拉刷新,上拉加载更多"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MyRecyclerViewAdapter();
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(title);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Snackbar.make(view, "你好!个人学习用的,慎重使用!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onItemClick(ViewParent parent, View view, int position) {
        switch (position) {
            case 0:
                LetterListActivity.toActivity(this);
                break;
            case 1:
                CircleLoadingActivity.toActivity(this);
                break;
            case 2:
                RefreshListViewActivity.toActivity(this);
                break;
        }
    }

    public static class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{

        private String[] mList;

        private OnItemClickListener mItemClickListener;

        public OnItemClickListener getItemClickListener() {
            return mItemClickListener;
        }

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        public MyRecyclerViewAdapter() {

        }

        public class  ViewHolder extends RecyclerView.ViewHolder{
            TextView tvTitle;
            public ViewHolder(View view){
                super(view);
                tvTitle = (TextView) view.findViewById(android.R.id.text1);
            }
        }

        public void setData(String[] list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String title = mList[position];
            holder.tvTitle.setText(title);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(holder.itemView.getParent(), holder.itemView, position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.length;
        }
    }

}