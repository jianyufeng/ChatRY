package com.sanbafule.sharelock.UI.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sanbafule.sharelock.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/24.
 */
public class ProductShowFragment extends BaseFragment {


    private static final String TAG = "Product_ShowActivity";
    private ListView mListView;
    private ArrayList<String> mListName = new ArrayList<>();
//    private ProductListFragment mProductListFragment;
//    private ArrayList<Goods> ca;
//    private static ArrayList<ArrayList<Goods>> mlist = new ArrayList<>();
//    private AbsAdapter<String> adapter;
    private TextView mEmptyView;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private static SwipeRefreshLayout loadLayout;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        initView();
//        setData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    //    private void setData() {
//        adapter = new AbsAdapter<String>(getActivity(), mListName, R.layout.item_product_list) {
//            @Override
//            public void showItemContenData(ViewHolder viewHolder, String name, ViewGroup parent, int position) {
//                View view = viewHolder.getLayoutView();
//                TextView tv_line = (TextView) viewHolder.getView(R.id.tv_tag);
//                TextView tv_name = (TextView) viewHolder.getView(R.id.product_class_name);
//                viewHolder.setTextViewContent(R.id.product_class_name, mListName.get(position));
//                if (position == this.selectItem) {
//                    tv_line.setVisibility(View.VISIBLE);
//                    tv_name.setTextColor(getResources().getColor(R.color.main_color));
//                    view.setBackgroundColor(getResources().getColor(R.color.white));
//                } else {
//                    tv_line.setVisibility(View.INVISIBLE);
//                    tv_name.setTextColor(getResources().getColor(R.color.black));
//                    view.setBackgroundColor(getResources().getColor(R.color.android_bg_color));
//                }
//            }
//        };
////        mListView.setAdapter(adapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                adapter.changeSelected(position);//刷新
////                mProductListFragment.updateData(position);
//            }
//        });
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                if(view.getAdapter()!=null){
//                    if (firstVisibleItem == 0){
//                        loadLayout.setEnabled(true);
//                    }else {
//                        loadLayout.setEnabled(false);
//                    }
//                }
//
//            }
//        });
//    }

//    private void initData() {
//
//        GetGoodsListBiz biz = new GetGoodsListBiz();
//        biz.getAllGoods(new GetGoodsListBiz.UpdateDataStateListtenner() {
//            @Override
//            public void updateProductSucceed(String s) {
//                loadLayout.setRefreshing(false);
//                dismissCommonPostingDialog();
//                mHasLoadedOnce = true;
//                try {
//                    JSONObject object = new JSONObject(s);
//                    JSONObject obj1 = object.getJSONObject("brand_list");
//                    int length = obj1.length();
//                    JSONArray array = new JSONArray();
//                    mListName.clear();
//                    for (int i = 0; i < length; i++) {
//                        JSONObject jsonObject = obj1.getJSONObject((1 + i) + "");
//                        String brand_name = jsonObject.getString("brand_name");
//                        mListName.add(brand_name);
//                        array.put(i, jsonObject);
//                    }
//                    mlist.clear();
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject body = array.getJSONObject(i);
//                        JSONArray jsonArray = body.getJSONArray("goods_list");
//                        if (jsonArray.length() > 0) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<ArrayList<Goods>>() {
//                            }.getType();
//                            ca = gson.fromJson(jsonArray.toString(), type);
//                        }
//                        mlist.add(i, ca);
//                    }
//
//                    if (mProductListFragment != null) {
//                        mProductListFragment.setAdapter(mlist.get(0));
//                    }
//                    //刷新后左列表设置默认选择第一项
//                    adapter.refreshData(mListName);
//                    adapter.setSelectItem(0);
//                    setEmpty();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    loadLayout.setRefreshing(false);
//                }
//
//            }
//
//            @Override
//            public void updateDataError() {
//                loadLayout.setRefreshing(false);
//                dismissCommonPostingDialog();
//                ToastUtils.showInternetToast();
//                setEmpty();
//            }
//
//            @Override
//            public void updateDataFail(String msg) {
//                loadLayout.setRefreshing(false);
//                dismissCommonPostingDialog();
////                ToastUtil.showMessage(msg);
//                setEmpty();
//            }
//        });
//    }

    public void setEmpty() {
//        if (adapter.getCount() == 0) {
////            if (mEmptyView.getVisibility() == View.GONE) {
//            mEmptyView.setVisibility(View.VISIBLE);
//            mEmptyView.setText(getString(R.string.pull_to_refresh_tap_label));
////            }
//        } else {
////            if (mEmptyView.getVisibility() == View.VISIBLE) {
//            mEmptyView.setVisibility(View.GONE);
////            }
//        }

    }
    private void initView() {
//        mListView = (ListView) findViewById(R.id.product_list);
//        mEmptyView= (TextView) findViewById(R.id.empty_conversation_tv);
//        loadLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_and_load_layout_id);
//        loadLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
//        loadLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                initData();
//            }
//        });
//        mProductListFragment = new ProductListFragment();
//        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
//        fm.beginTransaction().add(R.id.product_list_contant, mProductListFragment).commitAllowingStateLoss();

    }






    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_show;
    }


//    public static class ProductListFragment extends BaseFragment {
//        private ListView mListView;
////        private AbsAdapter<Goods> adapter;
////        private Intent intent;
////        private Goods goods;
//
//        @Override
//        protected int getLayoutId() {
//            return R.layout.fragment_productlist;
//        }
//
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
//            final View view = inflater.inflate(R.layout.fragment_productlist, container, false);
//            mListView = (ListView) view.findViewById(R.id.product_contant_list);
//            mListView.setOnItemClickListener(new MyOnItemClickListenenr());
//            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView absListView, int i) {
//
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                    if(view.getAdapter()!=null){
//                        if (firstVisibleItem == 0){
//                            loadLayout.setEnabled(true);
//                        }else {
//                            loadLayout.setEnabled(false);
//                        }
//                    }
//
//                }
//            });
//            return view;
//        }

//        class MyOnItemClickListenenr implements AdapterView.OnItemClickListener {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                goods = (Goods) parent.getAdapter().getItem(position);
////                startWebPage(goods);
//            }
//        }
//
////        private void startWebPage(Goods item) {
////
////            startActivity(new Intent(getActivity(), WebAboutActivity.class).
////                    putExtra(MyString.linkUrl, String.format(Url.PRODUCT_SEARCH_INFO, item.getGoods_id()))
////                    .putExtra(MyString.WEB_TITLE, item.getGoods_name())
////                    .putExtra(MyString.WEB_DESC, item.getGoods_name())
////                    .putExtra(MyString.WEB_TEXT, item.getGoods_name())
////                    .putExtra(MyString.WEB_IMG, item.getGoods_thumb()));
////
////
////        }
//
////        public void setAdapter(final List<Goods> list) {
////            adapter = new AbsAdapter<Goods>(getActivity(), list, R.layout.item_product_contant) {
////                @Override
////                public void showItemContenData(ViewHolder viewHolder, Goods data, ViewGroup parent, int position) {
////                    viewHolder.setImageViewContent(R.id.product_img, list.get(position).getGoods_thumb());
////                    viewHolder.setTextViewContent(R.id.product_name, list.get(position).getGoods_name());
////                }
////            };
////            mListView.setAdapter(adapter);
//        }
//
//        /**
//         * 改变adapter的数据
//         *
//         * @param arg0
//         */
////        public void updateData(int arg0) {
////            adapter = null;
////            setAdapter(mlist.get(arg0));
////        }
//
//    }
}