package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.base.BaseActivity;
/**
 * Demo描述:
 * 利用Bitmap.createBitmap()和Matrix实现图像
 * 的旋转,缩放,位移,倾斜.
 * 该方式的作用和mImageView.setImageMatrix(matrix);
 * 是一样的
 *
 * 备注说明:
 * 利用Bitmap.createBitmap()和Matrix的方式没有实现图像
 * 关于X轴,Y轴和XY的对称.
 * 明白怎么实现的coder请指点,多谢.
 *
 */
public class BitmapActivity extends BaseActivity {
    private Bitmap mBitmap;
    private Bitmap mNewBitmap;
    private ImageView mImageView;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, BitmapActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_bitmap;
    }

    @Override
    public void initViews() {
        init();
    }

    @Override
    public void initDatas(Bundle savedInstanceState) {

    }

    private void init(){
        mImageView=(ImageView) findViewById(R.id.imageView);
        mImageView.setScaleType(ImageView.ScaleType.MATRIX);

        BitmapDrawable bitmapDrawable=(BitmapDrawable) mImageView.getDrawable();
        mBitmap=bitmapDrawable.getBitmap();

        //平移的两种方式,效果一致
        testTranslate1(mBitmap);
//        testTranslate2(mBitmap);

        //围绕图片中心点旋转且位移的两种方式,效果一致
        //testRotate1(mBitmap);
        //testRotate2(mBitmap);

        //围绕原点旋转后平移的两种方式,效果一致
        //testRotateAndTranslate1(mBitmap);
        //testRotateAndTranslate2(mBitmap);

        //测试缩放的两种方式,效果一致
        //testScale1();
        //testScale2(mBitmap);

        //测试倾斜各两种方式,效果一致
        //testSkewX1();
        //testSkewX2(mBitmap);
        //testSkewY1();
        //testSkewY2(mBitmap);
        //testSkewXY1();
        //testSkewXY2(mBitmap);

        //测试对称
        //testSymmetryX(mBitmap);
        //testSymmetryY(mBitmap);
        //testSymmetryXY(mBitmap);
    }



    //平移的方式一
    private void testTranslate1(Bitmap bitmap){
        Matrix matrix=new Matrix();
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        matrix.postTranslate(width, height);
        mImageView.setImageMatrix(matrix);
    }


    //平移的方式二
    private void testTranslate2(Bitmap bitmap){
        Matrix matrix=mImageView.getImageMatrix();
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        matrix.postTranslate(width, height);
        mNewBitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        mImageView.setImageBitmap(mNewBitmap);
    }


    //围绕图片中心点旋转且位移的方式一
    private void testRotate1(Bitmap bitmap){
        Matrix matrix=new Matrix();
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        matrix.postRotate(45f, width/2, height/2);
        matrix.postTranslate(width, height);
        mImageView.setImageMatrix(matrix);
    }

    //围绕图片中心点旋转且位移的方式二
    //注意问题:
    //在方式一种旋转45°采用matrix.postRotate(45f, width/2, height/2);即可
    //但在方式二中只需旋转22.5度matrix.postRotate(45/2f, width/2, height/2);
    private void testRotate2(Bitmap bitmap){
        Matrix matrix=mImageView.getImageMatrix();
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        matrix.postRotate(45/2f, width/2, height/2);
        matrix.postTranslate(width, height);
        mNewBitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        mImageView.setImageBitmap(mNewBitmap);
    }


    //围绕原点旋转后平移的方式一
    private void testRotateAndTranslate1(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int width =bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setRotate(45f);
        matrix.postTranslate(width, height);
        mImageView.setImageMatrix(matrix);
    }



    //围绕原点旋转后平移的方式二
    //注意问题:
    //同上
    private void testRotateAndTranslate2(Bitmap bitmap) {
        Matrix matrix = mImageView.getImageMatrix();
        int width =bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setRotate(45/2f);
        matrix.postTranslate(width, height);
        mNewBitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        mImageView.setImageBitmap(mNewBitmap);
    }




    //缩放的方式一
    private void testScale1() {
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        mImageView.setImageMatrix(matrix);
    }

    //缩放的方式二
    private void testScale2(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int width =bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setScale(0.5f, 0.5f);
        mNewBitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        mImageView.setImageBitmap(mNewBitmap);
    }

    //水平倾斜的方式一
    private void testSkewX1() {
        Matrix matrix = new Matrix();
        matrix.setSkew(0.5f, 0);
        mImageView.setImageMatrix(matrix);
    }

    //水平倾斜的方式二
    private void testSkewX2(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int width =bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setSkew(0.5f, 0);
        mNewBitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        mImageView.setImageBitmap(mNewBitmap);
    }

    // 垂直倾斜的方式一
    private void testSkewY1() {
        Matrix matrix = new Matrix();
        matrix.setSkew(0, 0.5f);
        mImageView.setImageMatrix(matrix);
    }

    // 垂直倾斜的方式二
    private void testSkewY2(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int width =bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setSkew(0, 0.5f);
        mNewBitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        mImageView.setImageBitmap(mNewBitmap);
    }

    // 水平且垂直倾斜的方式一
    private void testSkewXY1() {
        Matrix matrix = new Matrix();
        matrix.setSkew(0.5f, 0.5f);
        mImageView.setImageMatrix(matrix);
    }

    // 水平且垂直倾斜的方式二
    private void testSkewXY2(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int width =bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setSkew(0.5f, 0.5f);
        mNewBitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        mImageView.setImageBitmap(mNewBitmap);
    }



    // 水平对称--图片关于X轴对称
    private void testSymmetryX(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int height =bitmap.getHeight();
        float matrixValues[] = { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
        matrix.setValues(matrixValues);
        //若是matrix.postTranslate(0, height);//表示将图片上下倒置
        matrix.postTranslate(0, height*2);
        mImageView.setImageMatrix(matrix);
    }




    // 垂直对称--图片关于Y轴对
    private void testSymmetryY(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int width=bitmap.getWidth();
        float matrixValues[] = {-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
        matrix.setValues(matrixValues);
        //若是matrix.postTranslate(width,0);//表示将图片左右倒置
        matrix.postTranslate(width*2, 0);
        mImageView.setImageMatrix(matrix);
    }


    // 关于X=Y对称
    private void testSymmetryXY(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float matrixValues[] = { 0f, -1f, 0f, -1f, 0f, 0f, 0f, 0f, 1f };
        matrix.setValues(matrixValues);
        matrix.postTranslate(width+height, width+height);
        mImageView.setImageMatrix(matrix);
    }


}
