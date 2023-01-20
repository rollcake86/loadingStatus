package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.util.DownloadUtils
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import timber.log.Timber

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val downloadUtils = intent.getSerializableExtra("download") as DownloadUtils
        when(downloadUtils){
            DownloadUtils.Glide -> {
                file_name.text = getString(R.string.radio1)
                file_status.text = getString(R.string.success)
                file_status.setTextColor(Color.BLUE)
            }
            DownloadUtils.LoadApp -> {
                file_name.text = getString(R.string.radio2)
                file_status.text = getString(R.string.fail)
                file_status.setTextColor(Color.RED)
            }
            DownloadUtils.Retrofit -> {
                file_name.text = getString(R.string.radio3)
                file_status.text = getString(R.string.success)
                file_status.setTextColor(Color.BLUE)
            }
        }


        button.setOnClickListener {
            finish()
        }

    }

}
