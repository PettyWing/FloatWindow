<a name="IeUgc"></a>
# 简介
一个可拖动的悬浮窗<br />
<a name="DJf47"></a>
# 集成
<a name="S4pEt"></a>
## 第 1 步、在工程的 build.gradle 中添加：
```java
allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()()
    }
}
```
<a name="eq1Se"></a>
## 第 2 步、在应用的 build.gradle 中添加：
```java
dependencies {
    implementation 'io.github.pettywing:floatwindow:1.0.0'
}
```
<a name="QhoLG"></a>
# 使用

- 支持多个悬浮窗的管控
- 支持拖动
- 内部自动进行权限申请操作
- 支持位置和宽高设置百分比
- 支持权限申请结果、位置等状态监听
- 支持弹起应用内或者应用外的悬浮窗
<a name="FJ2bj"></a>
## 基础使用
```java
FloatWindow.with(this, floatView)
    .setStartX(Screen.WIDTH, 0.5f)
    .setStartY(Screen.HEIGHT, 0.5f)
    .build();

// 显示
floatWindow.show();
// 隐藏
floatWindow.dismiss();
// 关闭
floatWindow.dismiss();
// 更新位置
floatWindow.updateLocation(100,200);
```
<a name="ASL1w"></a>
## builder参数详解
<a name="cN5oT"></a>
### 1. setTag 
> 多个悬浮窗管控

```java

// 在新建时可以手动设置tag来唯一标记悬浮窗
FloatWindow.with(this, floatView)
    .setTag(FLOAT_WINDOW_TAG)
    .build().show();  
// 获取的时候可以根据tag来获取
FloatWindow.get(FLOAT_WINDOW_TAG);
    
// 如果没有设置tag，默认会设置tag为defaultTag
FloatWindow.get();
```
<a name="aG7H4"></a>
### 2. setSystemPopup
> 设置应用外和应用内悬浮窗   true为应用外悬浮窗，false为应用内悬浮窗

- 应用内悬浮窗：
   - 不需要获取权限
   - 依托于Activity的windowManager，只能在当前activity显示
- 应用外悬浮窗：
   - 需要获取权限
   - 通过system获取的windowManager，可以在任何地方显示
<a name="kGmR4"></a>
### 3. setViewStateListener
> 设置悬浮窗滑动状态监听

<a name="K2jEF"></a>
### 4. setPermissionListener
> 设置悬浮窗权限获取监听

<a name="UvVJb"></a>
### 5. 设置宽高和起始位置
```java
FloatWindow.with(this, floatView)
            .setWidth(LayoutParams.MATCH_PARENT)
            .setHeight(LayoutParams.WRAP_CONTENT)
            .setStartX(Screen.WIDTH, 0.5f)
            .setStartY(Screen.HEIGHT, 0.5f)
```


<a name="q1Q6W"></a>
# 碰到的问题

1. layoutParams.gravity只能设置为Gravity.START
> 当gravity被设置为别的时候，会导致拖动时候的起始位置计算异常，导致拖动错误，如果要设置到其他位置，只能通过修改layoutParams的x和y

<br />

2. 弹窗输入框无法编辑
> 默认悬浮窗的layoutParams flag设置的是`WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL<br />    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;`
> 这种情况下，window的外部可以响应点击，但是内部就无法获取到焦点，导致输入框无法被拉起。
> 我没有找到很好的解决方案，目前采用的是添加一个editable的状态，在可编辑状态下不开启FLAG_NOT_FOCUSABLE
>



<a name="YHyCr"></a>
# 参考文档
[Android悬浮窗看这篇就够了](https://juejin.cn/post/6951608145537925128#heading-6)<br />[FloatWindowUtils](https://github.com/realskyrin/FloatWindowUtils)<br />[WindowManager.LayoutParams的各种flag含义](https://www.jianshu.com/p/b2580adcfcd2)
<a name="Sen0x"></a>
## 其他
[发布模块到Maven](https://www.yuque.com/youer-ycy0r/xx8eue/yx8iwm)<br />[Android技能树点亮计划Git库](https://github.com/PettyWing/AndroidSkillTree)<br />[Android技能树点亮计划-语雀文档库](https://www.yuque.com/youer-ycy0r/xx8eue/tx33l9)<br />稀土掘金：悠二<br />Github：[PettyWing](https://github.com/PettyWing)
