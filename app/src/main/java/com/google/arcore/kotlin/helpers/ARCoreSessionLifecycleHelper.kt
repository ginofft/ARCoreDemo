package com.google.arcore.kotlin.helpers

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.arcore.java.common.helpers.CameraPermissionHelper
import com.google.ar.core.exceptions.CameraNotAvailableException

/**
 * Manages an ARCore Session using the Android Lifecycle API. Before starting a Session, this class
 * requests installation of Google Play Services for AR if it's not installed or not up to date and
 * asks the user for required permissions if necessary.
 */

class ARCoreSessionLifecycleHelper (
    val activity: Activity, //Android activity
    val features: Set<Session.Feature> = setOf() //set of used ARCore's features
): DefaultLifecycleObserver{
    var installRequested = false
    var session: Session? = null
    private set

    var exceptionCallback: ((Exception)->Unit)? = null
    var beforeSessionResume: ((Session)->Unit)? = null

    private fun tryCreateSession(): Session? {
        if (!CameraPermissionHelper.hasCameraPermission(activity)){
            CameraPermissionHelper.requestCameraPermission(activity)
            return null
        }
        return try{
            when(ArCoreApk.getInstance().requestInstall(activity, !installRequested)!!) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    installRequested = true
                    return null
                }
                ArCoreApk.InstallStatus.INSTALLED -> {
                }
            }
            Session(activity, features)
        } catch (e:Exception){
            exceptionCallback?.invoke(e)
            null
        }
    }

    override fun onResume(owner: LifecycleOwner){
        val session = this.session ?: tryCreateSession() ?:return
        try {
            beforeSessionResume?.invoke(session)
            session.resume()
            this.session = session
        } catch (e: CameraNotAvailableException){
            exceptionCallback?.invoke(e)
        }
    }

    override fun onPause(owner: LifecycleOwner){
        session?.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        session?.close()
        session = null
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        results: IntArray
    )
    {
        if (!CameraPermissionHelper.hasCameraPermission(activity)){
            Toast.makeText(
                activity,
                "Camera permission is needed to run ARCore",
                Toast.LENGTH_LONG
            ).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(activity)){
                CameraPermissionHelper.launchPermissionSettings(activity)
            }
            activity.finish()
        }
    }
}