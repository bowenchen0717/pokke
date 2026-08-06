package com.example.update

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Clean Architecture Modularized Repository for Google Play In-App Updates.
 * Handles Play Store version checking, update availability, and fallback store intents.
 */
class AppUpdateRepository(private val context: Context) {

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(context)
    }

    fun getInstalledVersionName(): String {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            "v" + (info.versionName ?: "1.0.0")
        } catch (e: Exception) {
            "v1.0.0"
        }
    }

    fun getInstalledVersionCode(): Long {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                info.versionCode.toLong()
            }
        } catch (e: Exception) {
            1L
        }
    }

    fun getPackageName(): String = context.packageName

    suspend fun checkGooglePlayUpdate(): Pair<UpdateStatus, String?> = suspendCancellableCoroutine { continuation ->
        try {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                val availability = appUpdateInfo.updateAvailability()
                if (availability == UpdateAvailability.UPDATE_AVAILABLE) {
                    val newVerCode = appUpdateInfo.availableVersionCode()
                    continuation.resume(Pair(UpdateStatus.UPDATE_AVAILABLE, "v1.$newVerCode.0"))
                } else if (availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    continuation.resume(Pair(UpdateStatus.DOWNLOADING, null))
                } else {
                    continuation.resume(Pair(UpdateStatus.UP_TO_DATE, getInstalledVersionName()))
                }
            }.addOnFailureListener {
                // If running in non-Play Store environment (dev container / debug APK)
                continuation.resume(Pair(UpdateStatus.DEV_ENVIRONMENT_CHECKED, getInstalledVersionName()))
            }
        } catch (e: Exception) {
            continuation.resume(Pair(UpdateStatus.DEV_ENVIRONMENT_CHECKED, getInstalledVersionName()))
        }
    }

    fun openPlayStorePage(ctx: Context = context) {
        val pkgName = ctx.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkgName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$pkgName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(intent)
        }
    }
}
