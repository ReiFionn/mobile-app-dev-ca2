package ie.setu.mobileappdevelopmentca1.views.eventList

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ie.setu.mobileappdevelopmentca1.views.map.EventMapView
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
import ie.setu.mobileappdevelopmentca1.views.event.EventView
import com.google.firebase.firestore.ListenerRegistration

class EventListPresenter(val view: EventListView) {

    var app: MainApp = view.application as MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private var position: Int = 0
    private var listener: ListenerRegistration? = null

    init {
        app = view.application as MainApp
        registerMapCallback()
        registerRefreshCallback()
    }

    //https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/ListenerRegistration
    fun startListening() {
        listener = app.events.listenAll(
            onData = { pairs ->
                view.loadEvents(pairs)
            },
            onError = {
                view.showError(it.message ?: "Failed to load jobs")
            }
        )
    }

    fun stopListening() {
        listener?.remove()
    }

    fun doDeleteEvent(id: String) {
        app.events.delete(id, onDone = {
            view.setResult(99)
        }, onError = { view.showError(it.message ?: "Delete failed") })
    }

    fun doAddEvent() {
        val launcherIntent = Intent(view, EventView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

//    fun doEditEvent(event: EventModel, pos: Int) {
//        val launcherIntent = Intent(view, EventView::class.java)
//        launcherIntent.putExtra("event_edit", event)
//        position = pos
//        refreshIntentLauncher.launch(launcherIntent)
//    }

    fun doEditEvent(id: String, event: EventModel) {
        refreshIntentLauncher.launch(Intent(view, EventView::class.java).putExtra("event_id", id).putExtra("event_edit", event))
    }

    fun doShowEventsMap() {
        val launcherIntent = Intent(view, EventMapView::class.java)
        mapIntentLauncher.launch(launcherIntent)
    }

//    private fun registerRefreshCallback() {
//        refreshIntentLauncher =
//            view.registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult()
//            ) {
//                if (it.resultCode == RESULT_OK) {
//                    view.onRefresh()
//                }
//            }
//    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher = view.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {  }
    }

    fun handleSearch(query: String?) {
        val pairs = if (query.isNullOrBlank()) app.events.findAllPairs()
        else app.events.findByTitlePairs(query)
        view.loadEvents(pairs)
    }
}