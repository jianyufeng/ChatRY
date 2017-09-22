package com.sanbafule.sharelock.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sanbafule.sharelock.R;

import me.iwf.photopicker.widget.MultiPickResultView;

/**
 * 图片选择画面
 */
public class PhotoPickerActivity extends AppCompatActivity {

    private MultiPickResultView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView.onActivityResult(requestCode,resultCode,data);

    }
}
