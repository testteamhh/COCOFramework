COCOFramework
=============

My android development framework, under heavy development.
Short code, rapid development, less bug, more fun


How to start
=============

Firstly, create a maven based android project with this dependency in your POM

```xml
<dependency>
  <groupId>com.cocosw.framework</groupId>
  <artifactId>framework</artifactId>
  <version>1.1-SNAPSHOT</version>
  <type>apklib</type>
</dependency>
```

FAQ
============

- Why no view injector
I don't see too much benefit to use view injector, with android-query and TypeAdapter, much less code need to be written.
Not only the findViewById but also the setters are time-killer, especially when you use ImageView.
Besides most of DI use runtime injector, which is performance harmful.

- Why no gradle yet?
Not too much popular libraries has gradle support when I develop this framework, I will support it when (most of) them are ready.It's not time taking.


Dependency
============
- COCO accessory
- okhttp
- http-request
- actionbarsherlock
- timber
- android-query
- crouton
- undobar
- activitylifecyclecallbackscompat
- gson
- wishlist

TODO
=============
- Gradle building
- ABS as optional dependency