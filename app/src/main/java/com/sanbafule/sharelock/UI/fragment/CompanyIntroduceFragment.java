package com.sanbafule.sharelock.UI.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.sanbafule.sharelock.R;

import butterknife.Bind;


public class CompanyIntroduceFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.introduceLayout)
    LinearLayout introduceLayout;
    @Bind(R.id.cultureLayout)
    LinearLayout cultureLayout;
    @Bind(R.id.bigEventsLayout)
    LinearLayout bigEventsLayout;
    @Bind(R.id.cooperativAgencyLayout)
    LinearLayout cooperativAgencyLayout;
    @Bind(R.id.cooperativeHonorLayout)
    LinearLayout cooperativeHonorLayout;
    private Intent intent;
    private String url = "";
    private String title = "";
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_company_introduce, container, false);
//        introduceLayout= (LinearLayout) view.findViewById(R.id.introduceLayout);//公司介绍
//        introduceLayout.setOnClickListener(this);
//        cultureLayout= (LinearLayout) view.findViewById(R.id.cultureLayout);// 公司文化
//        cultureLayout.setOnClickListener(this);
//        bigEventsLayout= (LinearLayout) view.findViewById(R.id.bigEventsLayout);// 公司大事记
//        bigEventsLayout.setOnClickListener(this);
//        cooperativAgencyLayout= (LinearLayout) view.findViewById(R.id.cooperativAgencyLayout);// 合作机构
//        cooperativAgencyLayout.setOnClickListener(this);
//        cooperativeHonorLayout= (LinearLayout) view.findViewById(R.id.cooperativeHonorLayout);// 合作荣誉
//        cooperativeHonorLayout.setOnClickListener(this);
//
//        return view;
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_company_introduce;
    }


    /**
     * 主要处理跳转页面
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.introduceLayout:
//                 url = String.format(Url.COMPANY_INTRODUCE_URL, MyString.TOKEN);
//                 title="公司介绍";
//                startWebPage(url,title);
                break;
            case R.id.cultureLayout:
//                 url = String.format(Url.COMPANY_CULTURE_URL, MyString.TOKEN);
//                 title="公司文化";
//                startWebPage(url,title);
                break;
            case R.id.bigEventsLayout:
//                 url = String.format(Url.COMPANY_BIG_EVENTS_URL, MyString.TOKEN);
//                 title="公司大事记";
//                startWebPage(url,title);
                break;
            case R.id.cooperativAgencyLayout:
//                 url = String.format(Url.COOPERRATIV_AGENCY_URL, MyString.TOKEN);
//                 title="合作机构";
//                startWebPage(url,title);
                break;
            case R.id.cooperativeHonorLayout:
//                 url = String.format(Url.COOPERRATIV_HONOR_URL, MyString.TOKEN);
//                 title="合作荣誉";
//                startWebPage(url,title);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void startWebPage(String url, String title) {
//        intent = new Intent(getActivity(), WebAboutActivity.class);
//        intent.putExtra(MyString.linkUrl, url);
//        intent.putExtra(MyString.WEB_TITLE, title);
//        startActivity(intent);
    }



}
