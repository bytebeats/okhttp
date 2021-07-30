package me.bytebeats.okhttp

import okhttp3.OkHttpClient
import okhttp3.internal.closeQuietly
import okhttp3.tls.HandshakeCertificates
import okio.Buffer
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object OkHttpUtil {
    /**
     * To add multi self signing certificates, e.g.: 12306
     */
    fun supportMultiSelfSignedCertificates(builder: OkHttpClient.Builder, certISes: Collection<InputStream>) {
        try {
            val handShakeBuilder = HandshakeCertificates.Builder()
            val certificateFactory = CertificateFactory.getInstance("X.509")
            for (stream in certISes) {
                val cert = certificateFactory.generateCertificates(stream).single() as X509Certificate
                handShakeBuilder.addTrustedCertificate(cert)
                stream.close()
            }
            val certificates = handShakeBuilder.build()
            builder.sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } finally {
            certISes.forEach { it.closeQuietly() }
        }
    }

    /**
     * To add multi self signing certificates in String, e.g.: 12306
     */
    fun supportMultiSelfSignedCertificates(builder: OkHttpClient.Builder, vararg certs: Collection<String>) {
        supportMultiSelfSignedCertificates(builder, certs.map { Buffer().writeUtf8(it.toString()).inputStream() })
    }

    /**
     * To add single self signing certificates, e.g.: 12306
     */
    fun supportSelfSignedCertificates(builder: OkHttpClient.Builder, cert: InputStream) {
        supportMultiSelfSignedCertificates(builder, listOf(cert))
    }

    /**
     * To add single self signing certificates in String, e.g.: 12306
     */
    fun supportSelfSignedCertificates(builder: OkHttpClient.Builder, cert: String) {
        supportMultiSelfSignedCertificates(builder, listOf(Buffer().writeUtf8(cert).inputStream()))
    }


}