package com.example.chuckersamplecrash

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import okhttp3.Protocol

class MainActivity : AppCompatActivity() {
    private var tvStatus : TextView? = null
    private var btnSendWithChucker : Button? = null
    private var btnSendWithoutChucker : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvStatus = findViewById(R.id.status)
        btnSendWithChucker = findViewById(R.id.btn_upload_with_chucker)
        btnSendWithoutChucker = findViewById(R.id.btn_upload_without_chucker)

        btnSendWithChucker?.setOnClickListener {
            uploadWithChucker(this)
        }
        btnSendWithoutChucker?.setOnClickListener {
            uploadWithoutChucker()
        }
    }

    private fun uploadWithoutChucker() {
        val client = HttpClient(OkHttp) {
            developmentMode = true

            engine {
                config {
                    protocols(listOf(Protocol.HTTP_1_1))
                }
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30 * 1000
                socketTimeoutMillis = requestTimeoutMillis
                connectTimeoutMillis = requestTimeoutMillis
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val resp = client.post<String>("https://httpbin.org/post") {
                    body = MultiPartFormDataContent(formData {
                        append("file", "123")
                    })
                }
                //println(resp)
                withContext(Dispatchers.Main){
                    tvStatus?.text = resp
                }
            } catch (exception: Exception) {
                withContext(Dispatchers.Main){
                    tvStatus?.text = "Error: ${exception.toString()}"
                }
            }
        }
    }

    private fun uploadWithChucker(context: Context) {
        val client = HttpClient(OkHttp) {
            developmentMode = true

            engine {
                config {
                    protocols(listOf(Protocol.HTTP_1_1))
                }
                addInterceptor(ChuckerInterceptor(context))
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30 * 1000
                socketTimeoutMillis = requestTimeoutMillis
                connectTimeoutMillis = requestTimeoutMillis
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val resp = client.post<String>("https://httpbin.org/post") {
                    body = MultiPartFormDataContent(formData {
                        append("file", "123")
                    })
                }
                //println(resp)
                withContext(Dispatchers.Main){
                    tvStatus?.text = resp
                }
            } catch (exception: Exception) {
                withContext(Dispatchers.Main){
                    tvStatus?.text = "Error: ${exception.toString()}"
                }
            }
        }
    }
}
