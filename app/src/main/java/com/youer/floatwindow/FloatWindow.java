package com.youer.floatwindow;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 悬浮窗
 *
 * @author youer
 * @date 2021/11/30
 */
public class FloatWindow {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Context context;
    /**
     * 待悬浮的View
     */
    private View view;
    /**
     * View的宽
     */
    private int width = FrameLayout.LayoutParams.WRAP_CONTENT;
    /**
     * View的高
     */
    private int height = FrameLayout.LayoutParams.WRAP_CONTENT;
    /**
     * View在FloatWindow中的gravity
     */
    private int gravity = Gravity.LEFT;
    /**
     * 悬浮窗起始位置
     */
    private int startX, startY;

    /**
     * 手指按下位置
     */
    private int downX, downY;
    private boolean showing;

    /**
     * 手指移动位置
     */
    private int rowX, rowY;

    public FloatWindow(Builder builder) {
        this.context = builder.context;
        this.view = builder.view;
        this.width = builder.width;
        this.height = builder.height;
        this.gravity = builder.gravity;
        this.startX = builder.startX;
        this.startY = builder.startY;
        initWindowManager();
        initLayoutParams();
        initView();
    }

    private void initWindowManager() {
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    private void initLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.gravity = gravity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 设置之后window永远不会获取焦点,所以用户不能给此window发送点击事件,焦点会传递给在其下面的可获取焦点的window
        // windowManger.LayoutParams flag含义 https://www.jianshu.com/p/b2580adcfcd2
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //悬浮窗起始位置
        layoutParams.x = startX;
        layoutParams.y = startY;
    }

    private void initView() {
        view.setOnTouchListener(new ItemViewTouchListener());
    }

    /**
     * 更新位置
     */
    public void updateLocation(float movedX, float movedY) {
        layoutParams.x += (int)movedX;
        layoutParams.y += (int)movedY;
        windowManager.updateViewLayout(view, layoutParams);
    }

    /**
     * 设置悬浮窗可以获取焦点，用于弹出输入框
     *
     * @param editable
     */
    public void setFocusable(boolean editable) {
        if (editable) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        windowManager.updateViewLayout(view, layoutParams);
    }

    public void show() {
        if (!FloatPermissionUtil.requestFloatPermission(context)) {
            return;
        }
        if (isShowing()) {
            return;
        }
        windowManager.addView(view, layoutParams);
        showing = true;
    }

    public boolean isShowing() {
        return showing;
    }

    public void dismiss() {
        if (!showing) {
            return;
        }
        windowManager.removeView(view);
        showing = false;
    }

    class ItemViewTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int)event.getRawX();
                    downY = (int)event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    rowX = (int)event.getRawX();
                    rowY = (int)event.getRawY();
                    int movedX = rowX - downX;
                    int movedY = rowY - downY;
                    downX = rowX;
                    downY = rowY;
                    updateLocation(movedX, movedY);
                case MotionEvent.ACTION_UP:
                default:
                    break;
            }
            return true;
        }
    }

    public static class Builder {
        private Context context;
        private View view;
        private int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int gravity = Gravity.LEFT;
        private int startX;
        private int startY;

        public Builder(Context context, View view) {
            this.context = context;
            this.view = view;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setStartX(int startX) {
            this.startX = startX;
            return this;
        }

        public Builder setStartY(int startY) {
            this.startY = startY;
            return this;
        }

        public FloatWindow build() {
            return new FloatWindow(this);
        }
    }
} 