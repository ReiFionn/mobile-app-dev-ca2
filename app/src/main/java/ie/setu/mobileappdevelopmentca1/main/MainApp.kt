package ie.setu.mobileappdevelopmentca1.main

import android.app.Application
import ie.setu.mobileappdevelopmentca1.models.EventFirebaseMemStore
import ie.setu.mobileappdevelopmentca1.models.EventJSONStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {
//    lateinit var events: EventJSONStore
    lateinit var events: EventFirebaseMemStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
//        events = EventJSONStore(this)
        events = EventFirebaseMemStore()
        i("Events started")
    }
}