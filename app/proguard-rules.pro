# ProGuard / R8 Advanced Optimization Rules for Google Play Release Builds
# Maximum Optimization & Obfuscation Strategy

# =============================================================================
# 1. 高階 R8 最佳化與重新包裝設定
# =============================================================================
# 設定 R8 多輪最佳化次數 (預設為 5 次)
-optimizationpasses 5

# 允許 R8 修改類別/方法的存取修飾子 (public/private)，以利實現深層的方法內聯與變數移除
-allowaccessmodification

# 將混淆後的類別全部移至根 Package，大幅減少 DEX 中的字串池空間與大小
-repackageclasses ''

# 混淆時不使用大小寫混合的類別名稱
-dontusemixedcaseclassnames

# 詳細輸出混淆報告
-verbose

# 在 Release 構建中完全消除 android.util.Log 的 Debug 記錄呼叫與字串拼接
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}

# =============================================================================
# 2. 保留反射與 Annotations 屬性
# =============================================================================
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.invoke.**

# 保留有標註 @Keep 的類別與成員
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep <fields>;
    @androidx.annotation.Keep <methods>;
}

# =============================================================================
# 3. AndroidX Room 數據庫精準保留規則
# =============================================================================
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class *_Impl { *; }
-dontwarn androidx.room.paging.**

# =============================================================================
# 4. Moshi JSON 解析精準保留規則
# =============================================================================
-keep class * extends com.squareup.moshi.JsonAdapter
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-dontwarn com.squareup.moshi.**

# =============================================================================
# 5. Retrofit & OkHttp 網路層精準保留規則
# =============================================================================
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# =============================================================================
# 6. Jetpack Compose, ViewModels & Kotlin Serialization
# =============================================================================
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepattributes *kotlin.Metadata*

