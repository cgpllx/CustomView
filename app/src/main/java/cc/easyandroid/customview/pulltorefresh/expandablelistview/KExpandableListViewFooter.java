package cc.easyandroid.customview.pulltorefresh.expandablelistview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.easyandroid.customview.R;


public class KExpandableListViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;

    private Context mContext;

    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;
    private KExpandableListView.KConfig config;

    public KExpandableListViewFooter(Context context, KExpandableListView.KConfig config) {
        super(context);
        this.config = config;
        initView(context);
    }

    public KExpandableListViewFooter(Context context, AttributeSet attrs, KExpandableListView.KConfig config) {
        super(context, attrs);
        this.config = config;
        initView(context);
    }

    public void setState(int state) {
        mHintView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHintView.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {
            mHintView.setVisibility(View.VISIBLE);

            CharSequence footer_hint_ready = config.getFooter_hint_ready();
            mHintView.setText(!TextUtils.isEmpty(footer_hint_ready) ? footer_hint_ready : getResources().getString(R.string.xlistview_footer_hint_ready));

        } else if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mHintView.setVisibility(View.VISIBLE);

            CharSequence footer_hint_normal = config.getFooter_hint_normal();
            mHintView.setText(!TextUtils.isEmpty(footer_hint_normal) ? footer_hint_normal : getResources().getString(R.string.xlistview_footer_hint_normal));
        }
    }

    public void setBottomMargin(int height) {
        if (height < 0)
            return;
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }

    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * loading status
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }

    private void initView(Context context) {
        mContext = context;
        // LinearLayout moreView = (LinearLayout) ViewFactory.getKListview_footer(mContext, config.getFooter_heaght());
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.klistview_footer, null);

        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.xlistview_footer_content);
        mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) moreView.findViewById(R.id.xlistview_footer_hint_textview);
    }

}
