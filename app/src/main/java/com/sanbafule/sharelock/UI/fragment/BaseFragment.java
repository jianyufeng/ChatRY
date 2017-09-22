package com.sanbafule.sharelock.UI.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanbafule.sharelock.util.DialogUtils;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/19.
 */


public  abstract class BaseFragment extends Fragment {

    private static final String TAG = "ECSDK_Demo.CCPFragment";

    /**当前页面是否可以销毁*/
    private boolean isFinish = false;


    public boolean isFinish() {
        return isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    /**
     * 重载页面关闭方法
     */
    public void finish() {
        if(getActivity() == null) {
            return;
        }
        if(isFinish) {
            getActivity().finish();
            return;
        }

        getActivity().getSupportFragmentManager().popBackStack();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ButterKnife.bind(this, inflater.inflate(getLayoutId(),container,false));
        return inflater.inflate(getLayoutId(),container,false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected abstract int getLayoutId();

    /**
     * 查找	View
     * @param paramInt
     * @return
     */
    public final View findViewById(int paramInt) {
        return getView().findViewById(paramInt);
    }

    protected MaterialDialog mPostingdialog;
    protected  void  showDigLog(){
        DialogUtils.showIndeterminateProgressDialog(getActivity(),false);
    }
    protected  void  closeDialog(){
        if (mPostingdialog == null || !mPostingdialog.isShowing()) {
            return;
        }
        mPostingdialog.dismiss();
        mPostingdialog = null;
    }


}
