package me.bytebeats.okhttp.progress

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2021/8/9 21:43
 * @Version 1.0
 * @Description TO-DO
 */

interface DownloadListener {
    fun onStarted()
    fun onFinished()
}