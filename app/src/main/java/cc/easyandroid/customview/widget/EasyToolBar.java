package cc.easyandroid.customview.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;


public class EasyToolBar extends Toolbar {

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

    private int mTitleTextColor;
    private int mSubtitleTextColor;

    private float mTitleSize = 20f;
    private int mUnit = TypedValue.COMPLEX_UNIT_SP;

    private CharSequence mTitleText;
    private CharSequence mSubtitleText;

    private int mEasyTitleTextAppearance;
    private int mEasySubtitleTextAppearance;

    public EasyToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public EasyToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EasyToolBar(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        try {
            Class toolBar_Class = getToolBarClass(getClass());
            if (toolBar_Class != null) {
                Field mTitleTextAppearanceField = toolBar_Class.getDeclaredField("mTitleTextAppearance");
                mTitleTextAppearanceField.setAccessible(true);
                mEasyTitleTextAppearance = mTitleTextAppearanceField.getInt(this);
                setTitleTextAppearance(context, mEasyTitleTextAppearance);
                Field mSubtitleTextAppearanceField = toolBar_Class.getDeclaredField("mSubtitleTextAppearance");
                mSubtitleTextAppearanceField.setAccessible(true);
                mEasySubtitleTextAppearance = mSubtitleTextAppearanceField.getInt(this);
                setSubtitleTextAppearance(context, mEasySubtitleTextAppearance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class getToolBarClass(Class clazz) {
        Class super_clazz = null;
        if (clazz != null) {
            super_clazz = clazz.getSuperclass();
            if (super_clazz != null && super_clazz.getName().equals(Toolbar.class.getName())) {
                return super_clazz;
            } else {
                super_clazz = getToolBarClass(super_clazz);
            }
        }
        return super_clazz;
    }

    public void setTitleSize(float size) {
        setTitleSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setTitleSize(int unit, float size) {
        this.mUnit = unit;
        this.mTitleSize = size;
        if (mTitleTextView != null) {
            mTitleTextView.setTextSize(mUnit, mTitleSize);
        }
    }

    @Override
    public CharSequence getTitle() {
        return mTitleText;
    }

    @Override
    public CharSequence getSubtitle() {
        return mSubtitleText;
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
        if (!TextUtils.isEmpty(subtitle)) {
            if (this.mSubtitleTextView == null) {
                Context context = getContext();
                this.mSubtitleTextView = new TextView(context);
                this.mSubtitleTextView.setSingleLine();
                this.mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if (this.mEasySubtitleTextAppearance != 0) {
                    this.mSubtitleTextView.setTextAppearance(context, this.mEasySubtitleTextAppearance);
                }
                if (this.mSubtitleTextColor != 0) {
                    this.mSubtitleTextView.setTextColor(this.mSubtitleTextColor);
                }
            }
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            addSystemView(mSubtitleTextView, lp);
        }
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setText(subtitle);
        }
        this.mSubtitleText = subtitle;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            if (this.mTitleTextView == null) {
                Context context = getContext();
                this.mTitleTextView = new TextView(context);
                this.mTitleTextView.setSingleLine();
                this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if (this.mEasyTitleTextAppearance != 0) {
                    this.mTitleTextView.setTextAppearance(context, this.mEasyTitleTextAppearance);
                }
                if (this.mTitleTextColor != 0) {
                    this.mTitleTextView.setTextColor(this.mTitleTextColor);
                }
            }
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            addSystemView(mTitleTextView, lp);
        }
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setText(title);
        }
        this.mTitleText = title;

    }

    private LinearLayout titleLayout;

    private void addSystemView(View child, LayoutParams params) {
        if (titleLayout == null) {
            titleLayout = new LinearLayout(getContext());
            titleLayout.setOrientation(LinearLayout.VERTICAL);
            titleLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            addView(titleLayout, lp);
        }
        if (child.getParent() == null) {
            titleLayout.addView(child, params);
        }
    }

    @Override
    public void setTitleTextColor(int color) {
        this.mTitleTextColor = color;
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setTextColor(color);
        }
    }

    /**
     * Sets the text color of the subtitle, if present.
     *
     * @param color The new text color in 0xAARRGGBB format
     */
    public void setSubtitleTextColor(@ColorInt int color) {
        mSubtitleTextColor = color;
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextColor(color);
        }
    }

    public void setSubtitleTextAppearance(Context context, @StyleRes int resId) {
        if (this.mSubtitleTextView != null)
            this.mSubtitleTextView.setTextAppearance(context, resId);
    }

    public void setTitleTextAppearance(Context context, @StyleRes int resId) {
        if (this.mTitleTextView != null)
            this.mTitleTextView.setTextAppearance(context, resId);
    }
}
