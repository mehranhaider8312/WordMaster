package com.codewithmehran.wordmaster.utils

import android.content.Context
import android.util.Log
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

object MixpanelHelper {
    private const val TAG = "MixpanelHelper"
    private const val MIXPANEL_TOKEN = "8ae4ab4a61f9db21443dbfdbd523dbc0"

    private var mixpanel: MixpanelAPI? = null

    fun init(context: Context) {
        if (mixpanel == null) {
            mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN, true)
            Log.d(TAG, "Mixpanel initialized")
        }
    }

    fun trackEvent(eventName: String, properties: Map<String, Any>? = null) {
        val mp = mixpanel ?: return
        try {
            if (properties != null) {
                val props = JSONObject()
                for ((key, value) in properties) {
                    props.put(key, value)
                }
                mp.track(eventName, props)
            } else {
                mp.track(eventName)
            }
            Log.d(TAG, "Tracked event: $eventName, properties: $properties")
        } catch (e: Exception) {
            Log.e(TAG, "Error tracking event: $eventName", e)
        }
    }

    fun identify(userId: String) {
        mixpanel?.identify(userId)
        mixpanel?.people?.identify(userId)
    }

    // Dedicated Tracking Methods
    fun trackWordAdded(word: String, source: String) {
        trackEvent("Word Added", mapOf("Word" to word, "Source" to source))
    }

    fun trackWordDeleted(word: String) {
        trackEvent("Word Deleted", mapOf("Word" to word))
    }

    fun trackWordEdited(word: String) {
        trackEvent("Word Edited", mapOf("Word" to word))
    }

    fun trackQuizTaken(question: String, isCorrect: Boolean) {
        trackEvent("Quiz Taken", mapOf("Question" to question, "Is Correct" to isCorrect))
    }

    fun trackStreakUpdated(currentStreak: Int) {
        trackEvent("Streak Updated", mapOf("Current Streak" to currentStreak))
    }
}
