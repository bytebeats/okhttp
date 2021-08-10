package me.bytebeats.okhttp.progress

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2021/8/9 21:44
 * @Version 1.0
 * @Description TO-DO
 */

interface ProgressListener {
    fun onProgress(currentLength: Long, totalLength: Long)
}