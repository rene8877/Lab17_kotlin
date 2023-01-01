package com.example.lab17_k

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    class Data {
        lateinit var result: Result

        class Result {
            lateinit var results: Array<Results>

            class Results {
                var Station = ""
                var Destination = ""
            }
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_query).setOnClickListener { v: View? ->
            val URL = "https://lab12-api.web.app/"
            val request: Request = Request.Builder().url(URL).build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("查詢失敗", e.message!!)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.code === 200) {
                        if (response.body == null) return
                        val data: com.example.lab17_k.MainActivity.Data = Gson().fromJson(
                            response.body!!.string(),
                            com.example.lab17_k.MainActivity.Data::class.java
                        )
                        val items =
                            arrayOfNulls<String>(data.result.results.size)
                        for (i in items.indices) {
                            items[i] =
                                """
                                     
                                     列車即將進入:${data.result.results.get(i).Station}
                                     列車行駛目的地:
                                     """.trimIndent() + data.result.results.get(
                                    i
                                ).Destination
                        }
                        runOnUiThread {
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("台北捷運列車到站站名")
                                .setItems(items, null)
                                .show()
                        }
                    } else if (!response.isSuccessful) Log.e(
                        "伺服器錯誤",
                        response.code.toString() + " " + response.message
                    ) else Log.e("其他錯誤", response.code.toString() + " " + response.message)
                }
            })
        }
    }
}