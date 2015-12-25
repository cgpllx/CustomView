package cc.easyandroid.customview.progress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cc.easyandroid.customview.progress.core.KProgressClickListener;

public class KProgressLayout extends FrameLayout {
    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;
    private boolean isInited;

    public KProgressLayout(Context context) {
        this(context, null);
    }

    public KProgressLayout(Context context, AttributeSet attrs) {
        this(context, attrs, cc.easyandroid.customview.R.attr.kProgressLayoutsStyle);
    }

    public KProgressLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(cc.easyandroid.customview.R.layout.kprogresslayout_layout, this, true);
        // context.getTheme().obtainStyledAttributes(attrs)
        // a = context.obtainStyledAttributes(attrs, R.styleable.KProgressLayout);这个至获取
        TypedArray a = context.obtainStyledAttributes(attrs, cc.easyandroid.customview.R.styleable.KProgressLayout, defStyle, 0);

        int progressViewRef_id = a.getResourceId(cc.easyandroid.customview.R.styleable.KProgressLayout_loadingView, cc.easyandroid.customview.R.layout.kprogresslayout_loadingview);
        mLoadingView = inflater.inflate(progressViewRef_id, this, false);
        int errorViewRef_id = a.getResourceId(cc.easyandroid.customview.R.styleable.KProgressLayout_errorView, cc.easyandroid.customview.R.layout.kprogresslayout_errorview);
        mErrorView = inflater.inflate(errorViewRef_id, this, false);
        int emptyViewRef_id = a.getResourceId(cc.easyandroid.customview.R.styleable.KProgressLayout_emptyView, cc.easyandroid.customview.R.layout.kprogresslayout_errorview);
        mEmptyView = inflater.inflate(emptyViewRef_id, this, false);

        // a = context.obtainStyledAttributes(attrs, R.styleable.KProgressLayout);

        a.recycle();
        initKProgress();
    }

    int count = 0;

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() >= 3 && index < 0) {// 这里的3是initKProgress中的3个view
            index = getChildCount() - 3;
        }
        // System.out.println("KProgressLayout--index=" + index + "--getChildCount=" + getChildCount());
        super.addView(child, index, params);
        // if (!isInited) {
        // isInited = true;
        // initKProgress();
        // }
    }

    public void initKProgress() {

        if (mEmptyView != null) {
            this.addView(mEmptyView);
            mEmptyView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (kProgressClickListener != null) {
                        kProgressClickListener.onEmptyViewClick();
                    }
                }
            });
            mEmptyView.setVisibility(View.GONE);
        }

        if (mErrorView != null) {
            this.addView(mErrorView);
            mErrorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (kProgressClickListener != null) {
                        kProgressClickListener.onErrorViewClick();
                    }
                }
            });
            mErrorView.setVisibility(View.GONE);
        }
        if (mLoadingView != null) {
            this.addView(mLoadingView);
            mLoadingView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (kProgressClickListener != null) {
                        kProgressClickListener.onLoadingViewClick();
                    }
                }
            });
            mLoadingView.setVisibility(View.GONE);
        }
    }

    public static interface State {
        int LOADING = 0, EMPTY = 1, ERROR = 2, CANCEL = 3;
    }

    public void showLoadingView() {
        showView(State.LOADING);
    }

    public void showEmptyView() {
        showView(State.EMPTY);
    }

    public void showErrorView() {
        showView(State.ERROR);
    }

    public void cancelAll() {
        showView(State.CANCEL);
    }

    public void showView(int state) {

        boolean showLoadingView = false;
        boolean showEmptyView = false;
        boolean showErrorView = false;

        switch (state) {
            case State.LOADING:
                showLoadingView = true;
                break;
            case State.EMPTY:
                showEmptyView = true;
                break;
            case State.ERROR:
                showErrorView = true;
                break;
            case State.CANCEL:
                // 显示主界面
                break;
        }
        showProgress(showLoadingView, showEmptyView, showErrorView);
//		if (mLoadingView != null) {
//			mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
//
//			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
////			mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
//			mLoadingView.animate().setDuration(shortAnimTime).alpha(showLoadingView ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//				@Override
//				public void onAnimationEnd(Animator animation) {
//					mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
//				}
//			});
//		}
//
//		if (mEmptyView != null) {
//			mEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
//		}
//
//		if (mErrorView != null) {
//			mErrorView.setVisibility(showErrorView ? View.VISIBLE : View.GONE);
//		}
    }

    public void showProgress(final boolean showLoadingView, boolean showEmptyView, boolean showErrorView) {

        if (mLoadingView != null) {
//			mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
//			mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
//			mLoadingView.animate().setDuration(shortAnimTime).alpha(showLoadingView ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//				@Override
//				public void onAnimationEnd(Animator animation) {
//					mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
//				}
//			});
            mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
            mLoadingView.animate().setDuration(shortAnimTime).alpha(
                    showLoadingView ? 1 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
                }
            });
        }

        if (mEmptyView != null) {
            mEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
        }

        if (mErrorView != null) {
            mErrorView.setVisibility(showErrorView ? View.VISIBLE : View.GONE);
        }
    }

    private KProgressClickListener kProgressClickListener;

    public void setKProgressClickListener(KProgressClickListener kProgressClickListener) {
        this.kProgressClickListener = kProgressClickListener;
    }
}
