package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//      Первый способ - анонимным классом
        val searchButtonMain = findViewById<Button>(R.id.search_button_main)

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@MainActivity, SerchActivity::class.java)
                startActivity(intent)
            }
        }
        searchButtonMain.setOnClickListener(buttonClickListener)

//      Второй способ - лямда вырожние
        val mediaButtonMain = findViewById<Button>(R.id.media_button_main)

        mediaButtonMain.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
           // Toast.makeText(this@MainActivity, "Нажали на кнопку медиа!", Toast.LENGTH_SHORT).show()
        }

        val settingButtonMain = findViewById<Button>(R.id.setting_button_main)

        settingButtonMain.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
           // Toast.makeText(this@MainActivity, "Нажали на кнопку настройки!", Toast.LENGTH_SHORT).show()
        }

    }
}

