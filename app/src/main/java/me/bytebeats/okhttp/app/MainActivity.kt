package me.bytebeats.okhttp.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import me.bytebeats.okhttp.HttpsUtils
import me.bytebeats.okhttp.OkHttpUtil
import okhttp3.OkHttpClient
import okhttp3.Request
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

    companion object {
        private const val TAG = "MainActivity"
    }
}