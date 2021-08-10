package me.bytebeats.okhttp.progress

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2021/8/10 10:32
 * @Version 1.0
 * @Description TO-DO
 */

class DownloadInterceptor(
    private val downloadListener: DownloadListener? = null,
    private val progressListener: ProgressListener? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val body = response.body ?: return response
        return response.newBuilder().body(DownloadResponseBody(body, downloadListener, progressListener))
            .build()
    }
}