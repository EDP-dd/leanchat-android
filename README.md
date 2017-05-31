# LeanChat Android 客户端

## 简介

LeanChat 是 [LeanCloud](http://leancloud.cn) [实时通信](https://leancloud.cn/docs/realtime.html) 组件的 Demo，通过该应用你可以学习和了解 LeanCloud 实时通信功能。

## 下载
请直接点击 Github 上的`Download Zip`，如图所示，这样只下载最新版本。如果是 `git clone`，则可能非常慢，因为含杂很大的提交历史。某次测试两者是1.5M:40M。

![qq20150618-2 2x](https://cloud.githubusercontent.com/assets/5022872/8223520/4c25415a-15ab-11e5-912d-b5dab916ce86.png)

## LeanChat Android 项目构成

* leanchat，为整个聊天应用。它包含好友管理、群组管理、地理消息、附近的人、个人页面、登录注册的功能，完全基于 LeanCloud 的存储和通信功能。

注：新的版本已经删除了 leanchatlib、leanchatlib-demo，其相关的功能已经转移到 ChatKit，LeanChat 的核心页面会话列表页面及聊天页面均来自于 ChatKit，如果仅仅是要集成 LeanCloud 实时通讯可以使用 ChatKit，关于 ChatKit 可以参考 [ChatKit 文档](https://leancloud.cn/docs/chatkit-android.html)、[ChatKit 代码](https://github.com/leancloud/LeanCloudChatKit-Android)。

## LeanChat 项目构成

* [leanchat-android](https://github.com/leancloud/leanchat-android)，Android 客户端
* [leanchat-ios](https://github.com/leancloud/leanchat-ios)，iOS 客户端
* [leanchat-webapp](https://github.com/leancloud/leanchat-webapp)，Web 客户端
* [leanchat-cloud-code](https://github.com/leancloud/leanchat-cloudcode)，服务端

## Eclipse 或 Intellij IDEA 运行需知
1. 请装相应的 Gradle 插件
1. Intellij IDEA 用户建议装 Android Studio，AS 是基于 IDEA 的，不仅有和 IDEA 一样的体验，还是官方推荐的 IDE。
1. 用到了 [ButterKnife](https://github.com/JakeWharton/butterknife) 开源库，Eclipse需要设置一下来支持 ButterKnife 的注解，具体如何设置见 http://jakewharton.github.io/butterknife/ide-eclipse.html 。否则会因为 view 没有绑定上，导致崩溃。

## 相关文档

* [实时通信服务开发指南](https://leancloud.cn/docs/realtime_v2.html)
* [wiki](https://github.com/leancloud/leanchat-android/wiki)

## 依赖组件

LeanChat Android 客户端依赖 LeanCloud Android SDK 如下组件：

* 基础模块
* 实时通信模块
* 统计模块

如果需要这些模块 SDK，可以到 [这里](https://cn.avoscloud.com/docs/sdk_down.html) 下载或者像项目里用 Gradle 方式引入。

## 技术支持

关于此项目遇到的问题可提 [issue](https://github.com/leancloud/leanchat-android/issues)。




