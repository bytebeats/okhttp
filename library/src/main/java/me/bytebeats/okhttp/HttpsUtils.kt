package me.bytebeats.okhttp

import android.app.Application
import java.security.KeyStore
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.lang.Exception
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.Extension
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * @Author bytebeats
 * @Email <happychinapc></happychinapc>@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2021/7/30 21:12
 * @Version 1.0
 * @Description Https 双向证书认证
 */
/**
 * 提供了多种初始化方法看自己情况
 * 双向验证initSslSocketFactory
 * 单向验证bks证书initSslSocketFactorySingle 至于bks证书的生成方式自行百度很多
 * 单向验证crt证书initSslSocketFactorySingleBuyCrt
 */
object HttpsUtils {
    private const val KEY_STORE_TYPE_BKS = "bks"
    private const val KEY_STORE_TYPE_P12 = "PKCS12"
    const val KEY_STORE_PASSWORD = "123456" //P12文件密码
    const val BKS_STORE_PASSWORD = "123456" //BKS文件密码
    var sSLSocketFactory: SSLSocketFactory? = null
    var trustManager: X509TrustManager? = null

    /**
     * 双向校验中SSLSocketFactory X509TrustManager 参数的生成
     *
     * @param application
     */
    fun initSslSocketFactory(application: Application) {
        try {
            val bksStream = application.assets.open("xxxx.bks") //客户端信任的服务器端证书流
            val p12Stream = application.assets.open("xxxx.p12") //服务器需要验证的客户端证书流

            // 客户端信任的服务器端证书
            val trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS)
            // 服务器端需要验证的客户端证书
            val keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12)
            try {
                trustStore.load(bksStream, BKS_STORE_PASSWORD.toCharArray()) //加载客户端信任的服务器证书
                keyStore.load(p12Stream, KEY_STORE_PASSWORD.toCharArray()) //加载服务器信任的客户端证书
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } finally {
                try {
                    bksStream.close()
                    p12Stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val sslContext = SSLContext.getInstance("TLS")
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(trustStore)
            trustManager = chooseTrustManager(trustManagerFactory.trustManagers) //生成用来校验服务器真实性的trustManager
            val keyManagerFactory = KeyManagerFactory.getInstance("X509")
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray()) //生成服务器用来校验客户端真实性的KeyManager
            //初始化SSLContext
            sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
            sSLSocketFactory = sslContext.socketFactory //通过sslContext获取到SocketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 单向校验中SSLSocketFactory X509TrustManager 参数的生成
     * 通常单向校验一般都是服务器不校验客户端的真实性，客户端去校验服务器的真实性
     *
     * @param application
     */
    fun initSslSocketFactorySingle(application: Application) {
        try {
            val bksStream = application.assets.open("xxxx.bks") //客户端信任的服务器端证书流

            // 客户端信任的服务器端证书
            val trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS)
            try {
                trustStore.load(bksStream, BKS_STORE_PASSWORD.toCharArray()) //加载客户端信任的服务器证书
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } finally {
                try {
                    bksStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(trustStore)
            trustManager = chooseTrustManager(trustManagerFactory.trustManagers) //生成用来校验服务器真实性的trustManager
            val sslContext = SSLContext.getInstance("TLSv1", "AndroidOpenSSL")
            sslContext.init(null, trustManagerFactory.trustManagers, null)
            //初始化SSLContext
            sSLSocketFactory = sslContext.socketFactory //通过sslContext获取到SocketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 单向校验中,通过crt格式的证书生成SSLSocketFactory X509TrustManager 参数的生成
     * 通常在Android中，客户端用于校验服务器真实性的证书是支持BKS格式的，但是往往后台给的证书都是crt格式的
     * 当然我们可以自己生成BKS，但是想更方便一些我们也是可以直接使用crt格式的证书的
     *
     * @param application
     */
    fun initSslSocketFactorySingleBuyCrt(application: Application) {
        try {
            val crtStream = application.assets.open("xxxx.crt") //客户端信任的服务器端证书流
            val cf = CertificateFactory.getInstance("X.509")
            val ca = cf.generateCertificate(crtStream)
            val keyStoreType = KeyStore.getDefaultType()
            val trustStore = KeyStore.getInstance(keyStoreType)
            try {
                trustStore.load(null, null)
                trustStore.setCertificateEntry("ca", ca)
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } finally {
                try {
                    crtStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(trustStore)
            trustManager = chooseTrustManager(trustManagerFactory.trustManagers) //生成用来校验服务器真实性的trustManager
            val sslContext = SSLContext.getInstance("TLSv1", "AndroidOpenSSL")
            sslContext.init(null, trustManagerFactory.trustManagers, null)
            //初始化SSLContext
            sSLSocketFactory = sslContext.socketFactory //通过sslContext获取到SocketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
        for (trustManager in trustManagers) {
            if (trustManager is X509TrustManager) {
                return trustManager
            }
        }
        return null
    }

    /**
     * 当服务器不下发中间证书, 但中间需要认证时, 需要客户端自行下载中间证书并进行认证
     * 需要 bouncecastle 依赖才可以进行
     */
//    fun downloadAndCheckMiddleCA(chain: Array<X509Certificate>, authType: String): Boolean {
//        val extValue = chain[0].getExtensionValue(Extension.authorityIfoAccess.getId())
//        val aia = AuthorityInformationAccess.getInstance(X509ExtensionUti.)
//    }
}