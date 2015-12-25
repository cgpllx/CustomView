package cc.easyandroid.customview.pulltorefresh.expandablelistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import cc.easyandroid.customview.pulltorefresh.listview.KListViewHeader;
import cc.easyandroid.customview.pulltorefresh.listview.KListViewFooter;


/**
 * KListview根据xlistview扩展而来
 *
 * @author cgpllx1@qq.com (www.easyandroid.com)
 * @date 2014-7-29
 */
public class KExpandableListView extends ExpandableListView implements AbsListView.OnScrollListener {

    private float mLastY = -1; // save event y

    private Scroller mScroller; // used for scroll back

    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IKExpandableListViewListener mListViewListener;

    // -- header view
    private KExpandableListViewHeader mHeaderView;

    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;

    private TextView mHeaderTimeView;

    private int mHeaderViewHeight; // header view's height

    private boolean mEnablePullRefresh = false;// ++++++++++++++++++++刷新

    private boolean mPullRefreshing = false; // is refreashing.

    // -- footer view
    private KExpandableListViewFooter mFooterView;

    private boolean mEnablePullLoad = false;// +++++++++++++++++++++++++++加载

    private boolean mPullLoading;

    private boolean mIsFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;

    private final static int SCROLLBACK_HEADER = 0;

    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration

    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.

    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.
    // private String
    // header_hint_normal =
    // "\u4e0b\u62c9\u5237\u65b0",//
    // 下拉刷新

    // private KConfig config;

    /**
     * @param context
     */
    public KExpandableListView(Context context) {
        this(context, null, 0, null);
    }

    public KExpandableListView(Context context, KConfig config) {
        this(context, null, 0, config);
    }

    public KExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public KExpandableListView(Context context, AttributeSet attrs, KConfig config) {
        this(context, attrs, 0, config);
    }

    public KExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    public KExpandableListView(Context context, AttributeSet attrs, int defStyle, KConfig config) {
        super(context, attrs, defStyle);
        if (config == null) {
            config = KConfig.getSimpleInstance();
        }
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, cc.easyandroid.customview.R.styleable.KListView);
            int count = a.getIndexCount();
            for (int i = 0; i < count; i++) {
                final int attr = a.getIndex(i);
                final int KListView_arrow_pic = cc.easyandroid.customview.R.styleable.KListView_arrow_pic;
                final int KListView_footer_hint_normal = cc.easyandroid.customview.R.styleable.KListView_footer_hint_normal;
                final int KListView_footer_hint_ready = cc.easyandroid.customview.R.styleable.KListView_footer_hint_ready;
                final int KListView_header_hint_loading = cc.easyandroid.customview.R.styleable.KListView_header_hint_loading;
                final int KListView_header_hint_normal = cc.easyandroid.customview.R.styleable.KListView_header_hint_normal;
                final int KListView_header_hint_ready = cc.easyandroid.customview.R.styleable.KListView_header_hint_ready;


                if (attr == KListView_arrow_pic) {
                    int arrow_pic_resId = a.getResourceId(attr, cc.easyandroid.customview.R.mipmap.klistview_arrow);
                    config.setArrow_pic_resId(arrow_pic_resId);
                } else if (attr == KListView_footer_hint_normal) {
                    config.setFooter_hint_normal(a.getText(attr));
                } else if (attr == KListView_footer_hint_ready) {
                    config.setFooter_hint_ready(a.getText(attr));
                } else if (attr == KListView_header_hint_loading) {
                    config.setHeader_hint_loading(a.getText(attr));
                } else if (attr == KListView_header_hint_normal) {
                    config.setHeader_hint_normal(a.getText(attr));
                } else if (attr == KListView_header_hint_ready) {
                    config.setHeader_hint_ready(a.getText(attr));
                }

//				switch (attr) {
//					case  KListView_arrow_pic:
//						int arrow_pic_resId = a.getResourceId(attr, R.drawable.klistview_arrow);
//						config.setArrow_pic_resId(arrow_pic_resId);
//						break;
//					case  2:
//						config.setFooter_hint_normal(a.getText(attr));
//						break;
//					case  KListView_footer_hint_ready:
//						config.setFooter_hint_ready(a.getText(attr));
//						break;
//					case  KListView_header_hint_loading:
//						config.setHeader_hint_loading(a.getText(attr));
//						break;
//					case  KListView_header_hint_normal:
//						config.setHeader_hint_normal(a.getText(attr));
//						break;
//					case  KListView_header_hint_ready:
//						config.setHeader_hint_ready(a.getText(attr));
//						break;
//				}
            }
            a.recycle();
        }
        setFooterDividersEnabled(false);
        initWithContext(context, config);
    }

    private void initWithContext(Context context, KConfig config) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);
        // stringHoder = new StringHoder(header_hint_normal, header_hint_ready,
        // header_hint_loading, footer_hint_ready, footer_hint_normal,
        // footer_heaght, header_heaght, arrow_pic);
        // init header view
        mHeaderView = new KExpandableListViewHeader(context, config);
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(cc.easyandroid.customview.R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView.findViewById(cc.easyandroid.customview.R.id.xlistview_header_time);
        addHeaderView(mHeaderView);

        // init footer view
        mFooterView = new KExpandableListViewFooter(context, config);
        /* 2014 04 22 cgp */
        mFooterView.hide();
		/* 2014 04 22 cgp */

        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(KListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(KListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(KListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(KListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(KListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(KListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);// 解决list item中间一条线问题（在item没有充满屏幕时候出现）

        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    /**
     * 加载一次后必须停下才能加载第二次
     */
    public void startLoadMore() {
        if (mListViewListener != null) {
            if (!mPullLoading) {
                // flipPage();
                mListViewListener.onLoadMore();
            }
        }
        mPullLoading = true;
        mFooterView.setState(KListViewFooter.STATE_LOADING);
    }

    /**
     * 刷新一次后必须停下才能刷新第二次
     */
    public void startRefresh() {
        if (mListViewListener != null) {
            if (!mPullRefreshing) {
                pageInit();
                mPullRefreshing = true;
                mListViewListener.onRefresh();// 刷新结束后，会判断是否在刷新，如果是就取消，所以mPullRefreshing要放在回调前面
                mHeaderView.setState(KListViewHeader.STATE_REFRESHING);
                return;
            }
        }
        mPullRefreshing = true;
        mHeaderView.setState(KListViewHeader.STATE_REFRESHING);
    }

    /**
     * 是否正在加载
     *
     * @return
     */
    public boolean isLoading() {
        return mPullLoading;
    }

    /**
     * 是否正在刷新
     *
     * @return
     */
    public boolean isRefreshing() {
        return mPullRefreshing;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        startRefresh();
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setKExpandableListViewListener(IKExpandableListViewListener l) {
        mListViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IKExpandableListViewListener {
        public void onRefresh();

        public void onLoadMore();
    }

    // 增加翻页功能
    private int page = 0;

    /**
     * 数据加载成功才翻页
     */
    public void flipPage() {
        page++;
    }

    public int getCurrentPage() {
        return page;
    }

    public void pageInit() {
        page = 0;
    }

    private int eachPageNumber;

    public void setEachPageNumber(int eachPageNumber) {
        this.eachPageNumber = eachPageNumber;
    }

    public void stopPull() {// 同时停止
        stopLoadMore();
        stopRefresh();
    }

    // 增加翻页功能
    public static class KConfig {
        private CharSequence header_hint_normal,// 下拉刷新
                header_hint_ready,// 松开刷新数据
                header_hint_loading, // 正在加载...
                footer_hint_ready, // 松开加载数据
                footer_hint_normal;// 上拉加载
        private int arrow_pic_resId;

        private KConfig() {
        }

        public static KConfig getSimpleInstance() {
            return new KConfig();
        }

        public CharSequence getHeader_hint_normal() {
            return header_hint_normal;
        }

        public KConfig setHeader_hint_normal(CharSequence header_hint_normal) {
            this.header_hint_normal = header_hint_normal;
            return this;
        }

        public CharSequence getHeader_hint_ready() {
            return header_hint_ready;
        }

        public KConfig setHeader_hint_ready(CharSequence header_hint_ready) {
            this.header_hint_ready = header_hint_ready;
            return this;
        }

        public CharSequence getHeader_hint_loading() {
            return header_hint_loading;
        }

        public KConfig setHeader_hint_loading(CharSequence header_hint_loading) {
            this.header_hint_loading = header_hint_loading;
            return this;
        }

        public CharSequence getFooter_hint_ready() {
            return footer_hint_ready;
        }

        public KConfig setFooter_hint_ready(CharSequence footer_hint_ready) {
            this.footer_hint_ready = footer_hint_ready;
            return this;
        }

        public CharSequence getFooter_hint_normal() {
            return footer_hint_normal;
        }

        public KConfig setFooter_hint_normal(CharSequence footer_hint_normal) {
            this.footer_hint_normal = footer_hint_normal;
            return this;
        }

        public int getArrow_pic_resId() {
            return arrow_pic_resId;
        }

        public KConfig setArrow_pic_resId(int arrow_pic_resId) {
            this.arrow_pic_resId = arrow_pic_resId;
            return this;
        }
    }

}
