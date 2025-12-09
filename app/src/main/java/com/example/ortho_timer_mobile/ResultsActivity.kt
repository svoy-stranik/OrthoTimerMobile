package com.example.ortho_timer_mobile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultsActivity : AppCompatActivity() {

    private lateinit var totalTimeWorkedText: TextView
    private lateinit var shortBreaksCountText: TextView
    private lateinit var lunchesCountText: TextView
    private lateinit var telegramLinkText: TextView
    private lateinit var copyBtn: Button
    private lateinit var openBtn: Button
    private lateinit var startNewDayBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        totalTimeWorkedText = findViewById(R.id.totalTimeWorkedText)
        shortBreaksCountText = findViewById(R.id.shortBreaksCountText)
        lunchesCountText = findViewById(R.id.lunchesCountText)
        telegramLinkText = findViewById(R.id.telegramLinkText)
        copyBtn = findViewById(R.id.copyBtn)
        openBtn = findViewById(R.id.openBtn)
        startNewDayBtn = findViewById(R.id.startNewDayBtn)

        // Получаем данные из Intent
        val totalTimeWorked = intent.getLongExtra("total_time_worked", 0L)
        val shortBreaksCount = intent.getIntExtra("short_breaks_count", 0)
        val lunchesCount = intent.getIntExtra("lunches_count", 0)

        // Вычисляем часы и минуты
        val hours = totalTimeWorked / (60 * 60 * 1000)
        val minutes = (totalTimeWorked % (60 * 60 * 1000)) / (60 * 1000)

        // Обновляем UI
        totalTimeWorkedText.text = "Работал: $hours ч $minutes мин"
        shortBreaksCountText.text = "Коротких перерывов: $shortBreaksCount"
        lunchesCountText.text = "Обедов: $lunchesCount"

        telegramLinkText.text = "Telegram: https://t.me/periplanomenoc"

        copyBtn.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Telegram Link", "https://t.me/periplanomenoc")
            clipboard.setPrimaryClip(clip)
        }

        openBtn.setOnClickListener {
            val uri = Uri.parse("https://t.me/periplanomenoc")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        startNewDayBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}