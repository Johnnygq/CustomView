package com.gq.widget.edittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.gq.widget.R;

/**
 * 带有显示密码按钮的EditText
 * Created by GQ on 2016-07-30.
 */
public class PasswordEditText extends EditText implements EditText.OnFocusChangeListener {

    public static final String TAG = "PasswordEditText";

    public PasswordEditText(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private Drawable mRightDrawable;
    private Drawable mVisibilityOffDrawable;
    /**
     * Right Drawable 是否可见
     */
    private boolean mIsVisible;
    /**
     * 密码是否可见
     */
    private boolean mIsShown = false;

    /**
     * 是否正在显示Error
     */
    private boolean mErrorShowing;
    /**
     * 初始化时获取到的输入类型
     */
    private int mDefaultInputType;

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        Drawable drawables[] = getCompoundDrawables();
        mRightDrawable = drawables[2]; // Right Drawable;

        final Resources.Theme theme = context.getTheme();

        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ClearableEditText,defStyleAttr, defStyleRes);

        int rightDrawableColor = a.getColor(R.styleable.ClearableEditText_right_drawable_color,
                Color.BLACK);
        a.recycle();
        DrawableCompat.setTint(mRightDrawable, rightDrawableColor);

        mVisibilityOffDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_visibility_off_black, theme);
        mVisibilityOffDrawable.setBounds(mRightDrawable.getBounds());
        setOnFocusChangeListener(this);

        // 添加TextChangedListener
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged " + s);
                setDrawableVisible(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDefaultInputType = getInputType();

        // 第一次隐藏
        setDrawableVisible(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mErrorShowing && mIsVisible && event.getAction() == MotionEvent.ACTION_UP) {

            float x = event.getX();
            if (x >= getWidth() - getTotalPaddingRight() && x <= getWidth() - getPaddingRight()) {
//                Log.d(TAG, "点击密码按钮！");
                showPassword(!mIsShown);

            }

        }

        return super.onTouchEvent(event);
    }


    private void showPassword(boolean isShown) {
        mIsShown = isShown;

        int inputType = mDefaultInputType;
        if (isShown) {
            inputType |= InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        }

        setInputType(inputType);

//        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
//                isShown ? mVisibilityOffDrawable: mRightDrawable , getCompoundDrawables()[3]);

    }


    /**
     * 设置Right Drawable是否可见
     *
     * @param isVisible true for visible , false for invisible
     */
    public void setDrawableVisible(boolean isVisible) {

        Drawable drawable = mIsShown ? mVisibilityOffDrawable : mRightDrawable;

        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                isVisible ? drawable : null, getCompoundDrawables()[3]);

        mIsVisible = isVisible;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!mErrorShowing) {
            if (hasFocus) {
                if (getText().length() > 0) {
                    setDrawableVisible(true);
                }
            } else {
                setDrawableVisible(false);
            }
        }

    }

    @Override
    public void setError(CharSequence error, Drawable icon) {

        setDrawableVisible(false);

        super.setError(error, icon);
        // 如果error != null 代表错误提示正在显示，所以要隐藏mClearingDrawable
        mErrorShowing = error != null;
//        showPassword(mIsShown);
    }

}
