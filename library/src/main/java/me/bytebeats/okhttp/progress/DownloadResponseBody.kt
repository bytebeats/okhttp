package me.bytebeats.okhttp.progress

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2021/8/10 10:20
 * @Version 1.0
 * @Description TO-DO
 */

class DownloadResponseBody(
    private val responseBody: ResponseBody,
    private val downloadListener: DownloadListener? = null,
    private val progressListener: ProgressListener? = null
) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = wrapSource(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun wrapSource(source: Source): Source {
        return object : ForwardingSource(source) {
            private var readTotalBytes = -1L
            override fun read(sink: Buffer, byteCount: Long): Long {
                val readBytes = super.read(sink, byteCount)
                if (readTotalBytes == -1L) {
                    readTotalBytes = 0L
                    downloadListener?.onStarted()
                }
                readTotalBytes += if (readBytes != -1L) readBytes else 0
                progressListener?.onProgress(readTotalBytes, contentLength())
                if (readTotalBytes == contentLength()) {
                    downloadListener?.onFinished()
                }
                return readBytes
            }
        }
    }
}