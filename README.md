# okhttp
OkHttp stuff.

<br>OkHttp如何将自签名证书网站添加到受信任之列?
<br>OkHttp如何支持证书的双向验证?

<br>工程内给出了答案.

##如何监听下载进度?
```
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
```

##如何监听文件上传进度?
```
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
    
```