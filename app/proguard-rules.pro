# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Programming\Android\StudioSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn
# 指定代码的压缩级别
-optimizationpasses 5
# 不使用大小写混合
-dontusemixedcaseclassnames
# 混淆第三方jar
-dontskipnonpubliclibraryclasses
# 混淆时不做预校验
-dontpreverify
 # 混淆时记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
##保持泛型不被混淆
-keepattributes Signature


-keepattributes InnerClasses,LineNumberTable
#google权限控制---------------------------begin
-dontwarn rx.internal.**
-dontwarn pub.devrel.**
#------------------------------------------end

#不混淆android-async-http
-keep class com.loopj.android.http.**{*;}
#不混淆org.apache.http.legacy.jar
 -dontwarn android.net.compatibility.**
 -dontwarn android.net.http.**
 -dontwarn com.android.internal.http.multipart.**
 -dontwarn org.apache.commons.**
 -dontwarn org.apache.http.**
 -keep class android.net.compatibility.**{*;}
 -keep class android.net.http.**{*;}
 -keep class com.android.internal.http.multipart.**{*;}
 -keep class org.apache.commons.**{*;}
 -keep class org.apache.http.** {*;}
-dontwarn org.bytedeco.javacv.**
#支付宝SDK-------------------------------------begin
-dontwarn com.alipay.**
-dontwarn com.ta.**
-dontwarn com.ut.**
-keep class com.alipay.** {*;}
-keep class com.ta.**
-keep class com.ut.**
-keep class org.json.alipay.**
#---------------------------------------------end

#百度地图--------------------------------------begin
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**
#----------------------------------------------end

#微信SDK-------------------------------------begin
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}
#---------------------------------------------end

#银联SDK-------------------------------------begin
-dontwarn com.unionpay.**
-keep class com.unionpay.** { *; }
#---------------------------------------------end
#小米推送SDK-------------------------------------begin
#这里com.xiaomi.mipushdemo.DemoMessageRreceiver改成app中定义的完整类名
#-keep class com.lenovohit.hospitals.view.MiPushMessageReceiver {*;}
##可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。
-dontwarn com.xiaomi.push.**
#---------------------------------------------end

#防止报错Warning:com.baidu.platform.comapi.map.o: can't find referenced method 'float sqrt(float)' in library class android.util.FloatMath

#自己的框架需要加入的-----------------------------------begin
-keep public class * extends android.content.** { *; }
-keep public class * extends android.os.** { *; }
-keep class first.test.com.bscenter.** { *; }
-dontwarn first.test.com.bscenter.**
-keep class first.test.com.bscenter.** { *;}
#------------------------------------------------------end
-keep class com.google.gson.**{*;}

-keep public class * extends java.lang.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn android.support.**
#防止R被混淆，R类反射混淆，找不到资源ID
-keep class **.R$* {
	*;
}
#所有native的方法不能去混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#某些构造方法不能混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
# 这个主要是在layout中写的onclick方法android:onclick="onClick"，不进行混淆
 -keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
 }

 #保持注解
 -keepattributes *Annotation*

#枚举类不能混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#aidl文件不能去混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#-----------JavascriptInterface防止被混淆后无效----------------begin
#-keep class com.lenovohit.hospitals.utils.WebInterface{*;}
-keepattributes *JavascriptInterface*
#--------------------------------------------------------------end
