COCOFramework
=============

My android development framework, under heavy development.
Short code, rapid development, less bug, more fun


How to start
=============

- Create a normal android project with gradle building
- Change build.gradle by refering (demo)[https://github.com/soarcn/COCOFramework/blob/master/app/build.gradle]


Sample (Under development)
===========

We provide a sample application to help you better understand how the COCOFramework could boost your android development work.

The sample application is called Dridddle, a simple client for dribbble.com:

- Support android 2.3 +
- Simple network communication
- Style application accent by theme
- Support dualpane layout and screen rotation
- Endless data loading
- Parallex scrolling & Actionbar fading
- Powerful debug tools(TODO)
- Integrate online statistics tools(TODO)

![UndoBar Sample](https://raw.githubusercontent.com/soarcn/COCOFramework/master/arts/port1.png)
![UndoBar Sample](https://raw.githubusercontent.com/soarcn/COCOFramework/master/arts/port2.png)
![UndoBar Sample](https://raw.githubusercontent.com/soarcn/COCOFramework/master/arts/lands.png)

Style your application
============

Coco framework enable you to define your overall application visual style same as API v20.

![Theme](http://developer.android.com/preview/material/images/ThemeColors.png)

How to define your app style
---------

- Create your theme, you can extend your theme from one of preset themes.
- Customize theme color in your theme for example
```xml
    <style name="TODOTheme" parent="@style/Theme.App.Translucent">
        <item name="colorPrimary">@color/themecolor</item>
        <item name="navigationBarColor">@color/transparent</item>
        <item name="actionBarStyle">@style/LightActionBar</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
    </style>
```
- To compatible with API 20, create a separate style under values-v20 folder


Translucent
----------

Coco framework have build-in support for translucent UI, which build on modified (SystemBarTint)[https://github.com/jgilfelt/SystemBarTint]

- You can change the color of status and navigation bars in your theme.
- Framwork will set view padding for adapting the screen bound in most of case
- If you want to control the layout padding by your self, you can override onInsetsChanged method in your activity/fragment class. For example
```java
    @Override
    public void onInsetsChanged(final SystemBarTintManager.SystemBarConfig insets) {
        mBottomContent.setPadding(0,0,0,insets.getPixelInsetBottom());
    }
```
