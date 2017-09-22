package com.sanbafule.sharelock.chatting.activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sanbafule.sharelock.R;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


/****
 * 播放小视屏的页面
 */
public class PlayVideoFragment extends Fragment implements View.OnClickListener {

    private static final String VIDEO_PATH = "video_path";

    private String videoPath;

    private VideoView mVideoView;
    private ImageView imageView;

    public static PlayVideoFragment newInstance(String videoPath) {
        PlayVideoFragment fragment = new PlayVideoFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_PATH, videoPath);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoPath = getArguments().getString(VIDEO_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_video, container, false);
        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        imageView = (ImageView) view.findViewById(R.id.imgbt_vedio_show);
//        btnClose = (Button) view.findViewById(R.id.btn_close);
        // 播放相应的视频
        mVideoView.setMediaController(new MediaController(getActivity()));
        if (videoPath != null) {
            mVideoView.setVideoURI(Uri.parse(videoPath));
            mVideoView.start();
        }
//        mVideoView.setOnCompletionListener(this);
        imageView.setOnClickListener(this);
        mVideoView.requestFocus();

        return view;
    }



    @Override
    public void onClick(View v) {
//        mVideoView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        if (videoPath != null) {
            mVideoView.setVideoURI(Uri.parse(videoPath));
            mVideoView.start();
        }

    }




}
