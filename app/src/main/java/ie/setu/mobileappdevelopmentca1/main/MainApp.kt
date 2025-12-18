package ie.setu.mobileappdevelopmentca1.main

import android.app.Application
import ie.setu.mobileappdevelopmentca1.models.EventFirebaseMemStore
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

//ALL USE OF CHATGPT: https://chatgpt.com/share/69433dcd-cf84-8003-b2ec-5cc4f196a0a1