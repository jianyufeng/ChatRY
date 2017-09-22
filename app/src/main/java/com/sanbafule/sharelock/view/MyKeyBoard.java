package com.sanbafule.sharelock.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.view.AudioRecorderButton;
import com.sanbafule.sharelock.global.Code;

import java.util.ArrayList;
import java.util.Iterator;

import pub.devrel.easypermissions.EasyPermissions;
import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.PageSetEntity;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.EmoticonsFuncView;
import sj.keyboard.widget.EmoticonsIndicatorView;
import sj.keyboard.widget.EmoticonsToolBarView;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by Administrator on 2016/10/25.
 */
public class MyKeyBoard extends AutoHeightLayout implements View.OnClickListener, EmoticonsFuncView.OnEmoticonsPageViewListener, EmoticonsToolBarView.OnToolBarItemClickListener
        , EmoticonsEditText.OnBackKeyClickListener, FuncLayout.OnFuncChangeListener {
    public static final int FUNC_TYPE_EMOTION = -1;
    public static final int FUNC_TYPE_APPPS = -2;
    protected LayoutInflater mInflater;
    protected ImageView mBtnVoiceOrText;
    protected AudioRecorderButton mBtnVoice;
    protected EmoticonsEditText mEtChat;
    protected ImageView mBtnFace;
    protected RelativeLayout mRlInput;
    protected ImageView mBtnMultimedia;
    protected Button mBtnSend;
    protected FuncLayout mLyKvml;
    protected EmoticonsFuncView mEmoticonsFuncView;
    protected EmoticonsIndicatorView mEmoticonsIndicatorView;
    protected EmoticonsToolBarView mEmoticonsToolBarView;
    protected boolean mDispatchKeyEventPreImeLock = false;

    public MyKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.inflateKeyboardBar();
        this.initView();
        this.initFuncView();
    }

    public void inflateKeyboardBar() {
        this.mInflater.inflate(R.layout.mykeyboard, this);
    }

    public View inflateFunc() {
        return this.mInflater.inflate(R.layout.view_func_emoticon, (ViewGroup) null);
    }

    public void initView() {
        this.mBtnVoiceOrText = (ImageView) this.findViewById(R.id.btn_voice_or_text);
        this.mBtnVoice = (AudioRecorderButton) this.findViewById(R.id.btn_voice);
        this.mEtChat = (EmoticonsEditText) this.findViewById(R.id.et_chat);
        this.mBtnFace = (ImageView) this.findViewById(R.id.btn_face);
        this.mRlInput = (RelativeLayout) this.findViewById(R.id.rl_input);
        this.mBtnMultimedia = (ImageView) this.findViewById(R.id.btn_multimedia);
        this.mBtnSend = (Button) this.findViewById(R.id.btn_send);
        this.mLyKvml = (FuncLayout) this.findViewById(R.id.ly_kvml);
        this.mBtnVoiceOrText.setOnClickListener(this);
        this.mBtnFace.setOnClickListener(this);
        this.mBtnMultimedia.setOnClickListener(this);
        this.mEtChat.setOnBackKeyClickListener(this);
    }

    public void initFuncView() {
        this.initEmoticonFuncView();
        this.initEditView();
    }

    public void initEmoticonFuncView() {
        View keyboardView = this.inflateFunc();
        this.mLyKvml.addFuncView(-1, keyboardView);
        this.mEmoticonsFuncView = (EmoticonsFuncView) this.findViewById(R.id.view_epv);
        this.mEmoticonsIndicatorView = (EmoticonsIndicatorView) this.findViewById(R.id.view_eiv);
        this.mEmoticonsToolBarView = (EmoticonsToolBarView) this.findViewById(R.id.view_etv);
        this.mEmoticonsFuncView.setOnIndicatorListener(this);
        this.mEmoticonsToolBarView.setOnToolBarItemClickListener(this);
        this.mLyKvml.setOnFuncChangeListener(this);
    }


    protected void initEditView() {
        this.mEtChat.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!MyKeyBoard.this.mEtChat.isFocused()) {
                    MyKeyBoard.this.mEtChat.setFocusable(true);
                    MyKeyBoard.this.mEtChat.setFocusableInTouchMode(true);
                }

                return false;
            }
        });
        this.mEtChat.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    MyKeyBoard.this.mBtnSend.setVisibility(VISIBLE);
                    MyKeyBoard.this.mBtnMultimedia.setVisibility(GONE);
                    MyKeyBoard.this.mBtnSend.setBackgroundResource(R.drawable.btn_send_bg);
                } else {
                    MyKeyBoard.this.mBtnMultimedia.setVisibility(VISIBLE);
                    MyKeyBoard.this.mBtnSend.setVisibility(GONE);
                }

            }
        });
    }

    public void setAdapter(PageSetAdapter pageSetAdapter) {
        if (pageSetAdapter != null) {
            ArrayList pageSetEntities = pageSetAdapter.getPageSetEntityList();
            if (pageSetEntities != null) {
                Iterator i$ = pageSetEntities.iterator();

                while (i$.hasNext()) {
                    PageSetEntity pageSetEntity = (PageSetEntity) i$.next();
                    this.mEmoticonsToolBarView.addToolItemView(pageSetEntity);
                }
            }
        }

        this.mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    public void addFuncView(View view) {
        this.mLyKvml.addFuncView(-2, view);
    }

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        this.mLyKvml.hideAllFuncView();
        this.mBtnFace.setImageResource(R.drawable.icon_face_nomal);
    }

    protected void showVoice() {
        this.mRlInput.setVisibility(GONE);
        this.mBtnVoice.setVisibility(VISIBLE);
        this.reset();
    }

    protected void checkVoice() {
        if (this.mBtnVoice.isShown()) {
            this.mBtnVoiceOrText.setImageResource(R.drawable.btn_voice_or_text_keyboard);
        } else {
            this.mBtnVoiceOrText.setImageResource(R.drawable.btn_voice_or_text);
        }

    }

    protected void showText() {
        this.mRlInput.setVisibility(VISIBLE);
        this.mBtnVoice.setVisibility(GONE);
    }

    protected void toggleFuncView(int key) {
        this.showText();
        this.mLyKvml.toggleFuncView(key, this.isSoftKeyboardPop(), this.mEtChat);
    }

    public void onFuncChange(int key) {
        if (-1 == key) {
            this.mBtnFace.setImageResource(R.drawable.icon_face_pop);
        } else {
            this.mBtnFace.setImageResource(R.drawable.icon_face_nomal);
        }

        this.checkVoice();
    }

    protected void setFuncViewHeight(int height) {
        LayoutParams params = (LayoutParams) this.mLyKvml.getLayoutParams();
        params.height = height;
        this.mLyKvml.setLayoutParams(params);
    }

    public void onSoftKeyboardHeightChanged(int height) {
        this.mLyKvml.updateHeight(height);
    }

    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
        this.mLyKvml.setVisibility(true);
        this.mLyKvml.getClass();
        this.onFuncChange(-2147483648);
    }

    public void OnSoftClose() {
        super.OnSoftClose();
        if (this.mLyKvml.isOnlyShowSoftKeyboard()) {
            this.reset();
        } else {
            this.onFuncChange(this.mLyKvml.getCurrentFuncKey());
        }

    }

    public void addOnFuncKeyBoardListener(FuncLayout.OnFuncKeyBoardListener l) {
        this.mLyKvml.addOnKeyBoardListener(l);
    }

    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
        this.mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    public void playTo(int position, PageSetEntity pageSetEntity) {
        this.mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        this.mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

    public void setOpenSoft() {
        if (!this.mRlInput.isShown()) {
            this.showText();
            this.mBtnVoiceOrText.setImageResource(R.drawable.btn_voice_or_text);
            EmoticonsKeyboardUtils.openSoftKeyboard(this.mEtChat);
        }
    }
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_voice_or_text) {
            //检查权限
            if (!EasyPermissions.hasPermissions(GroupChattingActivity.mInstance, Manifest.permission.RECORD_AUDIO)) {
                //没有 申请权限
                EasyPermissions.requestPermissions(GroupChattingActivity.mInstance, "语音需要录音权限",
                        Code.Permission_Recode_Code, Manifest.permission.RECORD_AUDIO);
                return;
            }
            if (this.mRlInput.isShown()) {
                this.mBtnVoiceOrText.setImageResource(R.drawable.btn_voice_or_text_keyboard);
                this.showVoice();
            } else {
                this.showText();
                this.mBtnVoiceOrText.setImageResource(R.drawable.btn_voice_or_text);
                EmoticonsKeyboardUtils.openSoftKeyboard(this.mEtChat);
            }
        } else if (i == R.id.btn_face) {
            this.toggleFuncView(-1);
        } else if (i == R.id.btn_multimedia) {
            this.toggleFuncView(-2);
        }

    }

    public void onToolBarItemClick(PageSetEntity pageSetEntity) {
        this.mEmoticonsFuncView.setCurrentPageSet(pageSetEntity);
    }

    public void onBackKeyClick() {
        if (this.mLyKvml.isShown()) {
            this.mDispatchKeyEventPreImeLock = true;
            this.reset();
        }

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 4:
                if (this.mDispatchKeyEventPreImeLock) {
                    this.mDispatchKeyEventPreImeLock = false;
                    return true;
                } else {
                    if (this.mLyKvml.isShown()) {
                        this.reset();
                        return true;
                    }

                    return super.dispatchKeyEvent(event);
                }
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return EmoticonsKeyboardUtils.isFullScreen((Activity) this.getContext()) ? false : super.requestFocus(direction, previouslyFocusedRect);
    }

    public void requestChildFocus(View child, View focused) {
        if (!EmoticonsKeyboardUtils.isFullScreen((Activity) this.getContext())) {
            super.requestChildFocus(child, focused);
        }
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        } else {
            switch (event.getKeyCode()) {
                case 4:
                    if (EmoticonsKeyboardUtils.isFullScreen((Activity) this.getContext()) && this.mLyKvml.isShown()) {
                        this.reset();
                        return true;
                    }
                default:
                    if (event.getAction() == 0) {
                        boolean isFocused;
                        if (Build.VERSION.SDK_INT >= 21) {
                            isFocused = this.mEtChat.getShowSoftInputOnFocus();
                        } else {
                            isFocused = this.mEtChat.isFocused();
                        }

                        if (isFocused) {
                            this.mEtChat.onKeyDown(event.getKeyCode(), event);
                        }
                    }

                    return false;
            }
        }
    }

    public EmoticonsEditText getEtChat() {
        return this.mEtChat;
    }

    public AudioRecorderButton getBtnVoice() {
        return this.mBtnVoice;
    }

    public Button getBtnSend() {
        return this.mBtnSend;
    }

    public EmoticonsFuncView getEmoticonsFuncView() {
        return this.mEmoticonsFuncView;
    }

    public EmoticonsIndicatorView getEmoticonsIndicatorView() {
        return this.mEmoticonsIndicatorView;
    }

    public EmoticonsToolBarView getEmoticonsToolBarView() {
        return this.mEmoticonsToolBarView;
    }
}
