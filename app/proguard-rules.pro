-keepattributes Signature
-keepattributes *Annotation*
-keep class com.example.core.model.** { *; }
-keep class com.example.core.network.api.** { *; }

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    public *;
}

-keep class com.therouter.** { *; }
-keep class * implements com.therouter.router.Route { *; }

-keep class kotlin.Metadata { *; }

-keep class com.shuyu.gsyvideoplayer.** { *; }
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep class com.example.core.network.** { *; }

-keep class com.example.ept.dicover.** { *; }
-keep class com.example.ept.home.** { *; }
-keep class com.example.ept.daily.** { *; }
-keep class com.example.ept.category.** { *; }
-keep class com.example.ept.person.** { *; }
-keep class com.example.ept.search.** { *; }
-keep class com.example.ept.notify.** { *; }

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**
