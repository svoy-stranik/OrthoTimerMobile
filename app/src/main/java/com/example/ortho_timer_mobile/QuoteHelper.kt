package com.example.ortho_timer_mobile

import android.content.Context
import kotlin.random.Random

object QuoteHelper {
    fun getQuote(context: Context, isFirstRun: Boolean): String {
        return if (isFirstRun) {
            context.getString(R.string.otche_nash)
        } else {
            getRandomQuote(context)
        }
    }

    fun getRandomQuote(context: Context): String {
        val quotes = context.resources.getStringArray(R.array.bible_verses)
        return if (quotes.isNotEmpty()) {
            quotes[Random.nextInt(quotes.size)]
        } else "Цитата отсутствует"
    }
}