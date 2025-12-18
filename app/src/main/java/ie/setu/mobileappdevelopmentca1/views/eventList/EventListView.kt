package ie.setu.mobileappdevelopmentca1.views.eventList

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.adapters.EventAdapter
import ie.setu.mobileappdevelopmentca1.adapters.EventListener
import ie.setu.mobileappdevelopmentca1.databinding.ActivityEventListBinding
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
import ie.setu.mobileappdevelopmentca1.views.auth.AuthView
import ie.setu.mobileappdevelopmentca1.views.event.EventView
import ie.setu.mobileappdevelopmentca1.views.map.EventMapView
class EventListView : AppCompatActivity(), EventListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityEventListBinding
    lateinit var presenter: EventListPresenter
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        binding.eventSV.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.handleSearch(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                presenter.handleSearch(query)
                return true
            }
        })

        //https://www.geeksforgeeks.org/android/navigation-drawer-in-android/
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

        presenter = EventListPresenter(this)
        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        adapter = EventAdapter(emptyList(), this)
        binding.recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.item_add -> { presenter.doAddEvent() }
//            R.id.item_map -> { presenter.doShowEventsMap() }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onEventClick(id: String, event: EventModel) {
        presenter.doEditEvent(id, event)
    }

    override fun onDeleteButtonClicked(id: String, event: EventModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { _, _ ->
                presenter.doDeleteEvent(id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //https://developer.android.com/guide/components/activities/activity-lifecycle
    override fun onStart() {
        super.onStart()
        presenter.startListening()
    }

    override fun onStop() {
        super.onStop()
        presenter.stopListening()
    }

    fun showError(message: String) {

    }

    fun loadEvents(list: List<Pair<String, EventModel>>) {
        adapter.submitList(list)
    }

//    fun onRefresh() {
//        (binding.recyclerView.adapter as EventAdapter).submitList(presenter.getEvents().toList())
//    }
}
