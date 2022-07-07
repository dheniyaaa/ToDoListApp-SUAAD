package com.dicoding.todoapp.setting

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.*
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.NOTIFICATION_CONTENT
import java.util.concurrent.TimeUnit


class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var periodicNotifyRequest : PeriodicWorkRequest


        companion object{
            val TAG = "SettingsFragment"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            val workManager = context?.let { WorkManager.getInstance(it) }
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                val channelName = getString(R.string.notify_channel_name)
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName

                if (newValue == true){
                    val data = Data.Builder()
                        .putString(NOTIFICATION_CHANNEL_ID, channelName)
                        .putString(NOTIFICATION_CONTENT, getString(R.string.notify_content))
                        .build()

                    periodicNotifyRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                        1,
                        TimeUnit.DAYS
                    )
                        .setInputData(data)
                        .build()
                    workManager?.enqueue(periodicNotifyRequest)
                    workManager?.getWorkInfoByIdLiveData(periodicNotifyRequest.id)?.observe(viewLifecycleOwner){workInfo ->
                        val status = workInfo.state.name
                        Log.d(TAG, "workmanager status: $status")
                        if (workInfo.state == WorkInfo.State.ENQUEUED){
                            Log.d(TAG, "Reminder has been enququed")
                        }
                    }
                } else{
                    try {
                        workManager?.getWorkInfoByIdLiveData(periodicNotifyRequest.id)?.observe(viewLifecycleOwner){workInfo ->

                            val status = workInfo.state.name
                            Log.d(TAG, "workmanager status: $status")
                            if (workInfo.state == WorkInfo.State.ENQUEUED){
                                try {
                                    workManager.cancelWorkById(periodicNotifyRequest.id)
                                    Toast.makeText(requireContext(), "Task reminder has been cancelled", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception){
                                    e.printStackTrace()
                                }
                            }
                        }
                    } catch (e: Exception){
                        Log.d(TAG, "Reminder has been cancelled : ${e.message}")
                    }
                }
                true
            }

        }

        private fun modeTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true


        }


    }
}