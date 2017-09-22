package com.sanbafule.sharelock.UI.contact;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.business.DownImgBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.view.photoview.PhotoView;
import com.sanbafule.sharelock.view.photoview.PhotoViewAttacher;


/**
 *
 */
public class ImageShowerActivity extends AppCompatActivity {

    private PhotoView bigIg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        bigIg = (PhotoView) findViewById(R.id.image);
        bigIg.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        if(intent.getStringExtra(SString.BIG_IMG) != null&&intent.getStringExtra("ContactInfo_ID") != null){
            DownImgBiz biz=new DownImgBiz();
            biz.downLoadHeardImg(this,bigIg,intent.getStringExtra(SString.BIG_IMG),intent.getStringExtra("ContactInfo_ID"),R.drawable.ic_launcher);
        }


    }



}
