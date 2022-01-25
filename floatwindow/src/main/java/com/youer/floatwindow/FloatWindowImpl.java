package com.youer.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.youer.floatwindow.permission.FloatPermissionActivity;
import com.youer.floatwindow.permission.FloatPermissionListener;

/**
 * @author youer
 * @date 2022/1/24
 */
public class FloatWindowImpl extends IFloatWindowImpl {
    /**
     * 手指移动位置
     */
    private static final String TAG = "FloatWindow";
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Context context;
    private String tag;
    /**
     * 内容View
     */
    private View contentView;
    /**
     * View的宽
     */
    private int width = FrameLayout.LayoutParams.WRAP_CONTENT;
    /**
     * View的高
     */
    private int height = FrameLayout.LayoutParams.WRAP_CONTENT;
    /**
     * 悬浮窗起始位置
     */
    private int startX, startY;

    /**
     * 触摸点相对于view左上角的坐标
     */
    private float downX, downY, upX, upY;
    private int mx;
    private int my;
    private boolean firstShow = true;
    private boolean showing;
    private FloatPermissionListener permissionListener;
    private ViewStateListener viewStateListener;
    private boolean isSystemPopup = false;
    // 是否可编辑
    private boolean editable;

    public FloatWindowImpl(FloatWindow.Builder builder) {
        build(builder);
        initWindowManager();
        initLayoutParams();
        initFloatView();
    }

    private void build(FloatWindow.Builder builder) {
        this.context = builder.context;
        this.contentView = builder.view;
        this.width = builder.width;
        this.height = builder.height;
        this.startX = builder.startX;
        this.startY = builder.startY;
        this.permissionListener = builder.permissionListener;
        this.viewStateListener = builder.viewStateListener;
        this.editable = builder.editable;
        this.tag = builder.tag;
        if (!builder.isSystemPopup && context instanceof Activity) {
            this.isSystemPopup = false;
        } else {
            this.isSystemPopup = true;
        }

    }

    private void initWindowManager() {
        if (isSystemPopup) {
            windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        } else {

            windowManager = ((Activity)context).getWindowManager();
        }
    }

    private void initLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        if (isSystemPopup) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
        }
        // 设置之后window永远不会获取焦点,所以用户不能给此window发送点击事件,焦点会传递给在其下面的可获取焦点的window
        // windowManger.LayoutParams flag含义 https://www.jianshu.com/p/b2580adcfcd2
        if (editable) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        //悬浮窗起始位置
        layoutParams.x = mx = startX;
        layoutParams.y = my = startY;
    }

    private void initFloatView() {
        contentView.setOnTouchListener(new ItemViewTouchListener());
    }

    /**
     * 设置悬浮窗可以获取焦点，用于弹出输入框
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        if (editable) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        windowManager.updateViewLayout(contentView, layoutParams);
    }

    @Override
    public void show() {
        if (firstShow) {
            if (isSystemPopup) {
                FloatPermissionActivity.requestPermission(context, new FloatPermissionListener() {
                    @Override
                    public void onAcquired() {
                        firstShow();
                        if (permissionListener != null) {
                            permissionListener.onAcquired();
                        }
                    }

                    @Override
                    public void onSuccess() {
                        firstShow();
                        if (permissionListener != null) {
                            permissionListener.onSuccess();
                        }
                    }

                    @Override
                    public void onFailed() {
                        if (permissionListener != null) {
                            permissionListener.onFailed();
                        }
                    }

                });
            } else {
                firstShow();
            }
        } else {
            if (isShowing()) {
                return;
            }
            contentView.setVisibility(View.VISIBLE);
            showing = true;
            if (viewStateListener != null) {
                viewStateListener.onShow();
            }
        }
    }

    private void firstShow() {
        if (isShowing()) {
            return;
        }
        windowManager.addView(contentView, layoutParams);
        showing = true;
        firstShow = false;
        if (viewStateListener != null) {
            viewStateListener.onShow();
        }
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void hide() {
        if (!showing) {
            return;
        }
        contentView.setVisibility(View.GONE);
        showing = false;
        if (viewStateListener != null) {
            viewStateListener.onHide();
        }
    }

    @Override
    public void dismiss() {
        if (!showing) {
            return;
        }
        windowManager.removeView(contentView);
        showing = false;
        firstShow = true;
        FloatWindow.remove(tag);
        if (viewStateListener != null) {
            viewStateListener.onDismiss();
        }
    }

    /**
     * 更新位置
     */
    @Override
    public void updateLocation(int x, int y) {
        layoutParams.x = mx = x;
        layoutParams.y = my = y;

        windowManager.updateViewLayout(contentView, layoutParams);
    }

    class ItemViewTouchListener implements OnTouchListener {
        float lastX, lastY, changeX, changeY;
        boolean click = false;

        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getRawX();
                    downY = event.getRawY();
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    if (viewStateListener != null) {
                        viewStateListener.onActionDown(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    //拖动事件下一直计算坐标 然后更新悬浮窗位置
                    changeX = event.getRawX() - lastX;
                    changeY = event.getRawY() - lastY;
                    int movedX = (int)(mx + changeX);
                    int movedY = (int)(my + changeY);
                    //拖动事件下一直计算坐标 然后更新悬浮窗位置
                    updateLocation(movedX, movedY);
                    if (viewStateListener != null) {
                        viewStateListener.onActionMove(event, movedX, movedY);
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    upX = event.getRawX();
                    upY = event.getRawY();
                    //click = (Math.abs(upX - downX) > 5) || (Math.abs(upY - downY) > 5);
                    if (viewStateListener != null) {
                        viewStateListener.onActionUp(event);
                    }
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    if (viewStateListener != null) {
                        viewStateListener.onActionOutSide(event);
                    }
                    break;
                default:
                    break;
            }
            return click;
        }
    }
} 