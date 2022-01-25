package com.youer.floatwindow;

/**
 * @author youer
 * @date 2022/1/24
 */
public abstract class IFloatWindowImpl {
    public abstract void show();

    public abstract void hide();

    public abstract void dismiss();

    public abstract boolean isShowing();

    public abstract void updateLocation(int x, int y);
} 