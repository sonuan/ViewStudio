package com.wusongyuan.customviewstudio;

import android.view.View;
import android.view.ViewParent;

/**********************
 * @author: wusongyuan
 * @date: 2016-05-09
 * @desc:
 **********************/
public interface OnItemClickListener {
    void onItemClick(ViewParent parent, View view, int position);
}
