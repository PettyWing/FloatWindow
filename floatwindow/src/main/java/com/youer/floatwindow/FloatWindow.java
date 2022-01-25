package com.youer.floatwindow;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.youer.floatwindow.TagMode.Mode;
import com.youer.floatwindow.permission.FloatPermissionListener;

/**
 * 悬浮窗
 *
 * @author youer
 * @date 2021/11/30
 */
public class FloatWindow {

    private static Map<String, IFloatWindowImpl> floatWindowMap;
    public static final String DEFAULT_TAG = "defaultTag";

    public static Builder with(Context context, View view) {
        return new Builder(context, view);
    }

    public static IFloatWindowImpl get() {
        return get(DEFAULT_TAG);
    }

    public static IFloatWindowImpl get(String tag) {
        return floatWindowMap == null ? null : floatWindowMap.get(tag);
    }

    public static void remove(String tag) {
        if (floatWindowMap == null || !floatWindowMap.containsKey(tag)) {
            return;
        }
        floatWindowMap.remove(tag);
    }

    public static void destroy() {
        destroy(DEFAULT_TAG);
    }

    public static void destroy(String tag) {
        if (floatWindowMap == null || !floatWindowMap.containsKey(tag)) {
            return;
        }
        floatWindowMap.get(tag).dismiss();
        floatWindowMap.remove(tag);
    }

    public static void destroyAll() {
        if (floatWindowMap == null) {
            return;
        }
        for (Map.Entry<String, IFloatWindowImpl> entry : floatWindowMap.entrySet()) {
            entry.getValue().dismiss();
        }
        floatWindowMap = null;
    }

    public static class Builder {
        Context context;
        View view;
        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        int startX;
        int startY;
        FloatPermissionListener permissionListener;
        ViewStateListener viewStateListener;
        boolean editable;
        boolean isSystemPopup;
        String tag;
        @Mode
        int tagMode;

        public Builder(Context context, View view) {
            this.context = context;
            this.view = view;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setWidth(@Screen.ScreenType int screen, float radio) {
            this.width = ScreenTool.getSize(context, screen, radio);
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setHeight(@Screen.ScreenType int screen, float radio) {
            this.height = ScreenTool.getSize(context, screen, radio);
            return this;
        }

        public Builder setStartX(int startX) {
            this.startX = startX;
            return this;
        }

        public Builder setStartX(@Screen.ScreenType int screen, float radio) {
            setStartX(ScreenTool.getSize(context, screen, radio));
            return this;
        }

        public Builder setStartY(int startY) {
            this.startY = startY;
            return this;
        }

        public Builder setStartY(@Screen.ScreenType int screen, float radio) {
            setStartY(ScreenTool.getSize(context, screen, radio));
            return this;
        }

        public Builder setPermissionListener(FloatPermissionListener permissionListener) {
            this.permissionListener = permissionListener;
            return this;
        }

        public Builder setViewStateListener(ViewStateListener viewStateListener) {
            this.viewStateListener = viewStateListener;
            return this;
        }

        public Builder setEditable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder setSystemPopup(boolean systemPopup) {
            isSystemPopup = systemPopup;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            this.tagMode = Mode.REPLACE;
            return this;
        }

        public Builder setTag(String tag, @Mode int tagMode) {
            this.tag = tag;
            this.tagMode = tagMode;
            return this;
        }

        public IFloatWindowImpl build() {
            if (floatWindowMap == null) {
                floatWindowMap = new HashMap<>();
            }
            if (context == null && view == null) {
                throw new IllegalArgumentException("请进行初始化");
            }
            if (floatWindowMap.containsKey(tag)) {
                if (tagMode == Mode.REPLACE) {
                    // 删除调原有的floatwindow
                    floatWindowMap.get(tag).dismiss();
                    floatWindowMap.remove(tag);
                } else if (tagMode == Mode.LAST) {
                    // 维持显示原来的floatwindow
                    return floatWindowMap.get(tag);
                }
            }
            IFloatWindowImpl floatWindowImpl = new FloatWindowImpl(this);
            floatWindowMap.put(tag, floatWindowImpl);
            return floatWindowImpl;
        }
    }

}