package cc.easyandroid.customview.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.support.v7.widget.TintManager;
import android.support.v7.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cc.easyandroid.customview.R;


/**
 * 自定义返回键
 */
public class EasyBackToolBar extends EasyToolBar {
    private int mBackKeyTextColor;
    private Drawable mDrawableLeft;
    private TextView mBackKeyTextView;
    private int mBackTextAppearance;
    private Drawable controlBackground;

    public EasyBackToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public EasyBackToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.EasyToolBarStyle);
    }

    public EasyBackToolBar(Context context) {
        this(context, null);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.EasyToolBars, defStyleAttr, 0);
        //controlBackground 背景
        controlBackground = a.getDrawable(R.styleable.EasyToolBar_backKeyBackground);
        //文本
        final CharSequence backKeyText = a.getText(R.styleable.EasyToolBar_backKeyText);
        if (!TextUtils.isEmpty(backKeyText)) {
            setBackKeyText(backKeyText);
        }
        //颜色
        if (a.hasValue(R.styleable.EasyToolBar_backKeyTextColor)) {
            setBackKeyTextColor(a.getColor(R.styleable.EasyToolBar_backKeyTextColor, 0xffffffff));
        }
        //图片
        Drawable drawableLeft = a.getDrawable(R.styleable.EasyToolBar_backKeyImage);
        setBackKeyImage(drawableLeft);
        //        setBackKeyImage(drawableLeft);
        //样式  size color
        int backTextAppearance = a.getResourceId(R.styleable.EasyToolBar_backKeyTextAppearance, 0);
        setBackKeyTextAppearance(context, backTextAppearance);
        a.recycle();

    }

    public void setBackKeyText(CharSequence backKeyText) {
        if (!TextUtils.isEmpty(backKeyText)) {
            final Context context = getContext();
            if (mBackKeyTextView == null) {
                mBackKeyTextView = new TextView(context);
                mBackKeyTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL | Gravity.LEFT);
                mBackKeyTextView.setClickable(true);
                mBackKeyTextView.setVisibility(View.GONE);
                if (controlBackground != null) {
                    mBackKeyTextView.setBackgroundDrawable(controlBackground);
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        mBackKeyTextView.setForeground(controlBackground);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= 16) {
                        mBackKeyTextView.setBackground(controlBackground);
                    } else {
                        mBackKeyTextView.setBackgroundDrawable(controlBackground);
                    }
                }
                mBackKeyTextView.setLayoutParams(new EasyToolBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                addView(mBackKeyTextView);
            }
            if (mBackKeyTextView != null) {
                mBackKeyTextView.setText(backKeyText);
                if (mBackTextAppearance != 0) {
                    mBackKeyTextView.setTextAppearance(context, mBackTextAppearance);
                }
                if (mBackKeyTextColor != 0) {
                    mBackKeyTextView.setTextColor(mBackKeyTextColor);
                }
            }
        }
    }

    public void setDisplayHomeAsUpEnabled(boolean displayHomeAsUpEnabled) {
        if (mBackKeyTextView != null) {
            if (displayHomeAsUpEnabled) {
                if (mBackKeyTextView.getVisibility() != View.VISIBLE) {
                    mBackKeyTextView.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBackKeyTextView.getVisibility() != View.GONE) {
                    mBackKeyTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setBackKeyTextAppearance(Context context, @StyleRes int resId) {
        mBackTextAppearance = resId;
        if (this.mBackKeyTextView != null) {
            this.mBackKeyTextView.setTextAppearance(context, resId);
        }
    }

    public void setBackKeyTextColor(@ColorInt int color) {
        mBackKeyTextColor = color;
        if (mBackKeyTextView != null) {
            mBackKeyTextView.setTextColor(color);
        }
    }

    public void setBackKeyImage(@DrawableRes int resId) {
        Drawable drawableLeft = TintManager.getDrawable(getContext(), resId);
        setBackKeyImage(drawableLeft);
    }

    public void setBackKeyImage(Drawable drawableLeft) {
        mDrawableLeft = drawableLeft;
        if (mBackKeyTextView != null) {
            mBackKeyTextView.setCompoundDrawables(drawableLeft, null, null, null);
            mBackKeyTextView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        }
    }

    /**
     * 返回键点击监听
     *
     * @param l
     */
    public void setOnBackKeyClickLisener(OnClickListener l) {
        if (mBackKeyTextView != null) {
            mBackKeyTextView.setOnClickListener(l);
        }
    }
}
