package ie.setu.mobileappdevelopmentca1.views.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.databinding.ActivityEventMapsBinding
import ie.setu.mobileappdevelopmentca1.databinding.ContentEventMapsBinding
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
import ie.setu.mobileappdevelopmentca1.views.auth.AuthView
import ie.setu.mobileappdevelopmentca1.views.event.EventView
import ie.setu.mobileappdevelopmentca1.views.eventList.EventListView

class EventMapView : AppCompatActivity() , GoogleMap.OnMarkerClickListener{

    private lateinit var binding: ActivityEventMapsBinding
    private lateinit var contentBinding: ContentEventMapsBinding
    lateinit var app: MainApp
    lateinit var presenter: EventMapPresenter
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp
        binding = ActivityEventMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.nav_create_event -> {
                    startActivity(Intent(this, EventView::class.java))
                }
                R.id.nav_view_events -> {
                    startActivity(Intent(this, EventListView::class.java))
                }
                R.id.nav_view_events_map -> {
                    startActivity(Intent(this, EventMapView::class.java))
                }
                R.id.nav_sign_out -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, AuthView::class.java))
                }
                R.id.nav_dark_mode -> {
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        presenter = EventMapPresenter(this)

        contentBinding = ContentEventMapsBinding.bind(binding.root.findViewById(R.id.coordinatorLayout))

        contentBinding.mapView.onCreate(savedInstanceState)
        contentBinding.mapView.getMapAsync{ googleMap ->
            map = googleMap
            presenter.doPopulateMap(map)
            enableMyLocation()
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                map.isMyLocationEnabled = true
                getSystemService(LOCATION_SERVICE) as LocationManager
            }
        }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    fun showEvent(event: EventModel) {
        contentBinding.currentTitle.text = event.title
        contentBinding.currentDescription.text = event.description
        Picasso.get()
            .load(event.image.toUri())
            .into(contentBinding.currentImage)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doMarkerSelected(marker)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }
}