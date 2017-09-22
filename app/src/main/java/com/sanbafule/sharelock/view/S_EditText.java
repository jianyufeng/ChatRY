package com.sanbafule.sharelock.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/10/8.
 */
public class S_EditText extends EditText {
    
    public S_EditText(Context context) {
        this(context, null);
    }

    public S_EditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public S_EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);


    }


}
