package com.google.arcore.kotlin.arcoredemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.exceptions.*
import com.google.ar.core.Config
import com.google.ar.core.Config.InstantPlacementMode
import com.google.ar.core.Session
import com.google.ar.core.examples.kotlin.helloar.ARCoreView
import com.google.arcore.java.common.helpers.CameraPermissionHelper
import com.google.arcore.kotlin.helpers.ARCoreSessionLifecycleHelper
import com.google.arcore.java.common.samplerender.SampleRender
import com.google.arcore.java.common.helpers.InstantPlacementSettings
import com.google.arcore.java.common.helpers.FullScreenHelper
import com.google.arcore.java.common.helpers.DepthSettings
import com.google.arcore.java.common.helpers.PlaneSettings


class ARCoreDemo: AppCompatActivity (){
    companion object{
        private const val TAG ="ARCoreDemo"
    }

    lateinit var ARCoreSessionHelper: ARCoreSessionLifecycleHelper
    lateinit var view: ARCoreView
    lateinit var renderer: ARCoreRenderer

    val instantPlacementSettings =  InstantPlacementSettings()
    val depthSettings = DepthSettings()
    val planeSettings = PlaneSettings()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        ARCoreSessionHelper = ARCoreSessionLifecycleHelper(this)
        ARCoreSessionHelper.exceptionCallback = {
            exception ->
                val message =
                    when (exception){
                        is UnavailableUserDeclinedInstallationException ->
                            "Please install Google Play Services for AR"
                        is UnavailableApkTooOldException ->
                            "Please update ARCore"
                        is UnavailableSdkTooOldException ->
                            "Please update this app"
                        is UnavailableDeviceNotCompatibleException ->
                            "Your device does not support AR"
                        is CameraNotAvailableException ->
                            "Camera not available. Try restarting the App"
                        else -> "Failed to create AR session: $exception"
                    }
                Log.e(TAG, "ARCore threw an exception", exception)
                view.snackbarHelper.showError(this, message)
        }
        /* Set up ARCore Session, which call in ARCore feature's, which are: Light
        Estimation, Depth mode and Instant Placement
        * */
        ARCoreSessionHelper.beforeSessionResume = ::configureSession
        lifecycle.addObserver(ARCoreSessionHelper)

        renderer = ARCoreRenderer(this)
        lifecycle.addObserver(renderer)

        view = ARCoreView(this)
        lifecycle.addObserver(view)
        setContentView(view.root)

        //Set up an example renderer using ARCoreRenderer.
        SampleRender(view.surfaceView, renderer, assets)

        depthSettings.onCreate(this)
        instantPlacementSettings.onCreate(this)
        planeSettings.onCreate(this)

    }

    fun configureSession(session: Session){
        session.configure(
            session.config.apply{
                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)){
                        Config.DepthMode.AUTOMATIC
                    } else {
                        Config.DepthMode.DISABLED
                    }
                instantPlacementMode =
                    if (instantPlacementSettings.isInstantPlacementEnabled){
                        InstantPlacementMode.LOCAL_Y_UP
                    }else{
                        InstantPlacementMode.DISABLED
                    }
                planeFindingMode =
                    if (planeSettings.isVerticalPlaneEnabled){
                        Config.PlaneFindingMode.VERTICAL
                    }else{
                        Config.PlaneFindingMode.HORIZONTAL
                    }
            }
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission(this)){
            Toast.makeText(this, "Camera permission is required for this app", Toast.LENGTH_LONG)
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)){
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }
}