package me.bytebeats.okhttp.progress

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.*
import java.io.File

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2021/8/10 10:38
 * @Version 1.0
 * @Description TO-DO
 */

class UploadRequestBody(
    private val requestBody: RequestBody,
    private val uploadListener: UploadListener? = null,
    private val progressListener: ProgressListener? = null
) : RequestBody() {
    override fun contentLength(): Long = requestBody.contentLength()

    override fun contentType(): MediaType? = requestBody.contentType()

    override fun writeTo(sink: BufferedSink) {
        val wrapSink = object : ForwardingSink(sink) {
            private var writtenTotalBytes = -1L
            override fun write(source: Buffer, byteCount: Long) {
                if (writtenTotalBytes == -1L) {
                    writtenTotalBytes = 0L
                    uploadListener?.onStarted()
                }
                writtenTotalBytes += byteCount
                progressListener?.onProgress(writtenTotalBytes, contentLength())
                if (writtenTotalBytes == contentLength()) {
                    uploadListener?.onFinished()
                }
                super.write(source, byteCount)
            }
        }
        val bufferSink = wrapSink.buffer()
        requestBody.writeTo(bufferSink)
        bufferSink.flush()
    }

    companion object {
        fun multipartBody(
            file: File,
            uploadListener: UploadListener? = null,
            progressListener: ProgressListener? = null
        ): UploadRequestBody {
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("platform", "android")
//                .addFormDataPart("file", file.name, RequestBody.create(MediaType.parse(MediaType.Companion.get()), file))//how to make sure mediatype of file to upload?
                .build()
            return UploadRequestBody(builder, uploadListener, progressListener)
        }
    }
}