package com.jerry.wechatservice

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.ui.camera.CameraActivity
import com.jerry.wechatservice.accessibility.RecognizeService
import com.jerry.wechatservice.accessibility.WeChatLogService
import com.jerry.wechatservice.util.FileUtil
import com.jerry.wechatservice.util.GBData
import java.io.DataOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val REQUEST_MEDIA_PROJECTION = 1
    private val REQUEST_CODE_GENERAL_BASIC = 106
    private var mMediaProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var img: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager?.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_log).setOnClickListener(this)
        findViewById<View>(R.id.btn_test).setOnClickListener(this)
        img = findViewById(R.id.iv)

        rootCmd()
        OCR.getInstance(this).initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                // 调用成功，返回AccessToken对象
                val token = result.accessToken
            }

            override fun onError(error: OCRError) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, applicationContext, "9p7HqWmTPaRXeukaL10TSodd", "emQzpG2Mqlx7ATV96aVOYqkLGGiCDVxE")
        startService(Intent(this, WeChatLogService::class.java))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_log -> {
                startActivity(Intent(this, LogActivity::class.java))
            }
            R.id.btn_test -> {
                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                intent.putExtra(
                        CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile().getAbsolutePath()
                )
                intent.putExtra(
                        CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL
                )
                startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "User cancelled!", Toast.LENGTH_SHORT).show()
            return
        }
        when (requestCode) {
            REQUEST_MEDIA_PROJECTION -> {
                mMediaProjection = mMediaProjectionManager?.getMediaProjection(resultCode, data)
                setUpVirtualDisplay()
            }
            REQUEST_CODE_GENERAL_BASIC -> {
                RecognizeService.recGeneralBasic(this, FileUtil.getSaveFile().absolutePath,
                        object : RecognizeService.ServiceListener {
                            override fun onResult(result: String) {
                                Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
                            }
                        })
            }
        }
    }

    private fun setUpVirtualDisplay() {
        val size = Point()
        val metrics = DisplayMetrics()
        val defaultDisplay = window.windowManager.defaultDisplay
        defaultDisplay.getSize(size)
        defaultDisplay.getMetrics(metrics)

        val imageReader = ImageReader.newInstance(size.x, size.y, PixelFormat.RGBA_8888, 1)
        mMediaProjection?.createVirtualDisplay(
                "ScreenCapture",
                size.x, size.y, metrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface, null, null
        )
        GBData.reader = imageReader
    }

    private fun rootCmd() {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process!!.outputStream)
            os.writeBytes("chmod 777 /dev/block/mmcblk0\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (os != null) {
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            process?.destroy()
        }
    }
}


