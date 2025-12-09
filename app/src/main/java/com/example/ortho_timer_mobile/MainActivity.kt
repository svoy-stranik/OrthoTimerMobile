package com.example.ortho_timer_mobile

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var statusText: TextView
    private lateinit var startPauseBtn: Button
    private lateinit var lunchBtn: Button
    private lateinit var finishDayBtn: Button
    private lateinit var todoBtn: Button
    private lateinit var quoteText: TextView

    private var isRunning = false
    private var isWork = true
    private var timeLeft = 25 * 60 * 1000L
    private var timer: CountDownTimer? = null
    private var wasPausedForLunch = false
    private var isDayFinished = false

    private var totalWorkedTime = 0L
    private var lunchesCount = 0
    private var shortBreaksCount = 0

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_START_PAUSE" -> {
                    if (!isRunning && !isDayFinished) {
                        startTimer()
                    } else if (isRunning) {
                        pauseTimer()
                    }
                    updateNotification()
                }
                "ACTION_LUNCH" -> {
                    if (!isDayFinished) {
                        pauseTimer()
                        statusText.text = "Обед"
                        wasPausedForLunch = true
                        lunchesCount++
                        updateNotification()
                    }
                }
                "ACTION_FINISH_DAY" -> {
                    finishDay()
                }
            }
        }
    }

    // ✅ Код для запроса разрешения
    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Notification", "Разрешение на уведомления получено")
        } else {
            Log.d("Notification", "Разрешение на уведомления отклонено")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()

        NotificationHelper.createNotificationChannel(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val filter = IntentFilter().apply {
            addAction("ACTION_START_PAUSE")
            addAction("ACTION_LUNCH")
            addAction("ACTION_FINISH_DAY")
        }
        registerReceiver(notificationReceiver, filter, Context.RECEIVER_NOT_EXPORTED)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("first_run", true)
        quoteText.text = QuoteHelper.getQuote(this, isFirstRun)

        if (isFirstRun) {
            prefs.edit().putBoolean("first_run", false).apply()
        }

        updateTimerText()
        updateNotification()
    }

    private fun initViews() {
        timerText = findViewById(R.id.timerText)
        statusText = findViewById(R.id.statusText)
        startPauseBtn = findViewById(R.id.startPauseBtn)
        lunchBtn = findViewById(R.id.lunchBtn)
        finishDayBtn = findViewById(R.id.finishDayBtn)
        todoBtn = findViewById(R.id.todoBtn)
        quoteText = findViewById(R.id.quoteText)
    }

    private fun setupClickListeners() {
        startPauseBtn.setOnClickListener {
            if (!isRunning && !isDayFinished) {
                startTimer()
            } else if (isRunning) {
                pauseTimer()
            }
            updateNotification()
        }

        lunchBtn.setOnClickListener {
            if (!isDayFinished) {
                pauseTimer()
                statusText.text = "Обед"
                wasPausedForLunch = true
                lunchesCount++
                updateNotification()
            }
        }

        finishDayBtn.setOnClickListener {
            finishDay()
        }

        todoBtn.setOnClickListener {
            startActivity(Intent(this, TodoActivity::class.java))
        }
    }

    private fun startTimer() {
        if (wasPausedForLunch) {
            timeLeft = if (isWork) 25 * 60 * 1000L else 5 * 60 * 1000L
            wasPausedForLunch = false
        }

        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                toggleState()
                startTimer()
                updateNotification()
            }
        }
        timer?.start()
        isRunning = true
        startPauseBtn.text = "Пауза"

        if (wasPausedForLunch) {
            statusText.text = if (isWork) "Работа" else "Отдых"
        }

        if (isWork) {
            statusText.text = "Работа"
        } else {
            statusText.text = "Отдых"
        }
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        startPauseBtn.text = "Старт"
    }

    private fun toggleState() {
        isWork = !isWork
        timeLeft = if (isWork) 25 * 60 * 1000L else 5 * 60 * 1000L
        statusText.text = if (isWork) "Работа" else "Отдых"
    }

    private fun updateTimerText() {
        val minutes = (timeLeft / 1000).toInt() / 60
        val seconds = (timeLeft / 1000).toInt() % 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateNotification() {
        val title = if (isWork) "Работа" else "Отдых"
        val text = if (isWork) "" else "Молиться не забывай, солнышко 🌞"
        NotificationHelper.showNotification(
            this,
            title,
            text,
            isRunning,
            isDayFinished
        )
    }

    private fun finishDay() {
        pauseTimer()
        isDayFinished = true
        statusText.text = "День завершён"
        startPauseBtn.isEnabled = false
        lunchBtn.isEnabled = false
        finishDayBtn.isEnabled = false

        NotificationHelper.cancelNotification(this)

        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra("total_time_worked", totalWorkedTime)
            putExtra("short_breaks_count", shortBreaksCount)
            putExtra("lunches_count", lunchesCount)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (isDayFinished) {
            resetDay()
            updateNotification()
        }
    }

    private fun resetDay() {
        isDayFinished = false
        isRunning = false
        isWork = true
        timeLeft = 25 * 60 * 1000L
        wasPausedForLunch = false
        totalWorkedTime = 0
        lunchesCount = 0
        shortBreaksCount = 0

        timer?.cancel()
        timer = null

        statusText.text = "Работа"
        startPauseBtn.text = "Старт"
        startPauseBtn.isEnabled = true
        lunchBtn.isEnabled = true
        finishDayBtn.isEnabled = true

        updateTimerText()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }
}