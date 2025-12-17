package ie.setu.mobileappdevelopmentca1.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.mobileappdevelopmentca1.main.MainApp

class EventMapPresenter(val view: EventMapView) {
    var app: MainApp = view.application as MainApp

    fun doPopulateMap(map: GoogleMap) {
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(view)
        app.events.findAllPairs().forEach { (id, event) ->
            val loc = LatLng(event.lat, event.lng)
            val options = MarkerOptions().title(event.title).position(loc)
            map.addMarker(options)?.tag = id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, event.zoom))
        }
    }

    fun doMarkerSelected(marker: Marker) {
        val tag = marker.tag as? String ?: return
        val event = app.events.findById(tag)
        if (event != null) view.showEvent(event)
    }
}