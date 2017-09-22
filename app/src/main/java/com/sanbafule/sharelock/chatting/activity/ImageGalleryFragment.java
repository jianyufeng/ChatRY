package com.sanbafule.sharelock.chatting.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.fragment.BaseFragment;
import com.sanbafule.sharelock.chatting.modle.ViewImageInfo;
import com.sanbafule.sharelock.view.photoview.PhotoView;
import com.sanbafule.sharelock.view.photoview.PhotoViewAttacher;

import java.io.File;

public class ImageGalleryFragment extends BaseFragment {

    private static final String TAG = "ImageGalleryFragment";



    @Override
    protected int getLayoutId() {
        return R.layout.image_grallery_fragment;
    }

    private String mImageUrl;
    private PhotoView mImageView;
    private LinearLayout progressBar;
    private TextView mProgress;
    private ViewImageInfo mEntry;
    private Bitmap mThumbnailBitmap;

    private WebView webView;
    private File imgCacheFile;
    private String mCacheImageUrl;
    private FrameLayout mViewContainer;
    private FrameLayout mSuccessLayout;
    private LinearLayout mFailLayout;
    private boolean isGif = false;

    public static ImageGalleryFragment newInstance(String imageUrl) {
        final ImageGalleryFragment f = new ImageGalleryFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    public static ImageGalleryFragment newInstance(ViewImageInfo entry) {
        final ImageGalleryFragment f = new ImageGalleryFragment();
        final Bundle args = new Bundle();
        args.putParcelable("entry", entry);
        f.setArguments(args);

        return f;
    }
    public static int i=0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEntry  = getArguments() != null ? getArguments().<ViewImageInfo>getParcelable("entry") : null;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewContainer = (FrameLayout) findViewById(R.id.image_container);
        mSuccessLayout = (FrameLayout) findViewById(R.id.image_gallery_download_success);
        mFailLayout = (LinearLayout) findViewById(R.id.image_gallery_download_fail);
        mImageView = (PhotoView) findViewById(R.id.image);
        i++;

        if (mEntry == null) {
            finish();
            return ;
        }

        // 长按之后保存到手机
            mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

//                    new MaterialDialog.Builder(getActivity())
//                            .title(R.string.dialog_hint_title)
//                            .items(R.array.dialog_list_item)
//                            .itemsCallback(new MaterialDialog.ListCallback() {
//                                @Override
//                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                    handleDialogItemClick(which);
//
//                                }
//                            })
//                            .show();
                    return false;
                }
            });



        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });
        initWebView();
        progressBar = (LinearLayout) findViewById(R.id.loading);
        mProgress = (TextView) findViewById(R.id.uploading_tv);


        // 显示图片


        Glide.
                with(this).
                load(mEntry.getPicurl()).
                error(R.drawable.icon_delete).
                fitCenter().
                crossFade(300).
                into(mImageView);


        }


    private void initWebView() {
        webView = (WebView) findViewById(R.id.web_gif);
        webView.setBackgroundColor(0);
    }

    private void handleDialogItemClick(int position) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mImageView != null) {
            mImageView.setImageDrawable(null);
        }
        mImageView = null;
        if(mViewContainer != null) {
            mViewContainer.removeView(webView);
        }
        if(webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    /**
     * 使用WebView加载GIF图片和大图
     *
     * @param s
     */
    private void showImgInWebView(final String s) {
        if (webView != null) {
            webView.loadDataWithBaseURL("", "<!doctype html> <html lang=\"en\"> <head> <meta charset=\"UTF-8\"> <title></title><style type=\"text/css\"> html,body{width:100%;height:100%;margin:0;padding:0;background-color:black;} *{ -webkit-tap-highlight-color: rgba(0, 0, 0, 0);}#box{ width:100%;height:100%; display:table; text-align:center; background-color:black;} body{-webkit-user-select: none;user-select: none;-khtml-user-select: none;}#box span{ display:table-cell; vertical-align:middle;} #box img{  width:100%;} </style> </head> <body> <div id=\"box\"><span><img src=\"img_url\" alt=\"\"></span></div> <script type=\"text/javascript\" >document.body.onclick=function(e){window.external.onClick();e.preventDefault(); };function load_img(){var url=document.getElementsByTagName(\"img\")[0];url=url.getAttribute(\"src\");var img=new Image();img.src=url;if(img.complete){\twindow.external.img_has_loaded();\treturn;};img.onload=function(){window.external.img_has_loaded();};img.onerror=function(){\twindow.external.img_loaded_error();};};load_img();</script></body> </html>".replace("img_url", s), "text/html", "utf-8", "");
        }
    }
}
