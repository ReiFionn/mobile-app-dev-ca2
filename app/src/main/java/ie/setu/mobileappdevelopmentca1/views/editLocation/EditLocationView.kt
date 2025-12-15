package ie.setu.mobileappdevelopmentca1.views.editLocation

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.models.Location

class EditLocationView : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    lateinit var presenter: EditLocationPresenter
    var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        presenter = EditLocationPresenter(this)
        //location = intent.extras?.getParcelable("location",Location::class.java)!!
        location = intent.extras?.getParcelable<Location>("location")!!
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        onBackPressedDispatcher.addCallback(this ) {
            presenter.doOnBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        presenter.initMap(map)
    }

    override fun onMarkerDragStart(marker: Marker) {
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.doUpdateLocation(marker.position.latitude,
            marker.position.longitude,
            map.cameraPosition.zoom)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doUpdateMarker(marker)
        return false
    }
}