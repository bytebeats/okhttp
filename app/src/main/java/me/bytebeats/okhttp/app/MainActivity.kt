package me.bytebeats.okhttp.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import me.bytebeats.okhttp.HttpsUtils
import me.bytebeats.okhttp.OkHttpUtil
import me.bytebeats.okhttp.progress.DownloadInterceptor
import me.bytebeats.okhttp.progress.DownloadListener
import me.bytebeats.okhttp.progress.ProgressListener
import me.bytebeats.okhttp.progress.UploadListener
import me.bytebeats.okhttp.progress.UploadRequestBody.Companion.multipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.tv).setOnClickListener {
            thread {
                val builder = OkHttpClient.Builder()
                // 12306 不再使用自签名证书, 而是使用了Digicert
//                OkHttpUtil.supportSelfSignedCertificates(builder, applicationContext.assets.open("srca.cer"))
//                HttpsUtils.initSslSocketFactory(application)
//                HttpsUtils.sSLSocketFactory?.let { sslSocketFactory ->
//                    HttpsUtils.trustManager?.let { trustManager ->
//                        builder.sslSocketFactory(
//                            sslSocketFactory,
//                            trustManager
//                        )
//                    }
//                }
                val client = builder.build()
                val request = Request.Builder().url("https://kyfw.12306.cn/otn/").build()
                val response = client.newCall(request).execute()
                Log.e(TAG, response.body?.string() ?: "null body")
            }
        }
    }

    private fun download() {
        val downloadClient = OkHttpClient.Builder().addInterceptor(DownloadInterceptor(object : DownloadListener {
            override fun onStarted() {
                TODO("Not yet implemented")
            }

            override fun onFinished() {
                TODO("Not yet implemented")
            }
        }, object : ProgressListener {
            override fun onProgress(currentLength: Long, totalLength: Long) {

            }
        }))
        //continue...
    }

    private fun upload() {
        val file = File(this.filesDir, "xxx.zip")
        val uploadRequestBody = multipartBody(file, object : UploadListener {
            override fun onStarted() {
                TODO("Not yet implemented")
            }

            override fun onFinished() {
                TODO("Not yet implemented")
            }
        }, object : ProgressListener {
            override fun onProgress(currentLength: Long, totalLength: Long) {
                TODO("Not yet implemented")
            }
        })
        Request.Builder().url("your url")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}