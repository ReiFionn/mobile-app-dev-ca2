package ie.setu.mobileappdevelopmentca1.views.eventList

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ie.setu.mobileappdevelopmentca1.views.map.EventMapView
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
import ie.setu.mobileappdevelopmentca1.views.event.EventView

class EventListPresenter(val view: EventListView) {

    var app: MainApp = view.application as MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private var position: Int = 0

    init {
        registerMapCallback()
        registerRefreshCallback()
    }

    fun getEvents() = app.events.findAll()

    fun doAddEvent() {
        val launcherIntent = Intent(view, EventView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doEditEvent(event: EventModel, pos: Int) {
        val launcherIntent = Intent(view, EventView::class.java)
        launcherIntent.putExtra("event_edit", event)
        position = pos
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doShowEventsMap() {
        val launcherIntent = Intent(view, EventMapView::class.java)
        mapIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == RESULT_OK) {
                    view.onRefresh()
                }
            }
    }
    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {  }
    }

    fun doDeleteEvent(event: EventModel) {
        app.events.delete(event)
        view.onRefresh()
    }

    fun searchByTitle(query: String?): List<EventModel> {
        val allEvents = app.events.findAll()
        if (query.isNullOrBlank()) return allEvents
        return allEvents.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }
}