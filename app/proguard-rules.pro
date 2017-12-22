# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Users\xlc\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn okio.**

-dontwarn sun.misc.**

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.google.gson.** { *; }

-keep class com.google.inject.** { *; }

-dontwarn android.os.SystemProperties

#不混淆glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**

-keep class org.jsoup.**

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-dontusemixedcaseclassnames

-dontwarn com.google.android.gms.ads.**

-keep class com.google.android.gms.ads.internal.**{*;}

-keep class com.marswin89.marsdaemon.NativeDaemonBase{*;}
-keep class com.marswin89.marsdaemon.nativ.NativeDaemonAPI20{*;}
-keep class com.marswin89.marsdaemon.nativ.NativeDaemonAPI21{*;}
-keep class com.marswin89.marsdaemon.DaemonApplication{*;}
-keep class com.marswin89.marsdaemon.DaemonClient{*;}
-keepattributes Exceptions,InnerClasses,...
-keep class com.marswin89.marsdaemon.DaemonConfigurations{*;}
-keep class com.marswin89.marsdaemon.DaemonConfigurations$*{*;}